package fr.pederobien.mumble.client.impl;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

import fr.pederobien.communication.event.DataReceivedEvent;
import fr.pederobien.communication.event.LogEvent;
import fr.pederobien.communication.interfaces.IObsConnection;
import fr.pederobien.communication.interfaces.IUdpConnection;
import fr.pederobien.messenger.interfaces.IMessage;
import fr.pederobien.mumble.common.impl.Header;
import fr.pederobien.mumble.common.impl.Idc;
import fr.pederobien.mumble.common.impl.MumbleMessageFactory;
import fr.pederobien.mumble.common.impl.MumbleRequestMessage;
import fr.pederobien.utils.ByteWrapper;

public class AudioThread extends Thread implements IObsConnection {
	public static final AudioFormat FORMAT = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 2, 4, 44100, false);
	private static final int CHUNK_SIZE = 17640;
	private TargetDataLine microphone;
	private SourceDataLine speakers;
	private IUdpConnection connection;
	private AtomicBoolean isConnected;
	private Semaphore semaphore;
	private boolean isStarted, isDisconnectionRequested;

	public AudioThread(IUdpConnection connection) {
		super("AudioThread");
		this.connection = connection;
		connection.addObserver(this);

		isConnected = new AtomicBoolean(false);
		semaphore = new Semaphore(1, true);
		isStarted = false;
		isDisconnectionRequested = false;

		setDaemon(true);
	}

	@Override
	public synchronized void start() {
		if (isStarted)
			return;

		try {
			microphone = (TargetDataLine) AudioSystem.getLine(new DataLine.Info(TargetDataLine.class, FORMAT));
			speakers = (SourceDataLine) AudioSystem.getLine(new DataLine.Info(SourceDataLine.class, FORMAT));
			microphone.open(FORMAT);
			speakers.open(FORMAT);
			semaphore.acquire();
			super.start();
			isStarted = true;
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		int numBytesRead;
		byte[] data = new byte[microphone.getBufferSize() / 5];
		microphone.start();
		speakers.start();
		while (!isInterrupted()) {
			try {
				semaphore.acquire();
				numBytesRead = microphone.read(data, 0, CHUNK_SIZE);

				// The connection might be closed while waiting for the microphone data.
				if (connection.isDisposed())
					return;

				if (isDisconnectionRequested) {
					// Releasing the semaphore in order to operate the disconnection from the remote properly
					semaphore.release();
					// Giving time to the thread that ask for the disconnection to operate the disconnection.
					Thread.sleep(200);
					continue;
				}

				connection.send(new MumbleRequestMessage(MumbleMessageFactory.create(Idc.PLAYER_SPEAK, ByteWrapper.wrap(data).extract(0, numBytesRead))));
				semaphore.release();
			} catch (InterruptedException e) {
				break;
			}
		}
	}

	@Override
	public void onConnectionComplete() {

	}

	@Override
	public void onConnectionDisposed() {
		if (microphone != null && speakers != null) {
			microphone.stop();
			microphone.close();
			speakers.stop();
			speakers.close();
		}
		interrupt();
	}

	@Override
	public void onDataReceived(DataReceivedEvent event) {
		IMessage<Header> message = MumbleMessageFactory.parse(event.getBuffer());
		if (message.getHeader().getIdc() != Idc.PLAYER_SPEAK)
			return;
	}

	@Override
	public void onLog(LogEvent event) {

	}

	/**
	 * Stops the microphone and the speakers. Disconnect the connection. After calling this method, you can call {@link #connect()} in
	 * order to send data from the microphone.
	 */
	public void disconnect() {
		isDisconnectionRequested = true;

		if (!isConnected.get())
			return;

		try {
			semaphore.acquire();
			microphone.stop();
			speakers.stop();
			connection.disconnect();
			isConnected.set(false);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Start the microphone and the speaker. Connect the connection. After calling this method, you can call {@link #disconnect()} in
	 * order to stop sending data from the microphone.
	 */
	public void connect() {
		isDisconnectionRequested = false;

		if (!isStarted)
			start();

		if (isConnected.get())
			return;

		connection.connect();
		microphone.start();
		speakers.start();
		isConnected.set(true);
		semaphore.release();
	}
}
