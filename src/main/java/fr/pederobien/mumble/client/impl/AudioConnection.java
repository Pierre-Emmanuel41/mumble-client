package fr.pederobien.mumble.client.impl;

import fr.pederobien.communication.event.ConnectionCompleteEvent;
import fr.pederobien.communication.event.ConnectionDisposedEvent;
import fr.pederobien.communication.event.DataReceivedEvent;
import fr.pederobien.communication.impl.UdpClientConnection;
import fr.pederobien.communication.interfaces.IUdpConnection;
import fr.pederobien.messenger.interfaces.IMessage;
import fr.pederobien.mumble.common.impl.Header;
import fr.pederobien.mumble.common.impl.Idc;
import fr.pederobien.mumble.common.impl.MessageExtractor;
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

public class AudioConnection implements IEventListener {
	private ISoundResourcesProvider soundProvider;
	private IUdpConnection udpConnection;
	private boolean pauseMicrophone, pauseSpeakers;

	public AudioConnection(String remoteAddress, int udpPort) {
		udpConnection = new UdpClientConnection(remoteAddress, udpPort, new MessageExtractor(), true, 20000);
		EventManager.registerListener(this);
	}

	/**
	 * Connects the udp connection to the remote in order to send the bytes array coming from the microphone through the network.
	 */
	public void connect() {
		udpConnection.connect();
	}

	/**
	 * Stops the udp connection with the remote. It also interrupt the underlying microphone and speakers in order to release systems
	 * resources.
	 */
	public void disconnect() {
		soundProvider.getMicrophone().interrupt();
		soundProvider.getSpeakers().interrupt();
		udpConnection.disconnect();
	}

	/**
	 * Dispose the underlying udp connection.
	 */
	public void dispose() {
		udpConnection.dispose();
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
		if (udpConnection.isDisposed() || !event.getMicrophone().equals(soundProvider.getMicrophone()))
			return;

		udpConnection.send(new MumbleRequestMessage(MumbleMessageFactory.create(Idc.PLAYER_SPEAK, event.getEncoded())));
	}

	@EventHandler(priority = EventPriority.NORMAL)
	private void onConnectionComplete(ConnectionCompleteEvent event) {
		if (!event.getConnection().equals(udpConnection))
			return;

		start();
	}

	@EventHandler(priority = EventPriority.NORMAL)
	private void onConnectionDisposed(ConnectionDisposedEvent event) {
		if (!event.getConnection().equals(udpConnection))
			return;

		if (soundProvider != null) {
			soundProvider.getMicrophone().interrupt();
			soundProvider.getSpeakers().interrupt();
		}

		EventManager.unregisterListener(this);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	private void onDataReceived(DataReceivedEvent event) {
		if (!event.getConnection().equals(udpConnection))
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
