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
import fr.pederobien.communication.impl.BlockingQueueTask;
import fr.pederobien.communication.interfaces.IObsConnection;
import fr.pederobien.communication.interfaces.IUdpConnection;
import fr.pederobien.mumble.common.impl.Idc;
import fr.pederobien.mumble.common.impl.MumbleMessageFactory;
import fr.pederobien.mumble.common.impl.MumbleRequestMessage;
import fr.pederobien.utils.ByteWrapper;

public class AudioThread extends Thread implements IObsConnection {
	private static final int CHUNK_SIZE = 17640;
	private TargetDataLine microphone;
	private SourceDataLine speakers;
	private IUdpConnection connection;
	private AtomicBoolean isConnected;
	private Semaphore semaphore;
	private BlockingQueueTask<DataReceivedEvent> speakQueue;

	public AudioThread(IUdpConnection connection) {
		super("AudioThread");
		this.connection = connection;
		connection.addObserver(this);

		isConnected = new AtomicBoolean(false);
		semaphore = new Semaphore(1);
		speakQueue = new BlockingQueueTask<>("Speakers", event -> speak(event));

		setDaemon(true);
	}

	@Override
	public synchronized void start() {
		AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 2, 4, 44100, false);
		try {
			microphone = (TargetDataLine) AudioSystem.getLine(new DataLine.Info(TargetDataLine.class, format));
			speakers = (SourceDataLine) AudioSystem.getLine(new DataLine.Info(SourceDataLine.class, format));
			microphone.open(format);
			speakers.open(format);
			semaphore.acquire();
			super.start();
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
		speakQueue.start();
		while (!isInterrupted()) {
			try {
				semaphore.acquire();
				numBytesRead = microphone.read(data, 0, CHUNK_SIZE);

				// The connection might be closed while waiting for the microphone data.
				if (connection.isDisposed())
					return;

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
		speakQueue.dispose();
	}

	@Override
	public void onDataReceived(DataReceivedEvent event) {
		speakQueue.add(event);
	}

	@Override
	public void onLog(LogEvent event) {

	}

	/**
	 * Stops the microphone and the speakers. Disconnect the connection. After calling this method, you can call {@link #connect()} in
	 * order to send data from the microphone.
	 */
	public void disconnect() {
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
		if (isConnected.get())
			return;

		connection.connect();
		microphone.start();
		speakers.start();
		isConnected.set(true);
		semaphore.release();
	}

	private void speak(DataReceivedEvent event) {
		byte[] buffer = (byte[]) MumbleMessageFactory.parse(event.getBuffer()).getPayload()[0];
		speakers.write(buffer, 0, buffer.length);
	}
}
