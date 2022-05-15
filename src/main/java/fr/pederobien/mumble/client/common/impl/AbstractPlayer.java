package fr.pederobien.mumble.client.common.impl;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import fr.pederobien.mumble.client.common.interfaces.ICommonPlayer;

public abstract class AbstractPlayer implements ICommonPlayer {
	private String name;
	private AtomicBoolean isAdmin, isOnline, isMute, isDeafen;
	private Lock lock;

	/**
	 * Creates a player associated to a name.
	 * 
	 * @param name The player name.
	 */
	protected AbstractPlayer(String name) {
		this.name = name;

		isAdmin = new AtomicBoolean(false);
		isOnline = new AtomicBoolean(false);
		isMute = new AtomicBoolean(false);
		isDeafen = new AtomicBoolean(false);
		lock = new ReentrantLock(true);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean isAdmin() {
		return isAdmin.get();
	}

	@Override
	public boolean isOnline() {
		return isOnline.get();
	}

	@Override
	public boolean isMute() {
		return isMute.get();
	}

	@Override
	public boolean isDeafen() {
		return isDeafen.get();
	}

	/**
	 * @return The lock associated to this player.
	 */
	protected Lock getLock() {
		return lock;
	}

	/**
	 * Set the name of this player.
	 * 
	 * @param name The new name of this player.
	 */
	protected void setName0(String name) {
		this.name = name;
	}

	/**
	 * Set the administrator status of this player.
	 * 
	 * @param isAdmin The new administrator status of this player.
	 * 
	 * @return True if the administrator status has changed, false otherwise.
	 */
	protected boolean setAdmin0(boolean isAdmin) {
		return this.isAdmin.compareAndSet(!isAdmin, isAdmin);
	}

	/**
	 * Set the online status of this player.
	 * 
	 * @param isOnline The new online status of this player.
	 * 
	 * @return True if the online status has changed, false otherwise.
	 */
	protected boolean setOnline0(boolean isOnline) {
		return this.isOnline.compareAndSet(!isOnline, isOnline);
	}

	/**
	 * Set the mute status of this player.
	 * 
	 * @param isMute The new mute status of this player.
	 * 
	 * @return True if the mute status has changed, false otherwise.
	 */
	protected boolean setMute0(boolean isMute) {
		return this.isMute.compareAndSet(!isMute, isMute);
	}

	/**
	 * Set the deafen status of this player.
	 * 
	 * @param isDeafen The new deafen status of this player.
	 * 
	 * @return True if the deafen status has changed, false otherwise.
	 */
	protected boolean setDeafen0(boolean isDeafen) {
		return this.isDeafen.compareAndSet(!isDeafen, isDeafen);
	}
}
