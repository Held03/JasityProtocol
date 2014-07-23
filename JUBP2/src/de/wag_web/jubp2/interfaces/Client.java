package de.wag_web.jubp2.interfaces;

/**
 * Manage the general connections and organization of the connection to the server. 
 * <p/>
 * @author held03
 */
public interface Client {
	
	/**
	 * Starts a client.
	 * <p/>
	 * This means that the client connects to the server.
	 * <p/>
	 * Notice that this will only work on a new client instance.
	 * If it was already stated, this method will fail, also if it was stopped with {@link #stopConnection()}.
	 */
	public void startConnection();
	
	/**
	 * Stops the client.
	 * <p/>
	 * This will break the connection to the server, all related threads are interrupted.
	 */
	public void stopConnection();
	
	/**
	 * Send a message to the server.
	 */
	public void send(Message msg);
	
	/**
	 * Gets the underlying connection of the client.
	 * <p/>
	 * @return the local connection
	 */
	public Connection getConnection();
	
	/**
	 * Adds a listener for this client.
	 * <p/>
	 * @param listener the listener to add
	 */
	public void addListener(ServerListener listener);
	
	/**
	 * Removes a listener from this client.
	 * <p/>
	 * @param listener the listener to remove
	 */
	public void removeListener(ServerListener listener);
}
