package fr.pederobien.mumble.client.impl.request;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import fr.pederobien.mumble.client.interfaces.IMumbleServer;
import fr.pederobien.mumble.common.impl.Idc;
import fr.pederobien.mumble.common.impl.Oid;
import fr.pederobien.mumble.common.impl.messages.v10.ChannelsPlayerRemoveMessageV10;
import fr.pederobien.mumble.common.impl.messages.v10.PlayerKickSetMessageV10;
import fr.pederobien.mumble.common.impl.messages.v10.PlayerPositionGetMessageV10;
import fr.pederobien.mumble.common.impl.messages.v10.PlayerPositionSetMessageV10;
import fr.pederobien.mumble.common.impl.messages.v10.PlayerSetMessageV10;
import fr.pederobien.mumble.common.impl.messages.v10.SoundModifierGetMessageV10;
import fr.pederobien.mumble.common.impl.messages.v10.SoundModifierInfoMessageV10;
import fr.pederobien.mumble.common.impl.messages.v10.SoundModifierSetMessageV10;
import fr.pederobien.mumble.common.interfaces.IMumbleMessage;

public abstract class RequestServerManagement {
	private IMumbleServer server;
	private Map<Idc, Map<Oid, Consumer<IMumbleMessage>>> requests;

	/**
	 * Creates a request management in order to modify the given server and answer to remote requests.
	 * 
	 * @param server The server to update.
	 */
	public RequestServerManagement(IMumbleServer server) {
		this.server = server;
		requests = new HashMap<Idc, Map<Oid, Consumer<IMumbleMessage>>>();
	}

	/**
	 * Run a specific treatment associated to the given request.
	 * 
	 * @param request The request sent by the remote.
	 */
	public void apply(IMumbleMessage request) {
		Map<Oid, Consumer<IMumbleMessage>> map = requests.get(request.getHeader().getIdc());

		if (map == null)
			return;

		Consumer<IMumbleMessage> answer = map.get(request.getHeader().getOid());
		if (answer == null)
			return;

		answer.accept(request);
	}

	/**
	 * @return The map that stores requests.
	 */
	public Map<Idc, Map<Oid, Consumer<IMumbleMessage>>> getRequests() {
		return requests;
	}

	/**
	 * @return The server to update.
	 */
	protected IMumbleServer getServer() {
		return server;
	}

	/**
	 * Update the statuses of a specific player.
	 * 
	 * @param request The request sent by the remote.
	 */
	protected abstract void playerInfoSet(PlayerSetMessageV10 request);

	/**
	 * Removes a player from a channel.
	 * 
	 * @param request The request sent by the remote.
	 */
	protected abstract void channelsPlayerRemove(ChannelsPlayerRemoveMessageV10 request);

	/**
	 * Kicks a player from a channel.
	 * 
	 * @param request The request sent by the remote.
	 */
	protected abstract void playerKickSet(PlayerKickSetMessageV10 request);

	/**
	 * Get the position of a player.
	 * 
	 * @param request The request sent by the remote.
	 */
	protected abstract void playerPositionGet(PlayerPositionGetMessageV10 request);

	/**
	 * Set the position of a player.
	 * 
	 * @param request The request sent by the remote.
	 */
	protected abstract void playerPositionSet(PlayerPositionSetMessageV10 request);

	/**
	 * Get or update the sound modifier of a channel.
	 * 
	 * @param request The request sent by the remote.
	 */
	protected abstract void soundModifierGet(SoundModifierGetMessageV10 request);

	/**
	 * Set the sound modifier of a channel.
	 * 
	 * @param request The request sent by the remote.
	 */
	protected abstract void soundModifierSet(SoundModifierSetMessageV10 request);

	/**
	 * Get a description of each sound modifier registered in the {@link SoundManager}.
	 * 
	 * @param request The request sent by the remote.
	 */
	protected abstract void soundModifierInfo(SoundModifierInfoMessageV10 request);
}
