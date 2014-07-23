package de.wag_web.jubp2.interfaces;

/**
 * Manage the general connections and organization of the clients. 
 * <p/>
 * @author held03
 */
public interface Server {
	
	/**
	 * Starts the server.
	 * <p/>
	 * This means that the server starts listening on the server port.
	 * <p/>
	 * Notic that this will only work on a new server instance.
	 * If it was already stated this method will fail, also if it was stopped with {@link #stopConnection()}.
	 */
	public void startConnection();
	
	/**
	 * Stops the server.
	 * <p/>
	 * This will break all connections to the server, all related threads are interrupted.
	 */
	public void stopConnection();
	
	/**
	 * Broadcasts a message to all clients.
	 */
	public void broadcast(msg);
	
	/**
	 * Gets all connections to clients.
	 */
	public List getConnections();
	
	/**
	 * Adds a listener for this server.
	 * <p/>
	 * @param listener the listener to add
	 */
	public void addListener(ServerListener listener);
	
	/**
	 * Removes a listener from this server.
	 * <p/>
	 * @param listener the listener to remove
	 */
	public void removeListener(ServerListener listener);
}
