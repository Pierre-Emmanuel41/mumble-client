package fr.pederobien.mumble.client.impl;

import java.util.concurrent.atomic.AtomicBoolean;

import fr.pederobien.communication.event.DataReceivedEvent;
import fr.pederobien.communication.event.LogEvent;
import fr.pederobien.communication.interfaces.IObsConnection;
import fr.pederobien.communication.interfaces.IUdpConnection;
import fr.pederobien.messenger.interfaces.IMessage;
import fr.pederobien.mumble.client.interfaces.IAudioConnection;
import fr.pederobien.mumble.client.interfaces.observers.IObsMicrophone;
import fr.pederobien.mumble.common.impl.Header;
import fr.pederobien.mumble.common.impl.Idc;
import fr.pederobien.mumble.common.impl.MumbleMessageFactory;
import fr.pederobien.mumble.common.impl.MumbleRequestMessage;
import fr.pederobien.mumble.common.impl.Oid;
import fr.pederobien.utils.ByteWrapper;

public class AudioConnection implements IAudioConnection, IObsMicrophone, IObsConnection {
	private IUdpConnection connection;
	private Microphone microphone;
	private AtomicBoolean isConnected;
	private Mixer mixer;
	private Speakers speakers;
	private boolean pauseMicrophone, pauseSpeakers;

	public AudioConnection(IUdpConnection connection) {
		this.connection = connection;
		connection.addObserver(this);

		isConnected = new AtomicBoolean(false);
	}

	@Override
	public void onConnectionComplete() {
		start();
	}

	@Override
	public void onConnectionDisposed() {
		if (microphone != null)
			microphone.interrupt();

		if (speakers != null)
			speakers.interrupt();
	}

	@Override
	public void onDataReceived(DataReceivedEvent event) {
		IMessage<Header> message = MumbleMessageFactory.parse(event.getBuffer());
		if (message.getHeader().getIdc() != Idc.PLAYER_SPEAK || message.getHeader().getOid() != Oid.SET)
			return;

		mixer.put(message);
	}

	@Override
	public void onLog(LogEvent event) {

	}

	@Override
	public void onDataRead(byte[] data, int size) {
		byte[] buffer = data;
		if (data.length != size)
			buffer = ByteWrapper.wrap(data).extract(0, size);

		if (connection.isDisposed())
			return;

		connection.send(new MumbleRequestMessage(MumbleMessageFactory.create(Idc.PLAYER_SPEAK, buffer)));
	}

	@Override
	public void connect() {
		if (!isConnected.compareAndSet(false, true))
			return;

		connection.connect();
	}

	@Override
	public void disconnect() {
		if (!isConnected.compareAndSet(true, false))
			return;

		microphone.removeObserver(this);
		microphone.interrupt();
		speakers.interrupt();
		connection.disconnect();
	}

	@Override
	public void pause() {
		pauseMicrophone();
		pauseSpeakers();
	}

	@Override
	public void pauseMicrophone() {
		if (pauseMicrophone)
			return;

		pauseMicrophone = true;
		microphone.pause();
	}

	@Override
	public void pauseSpeakers() {
		if (pauseSpeakers)
			return;

		pauseSpeakers = true;
		speakers.pause();
	}

	@Override
	public void resume() {
		resumeMicrophone();
		resumeSpeakers();
	}

	@Override
	public void resumeMicrophone() {
		if (!pauseMicrophone)
			return;

		pauseMicrophone = false;
		microphone.relaunch();
	}

	@Override
	public void resumeSpeakers() {
		if (!pauseSpeakers)
			return;

		pauseSpeakers = false;
		speakers.relaunch();
	}

	private void start() {
		microphone = new Microphone();
		microphone.addObserver(this);
		microphone.start();

		mixer = new Mixer();
		speakers = new Speakers(mixer);
		speakers.start();
	}
}
