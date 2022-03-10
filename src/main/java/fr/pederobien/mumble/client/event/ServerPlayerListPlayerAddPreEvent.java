package fr.pederobien.mumble.client.event;

import java.net.InetSocketAddress;
import java.util.StringJoiner;
import java.util.function.Consumer;

import fr.pederobien.mumble.client.interfaces.IResponse;
import fr.pederobien.mumble.client.interfaces.IServerPlayerList;
import fr.pederobien.utils.ICancellable;

public class ServerPlayerListPlayerAddPreEvent extends ServerPlayerListEvent implements ICancellable {
	private boolean isCancelled;
	private String name;
	private InetSocketAddress gameAddress;
	private boolean isAdmin, isMute, isDeafen;
	private double x, y, z, yaw, pitch;
	private Consumer<IResponse> callback;

	/**
	 * Creates an event thrown when a player is about to be created and registered on the server.
	 * 
	 * @param list        The list to which the player will be added.
	 * @param name        The player's name.
	 * @param gameAddress The game address used to play to the game.
	 * @param isAdmin     The player's administrator status.
	 * @param isMute      The player's mute status.
	 * @param isDeafen    The player's deafen status.
	 * @param x           The player's x coordinate.
	 * @param y           The player's y coordinate.
	 * @param z           The player's z coordinate.
	 * @param yaw         The player's yaw angle.
	 * @param pitch       The player's pitch angle.
	 * @param callback    The callback to run when an answer is received from the server.
	 */
	public ServerPlayerListPlayerAddPreEvent(IServerPlayerList list, String name, InetSocketAddress gameAddress, boolean isAdmin, boolean isMute, boolean isDeafen,
			double x, double y, double z, double yaw, double pitch, Consumer<IResponse> callback) {
		super(list);
		this.name = name;
		this.gameAddress = gameAddress;
		this.isAdmin = isAdmin;
		this.isMute = isMute;
		this.isDeafen = isDeafen;
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
		this.callback = callback;
	}

	@Override
	public boolean isCancelled() {
		return isCancelled;
	}

	@Override
	public void setCancelled(boolean isCancelled) {
		this.isCancelled = isCancelled;
	}

	/**
	 * @return The player's name.
	 */
	public String getPlayerName() {
		return name;
	}

	/**
	 * @return The address used to play to the game.
	 */
	public InetSocketAddress getGameAddress() {
		return gameAddress;
	}

	/**
	 * @return The player's administrator status.
	 */
	public boolean isAdmin() {
		return isAdmin;
	}

	/**
	 * @return The player's mute status.
	 */
	public boolean isMute() {
		return isMute;
	}

	/**
	 * @return The player's deafen status.
	 */
	public boolean isDeafen() {
		return isDeafen;
	}

	/**
	 * @return The player's x coordinate.
	 */
	public double getX() {
		return x;
	}

	/**
	 * @return The player's y coordinate.
	 */
	public double getY() {
		return y;
	}

	/**
	 * @return The player's z coordinate.
	 */
	public double getZ() {
		return z;
	}

	/**
	 * @return The player's yaw angle.
	 */
	public double getYaw() {
		return yaw;
	}

	/**
	 * @return The player's pitch angle.
	 */
	public double getPitch() {
		return pitch;
	}

	/**
	 * @return The callback to run when an answer is received from the server.
	 */
	public Consumer<IResponse> getCallback() {
		return callback;
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(", ", "{", "}");
		joiner.add("list=" + getList().getName());
		joiner.add("name=" + getPlayerName());
		joiner.add("gameAddress=" + getGameAddress());
		joiner.add("isAdmin=" + isAdmin());
		joiner.add("isMute=" + isMute());
		joiner.add("isDeafen=" + isDeafen());
		joiner.add("x=" + getX());
		joiner.add("y=" + getY());
		joiner.add("z=" + getZ());
		joiner.add("yaw=" + getYaw());
		joiner.add("pitch=" + getPitch());
		return String.format("%s_%s", getName(), joiner);
	}
}
