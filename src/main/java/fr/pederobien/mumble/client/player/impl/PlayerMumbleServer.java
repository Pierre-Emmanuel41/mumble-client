package fr.pederobien.mumble.client.player.impl;

import fr.pederobien.utils.event.IEventListener;

public class PlayerMumbleServer implements IEventListener {
	/*
	 * private Lock lock; private Condition serverConfiguration, communicationProtocolVersion; private boolean
	 * serverConfigurationRequestSuccess; private AtomicBoolean isJoined;
	 * 
	 * public PlayerMumbleServer(String name, InetSocketAddress address) { super(name, address);
	 * 
	 * lock = new ReentrantLock(true); serverConfiguration = lock.newCondition(); communicationProtocolVersion = lock.newCondition();
	 * 
	 * isJoined = new AtomicBoolean(false);
	 * 
	 * EventManager.registerListener(this); }
	 * 
	 * @Override public void open() { // Do not try to open the server when already opened if (isOpened.get()) return;
	 * 
	 * super.open();
	 * 
	 * lock.lock(); try { if (!communicationProtocolVersion.await(5000, TimeUnit.MILLISECONDS)) {
	 * getMumbleConnection().getTcpConnection().dispose(); throw new
	 * IllegalStateException("Time out on establishing the version of the communication protocol."); } } catch (InterruptedException
	 * e) { // Do nothing } finally { lock.unlock(); } }
	 * 
	 * @Override public void join(Consumer<IResponse> callback) { if (!isJoined.compareAndSet(false, true)) return;
	 * 
	 * Consumer<IResponse> update = response -> { if (!response.hasFailed()) EventManager.callEvent(new ServerJoinPostEvent(this));
	 * callback.accept(response); }; EventManager.callEvent(new ServerJoinPreEvent(this, update));
	 * 
	 * lock.lock(); try { serverConfigurationRequestSuccess = false; if (!serverConfiguration.await(5000, TimeUnit.MILLISECONDS)) {
	 * isJoined.set(false); getMumbleConnection().getTcpConnection().dispose(); throw new
	 * IllegalStateException("Time out on server configuration request."); } if (serverConfigurationRequestSuccess) {
	 * isJoined.set(false); getMumbleConnection().getTcpConnection().dispose(); throw new
	 * IllegalStateException("Technical error: Fail to retrieve the server configuration"); }
	 * 
	 * isJoined.set(true); } catch (InterruptedException e) { // Do nothing } finally { lock.unlock(); } }
	 * 
	 * @Override public void leave(Consumer<IResponse> callback) { if (!isJoined.compareAndSet(true, false)) return;
	 * 
	 * Consumer<IResponse> update = response -> { if (!response.hasFailed()) EventManager.callEvent(new ServerLeavePostEvent(this));
	 * callback.accept(response); }; EventManager.callEvent(new ServerLeavePreEvent(this, update)); }
	 * 
	 * @Override public boolean isJoined() { return isJoined.get(); }
	 * 
	 * @EventHandler private void onConnectionComplete(ConnectionCompleteEvent event) { if
	 * (!event.getConnection().equals(getMumbleConnection().getTcpConnection())) return;
	 * 
	 * setIsReachable(true); }
	 * 
	 * @EventHandler private void onSetCommunicationProtocolVersion(CommunicationProtocolVersionSetPostEvent event) { if
	 * (!event.getConnection().equals(getMumbleConnection())) return;
	 * 
	 * lock.lock(); try { communicationProtocolVersion.signal(); } finally { lock.unlock(); } }
	 * 
	 * @EventHandler private void onServerJoin(ServerJoinPostEvent event) { if (!event.getServer().equals(this)) return;
	 * 
	 * Consumer<IResponse> callback = response -> { serverConfigurationRequestSuccess = response.hasFailed(); lock.lock(); try {
	 * serverConfiguration.signal(); } finally { lock.unlock(); } };
	 * 
	 * getMumbleConnection().getFullServerConfiguration(callback); }
	 * 
	 * @EventHandler private void onServerLeave(ServerLeavePostEvent event) { if (!event.getServer().equals(this)) return;
	 * 
	 * isJoined.set(false); clear(); }
	 * 
	 * @EventHandler private void onConnectionDisposed(ConnectionDisposedEvent event) { if
	 * (!event.getConnection().equals(getMumbleConnection().getTcpConnection())) return;
	 * 
	 * setIsReachable(false); EventManager.unregisterListener(this); }
	 * 
	 * @EventHandler private void onConnectionLost(ConnectionLostEvent event) { if
	 * (!event.getConnection().equals(getMumbleConnection().getTcpConnection())) return;
	 * 
	 * setIsReachable(false); clear(); }
	 */
}
