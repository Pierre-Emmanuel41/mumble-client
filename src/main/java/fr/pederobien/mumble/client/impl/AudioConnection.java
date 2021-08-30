package fr.pederobien.mumble.client.impl;

import java.util.concurrent.atomic.AtomicBoolean;

import fr.pederobien.communication.event.DataReceivedEvent;
import fr.pederobien.communication.event.LogEvent;
import fr.pederobien.communication.interfaces.IObsConnection;
import fr.pederobien.communication.interfaces.IUdpConnection;
import fr.pederobien.messenger.interfaces.IMessage;
import fr.pederobien.mumble.client.interfaces.IAudioConnection;
import fr.pederobien.mumble.client.interfaces.observers.IObsAudioConnection;
import fr.pederobien.mumble.common.impl.Header;
import fr.pederobien.mumble.common.impl.Idc;
import fr.pederobien.mumble.common.impl.MumbleMessageFactory;
import fr.pederobien.mumble.common.impl.MumbleRequestMessage;
import fr.pederobien.mumble.common.impl.Oid;
import fr.pederobien.sound.event.MicrophoneDataReadEvent;
import fr.pederobien.sound.impl.SoundResourcesProvider;
import fr.pederobien.sound.interfaces.ISoundResourcesProvider;
import fr.pederobien.utils.Observable;
import fr.pederobien.utils.event.EventHandler;
import fr.pederobien.utils.event.EventManager;
import fr.pederobien.utils.event.EventPriority;
import fr.pederobien.utils.event.IEventListener;

public class AudioConnection implements IAudioConnection, IEventListener, IObsConnection {
	private static int N_SHORTS = 0xffff;
	private static final short[] VOLUME_NORM_LUT = new short[N_SHORTS];
	private static int MAX_NEGATIVE_AMPLITUDE = 0x8000;

	private ISoundResourcesProvider soundProvider;
	private IUdpConnection connection;
	private AtomicBoolean isConnected;
	private boolean pauseMicrophone, pauseSpeakers;
	private Observable<IObsAudioConnection> observers;

	public AudioConnection(IUdpConnection connection) {
		this.connection = connection;
		connection.addObserver(this);

		isConnected = new AtomicBoolean(false);
		observers = new Observable<IObsAudioConnection>();
		preComputeVolumeNormLUT();
	}

	@Override
	public void addObserver(IObsAudioConnection obs) {
		observers.addObserver(obs);
	}

	@Override
	public void removeObserver(IObsAudioConnection obs) {
		observers.removeObserver(obs);
	}

	@Override
	public void onConnectionComplete() {
		start();
	}

	@Override
	public void onConnectionDisposed() {
		if (soundProvider != null) {
			soundProvider.getMicrophone().interrupt();
			soundProvider.getSpeakers().interrupt();
		}
		connection.removeObserver(this);
	}

	@Override
	public void onDataReceived(DataReceivedEvent event) {
		IMessage<Header> message = MumbleMessageFactory.parse(event.getBuffer());
		if (pauseSpeakers || message.getHeader().getIdc() != Idc.PLAYER_SPEAK || message.getHeader().getOid() != Oid.SET)
			return;

		soundProvider.getMixer().put((String) message.getPayload()[0], toStereo(message), (double) message.getPayload()[2], false);
	}

	@Override
	public void onLog(LogEvent event) {

	}

	@Override
	public void connect() {
		if (!isConnected.compareAndSet(false, true))
			return;

		connection.connect();
		observers.notifyObservers(obs -> obs.onAudioConnect());
	}

	@Override
	public void disconnect() {
		if (!isConnected.compareAndSet(true, false))
			return;

		EventManager.unregisterListener(this);
		soundProvider.getMicrophone().interrupt();
		soundProvider.getSpeakers().interrupt();
		connection.disconnect();
		observers.notifyObservers(obs -> obs.onAudioDisconnect());
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
		observers.notifyObservers(obs -> obs.onPauseMicrophone());
	}

	/**
	 * Pause the soundProvider.getSpeakers(), data are received from the remote but no played by the soundProvider.getSpeakers().
	 */
	public void pauseSpeakers() {
		if (pauseSpeakers)
			return;

		pauseSpeakers = true;
		soundProvider.getSpeakers().pause();
		observers.notifyObservers(obs -> obs.onPauseSpeakers());
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
		observers.notifyObservers(obs -> obs.onResumeMicrophone());
	}

	/**
	 * Resume the soundProvider.getSpeakers(), data are received from the remote and played by the soundProvider.getSpeakers().
	 */
	public void resumeSpeakers() {
		if (!pauseSpeakers)
			return;

		pauseSpeakers = false;
		soundProvider.getSpeakers().relaunch();
		observers.notifyObservers(obs -> obs.onResumeSpeakers());
	}

	private void start() {
		soundProvider = new SoundResourcesProvider();
		EventManager.registerListener(this);
		soundProvider.getMicrophone().start();
		soundProvider.getSpeakers().start();

		pauseMicrophone = false;
		pauseSpeakers = false;
	}

	private void normalizeVolume(byte[] audioSamples) {
		for (int i = 0; i < audioSamples.length; i += 2) {
			short res = (short) ((audioSamples[i + 1] & 0xff) << 8 | audioSamples[i] & 0xff);

			res = VOLUME_NORM_LUT[Math.min(res + MAX_NEGATIVE_AMPLITUDE, N_SHORTS - 1)];
			audioSamples[i] = (byte) res;
			audioSamples[i + 1] = (byte) (res >> 8);
		}
	}

	private void preComputeVolumeNormLUT() {
		for (int s = 0; s < N_SHORTS; s++) {
			double v = s - MAX_NEGATIVE_AMPLITUDE;
			double sign = Math.signum(v);
			// Non-linear volume boost function
			// fitted exponential through (0,0), (10000, 25000), (32767, 32767)
			VOLUME_NORM_LUT[s] = (short) (sign * (1.240769e-22 - (-4.66022 / 0.0001408133) * (1 - Math.exp(-0.0001408133 * v * sign))));
		}
	}

	private byte[] toStereo(IMessage<Header> message) {
		byte[] buffer = (byte[]) message.getPayload()[1];
		double left = (double) message.getPayload()[3];
		double right = (double) message.getPayload()[4];

		byte[] data = new byte[buffer.length * 2];
		int index = 0;
		for (int i = 0; i < buffer.length; i += 2) {
			short initialShort = (short) ((buffer[i + 1] & 0xff) << 8 | buffer[i] & 0xff);
			short leftResult = (short) (((double) initialShort) * left);
			short rightResult = (short) (((double) initialShort) * right);

			data[index++] = (byte) leftResult;
			data[index++] = (byte) (leftResult >> 8);
			data[index++] = (byte) rightResult;
			data[index++] = (byte) (rightResult >> 8);
		}

		return data;
	}

	@EventHandler(priority = EventPriority.NORMAL)
	private void onMicrophoneDataRead(MicrophoneDataReadEvent event) {
		if (connection.isDisposed() || !event.getMicrophone().equals(soundProvider.getMicrophone()))
			return;

		normalizeVolume(event.getData());
		connection.send(new MumbleRequestMessage(MumbleMessageFactory.create(Idc.PLAYER_SPEAK, event.getData())));
	}
}
