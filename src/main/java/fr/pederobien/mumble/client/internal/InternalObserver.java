package fr.pederobien.mumble.client.internal;

import java.util.function.Consumer;

import fr.pederobien.communication.NonBlockingConsole;
import fr.pederobien.communication.event.DataReceivedEvent;
import fr.pederobien.communication.event.LogEvent;
import fr.pederobien.communication.event.UnexpectedDataReceivedEvent;
import fr.pederobien.communication.interfaces.IObsTcpConnection;
import fr.pederobien.messenger.interfaces.IMessage;
import fr.pederobien.mumble.client.impl.MumbleConnection;
import fr.pederobien.mumble.client.interfaces.observers.IObsMumbleConnection;
import fr.pederobien.mumble.common.impl.Header;
import fr.pederobien.mumble.common.impl.MumbleMessageFactory;
import fr.pederobien.utils.IObservable;
import fr.pederobien.utils.Observable;

public class InternalObserver implements IObservable<IObsMumbleConnection>, IObsTcpConnection {
	private MumbleConnection connection;
	private InternalPlayer player;
	private InternalChannelList channelList;
	private Observable<IObsMumbleConnection> observers;
	private boolean ignoreChannelModifications;

	public InternalObserver(MumbleConnection connection, InternalPlayer player, InternalChannelList channelList) {
		this.connection = connection;
		this.player = player;
		this.channelList = channelList;

		observers = new Observable<IObsMumbleConnection>();
		ignoreChannelModifications = true;
	}

	@Override
	public void addObserver(IObsMumbleConnection obs) {
		observers.addObserver(obs);
	}

	@Override
	public void removeObserver(IObsMumbleConnection obs) {
		observers.removeObserver(obs);
	}

	@Override
	public void onConnectionComplete() {
		connection.getUdpPort();
		notifyObservers(obs -> obs.onConnectionComplete());
	}

	@Override
	public void onConnectionDisposed() {
		notifyObservers(obs -> obs.onConnectionDisposed());
	}

	@Override
	public void onConnectionLost() {
		notifyObservers(obs -> obs.onConnectionLost());
	}

	@Override
	public void onDataReceived(DataReceivedEvent event) {

	}

	@Override
	public void onLog(LogEvent event) {
		NonBlockingConsole.println(event.getMessage());
	}

	@Override
	public void onUnexpectedDataReceived(UnexpectedDataReceivedEvent event) {
		if (player == null)
			return;

		IMessage<Header> message = MumbleMessageFactory.parse(event.getAnswer());
		switch (message.getHeader().getIdc()) {
		case PLAYER_STATUS:
			if (message.getPayload().length > 1)
				player.setName((String) message.getPayload()[1]);
			player.setIsOnline((boolean) message.getPayload()[0]);
			break;
		case PLAYER_ADMIN:
			player.setIsAdmin((boolean) message.getPayload()[0]);
			break;
		case CHANNELS:
			if (ignoreChannelModifications)
				return;

			switch (message.getHeader().getOid()) {
			case ADD:
				channelList.internalAdd(new InternalChannel(connection, (String) message.getPayload()[0]));
				break;
			case REMOVE:
				channelList.internalRemove((String) message.getPayload()[0]);
				break;
			case SET:
				String oldName = (String) message.getPayload()[0];
				String newName = (String) message.getPayload()[1];
				channelList.getChannel(oldName).internalSetName(newName);
			default:
				break;
			}
			break;
		case CHANNELS_PLAYER:
			if (ignoreChannelModifications)
				return;

			String channelName, playerName;
			switch (message.getHeader().getOid()) {
			case ADD:
				channelName = (String) message.getPayload()[0];
				playerName = (String) message.getPayload()[1];
				channelList.getChannel(channelName).internalAddPlayer(playerName);
				break;
			case REMOVE:
				channelName = (String) message.getPayload()[0];
				playerName = (String) message.getPayload()[1];
				channelList.getChannel(channelName).internalRemovePlayer(playerName);
				break;
			default:
				break;
			}
			break;
		case PLAYER_MUTE:
			playerName = (String) message.getPayload()[0];
			boolean isMute = (boolean) message.getPayload()[1];

			// In order to update the PlayerView
			if (player.getName().equals(playerName))
				player.internalSetMute(isMute);

			channelList.onPlayerMuteChanged(playerName, isMute);
			break;
		default:
			break;
		}
	}

	/**
	 * Set to true in order to ignore when a channel is added, removed or renamed and when a player is added or removed from a
	 * channel.
	 * 
	 * @param ignoreChannelModifications True ignore channel modifications, false otherwise.
	 */
	public void setIgnoreChannelModifications(boolean ignoreChannelModifications) {
		this.ignoreChannelModifications = ignoreChannelModifications;
	}

	private void notifyObservers(Consumer<IObsMumbleConnection> consumer) {
		observers.notifyObservers(consumer);
	}
}
