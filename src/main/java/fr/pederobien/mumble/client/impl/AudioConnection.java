package fr.pederobien.mumble.client.impl;

import java.util.concurrent.atomic.AtomicBoolean;

import fr.pederobien.communication.event.ConnectionCompleteEvent;
import fr.pederobien.communication.event.ConnectionDisposedEvent;
import fr.pederobien.communication.event.DataReceivedEvent;
import fr.pederobien.messenger.interfaces.IMessage;
import fr.pederobien.mumble.client.interfaces.IAudioConnection;
import fr.pederobien.mumble.common.impl.Header;
import fr.pederobien.mumble.common.impl.Idc;
import fr.pederobien.mumble.common.impl.MumbleMessageFactory;
import fr.pederobien.mumble.common.impl.MumbleRequestMessage;
import fr.pederobien.mumble.common.impl.Oid;
import fr.pederobien.sound.event.MicrophoneDataEncodedEvent;
import fr.pederobien.sound.impl.AudioPacket;
import fr.pederobien.sound.impl.SoundResourcesProvider;
import fr.pederobien.sound.interfaces.ISoundResourcesProvider;
import fr.pederobien.utils.event.EventHandler;
import fr.pederobien.utils.event.EventManager;
import fr.pederobien.utils.event.EventPriority;
import fr.pederobien.utils.event.IEventListener;

public class AudioConnection implements IAudioConnection, IEventListener {
	private ISoundResourcesProvider soundProvider;
	private MumbleConnection mumbleConnection;
	private AtomicBoolean isConnected;
	private boolean pauseMicrophone, pauseSpeakers;

	public AudioConnection(MumbleConnection mumbleConnection) {
		this.mumbleConnection = mumbleConnection;
		isConnected = new AtomicBoolean(false);
		EventManager.registerListener(this);
	}

	@Override
	public void connect() {
		if (!isConnected.compareAndSet(false, true))
			return;

		mumbleConnection.getUdpConnection().connect();
	}

	@Override
	public void disconnect() {
		if (!isConnected.compareAndSet(true, false))
			return;

		soundProvider.getMicrophone().interrupt();
		soundProvider.getSpeakers().interrupt();
		mumbleConnection.getUdpConnection().disconnect();
	}

	/**
	 * Stops the soundProvider.getMicrophone() and the soundProvider.getSpeakers(). But does not disconnected the internal connection
	 * from the remote.
	 */
	public void pause() {
		pauseMicrophone();
		pauseSpeakers();
	}

	/**
	 * Pause the soundProvider.getMicrophone(), no data are sent to the remote.
	 */
	public void pauseMicrophone() {
		if (pauseMicrophone)
			return;

		pauseMicrophone = true;
		soundProvider.getMicrophone().pause();
	}

	/**
	 * Pause the soundProvider.getSpeakers(), data are received from the remote but no played by the soundProvider.getSpeakers().
	 */
	public void pauseSpeakers() {
		if (pauseSpeakers)
			return;

		pauseSpeakers = true;
		soundProvider.getSpeakers().pause();
	}

	/**
	 * Resumes the soundProvider.getMicrophone() and the soundProvider.getSpeakers() in order to send again data to the remote and
	 * receive data from the remote.
	 */
	public void resume() {
		resumeMicrophone();
		resumeSpeakers();
	}

	/**
	 * Resume the soundProvider.getMicrophone(), data are sent to the remote.
	 */
	public void resumeMicrophone() {
		if (!pauseMicrophone)
			return;

		pauseMicrophone = false;
		soundProvider.getMicrophone().relaunch();
	}

	/**
	 * Resume the soundProvider.getSpeakers(), data are received from the remote and played by the soundProvider.getSpeakers().
	 */
	public void resumeSpeakers() {
		if (!pauseSpeakers)
			return;

		pauseSpeakers = false;
		soundProvider.getSpeakers().relaunch();
	}

	private void start() {
		soundProvider = new SoundResourcesProvider();
		soundProvider.getMicrophone().start();
		soundProvider.getSpeakers().start();

		pauseMicrophone = false;
		pauseSpeakers = false;
	}

	@EventHandler(priority = EventPriority.NORMAL)
	private void onMicrophoneDataRead(MicrophoneDataEncodedEvent event) {
		if (mumbleConnection.getUdpConnection().isDisposed() || !event.getMicrophone().equals(soundProvider.getMicrophone()))
			return;

		mumbleConnection.getUdpConnection().send(new MumbleRequestMessage(MumbleMessageFactory.create(Idc.PLAYER_SPEAK, event.getEncoded())));
	}

	@EventHandler(priority = EventPriority.NORMAL)
	private void onConnectionComplete(ConnectionCompleteEvent event) {
		if (!event.getConnection().equals(mumbleConnection.getUdpConnection()))
			return;

		start();
	}

	@EventHandler(priority = EventPriority.NORMAL)
	private void onConnectionDisposed(ConnectionDisposedEvent event) {
		if (!event.getConnection().equals(mumbleConnection.getUdpConnection()))
			return;

		if (soundProvider != null) {
			soundProvider.getMicrophone().interrupt();
			soundProvider.getSpeakers().interrupt();
		}

		EventManager.unregisterListener(this);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	private void onDataReceived(DataReceivedEvent event) {
		if (!event.getConnection().equals(mumbleConnection.getUdpConnection()))
			return;

		IMessage<Header> message = MumbleMessageFactory.parse(event.getBuffer());
		if (pauseSpeakers || message.getHeader().getIdc() != Idc.PLAYER_SPEAK || message.getHeader().getOid() != Oid.SET)
			return;

		String playerName = (String) message.getPayload()[0];
		byte[] encodedData = (byte[]) message.getPayload()[1];
		double globalVolume = (double) message.getPayload()[2];
		double leftVolume = (double) message.getPayload()[3];
		double rightVolume = (double) message.getPayload()[4];
		AudioPacket packet = new AudioPacket(playerName, encodedData, globalVolume, rightVolume, leftVolume, true, true);
		soundProvider.getMixer().put(packet);
	}
}
