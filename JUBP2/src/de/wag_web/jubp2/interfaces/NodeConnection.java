package de.wag_web.jubp2.interfaces;

import java.net.InetAddress;
import java.net.SocketAddress;

/**
 * This represents a connection to a specific node.
 * <p>
 * This is useful if multiple nodes are communicate over a single local socket.
 * <p>
 * It identifies a node depending on the type of the connection.
 * <p>
 * @author held03
 */
public interface NodeConnection {
	
	/**
	 * Sends a message to the represented client.
	 * <p>
	 * Anyway this will send the message to the client.
	 * <p>
	 * @param msg the message to send
	 */
	public void sendMessage(Message msg);
	
	/**
	 * Gets the Internet address of the client.
	 * <p>
	 * Notice that this is NOT a unique identifier for this client,
	 * due it is possible that multiple clients can connect to this server from the same address.
	 * <p>
	 * @return the Internet address
	 */
	public InetAddress getInetAddress();
	
	
	/**
	 * Gets the socket address of the client.
	 * <p>
	 * If the underlying system supports no socket address this method will return <code>null</code>.
	 * <p>
	 * @return the socket address
	 */
	public SocketAddress getSocketAddress();
	
	
	/**
	 * Gets the connection of client to communicate.
	 * <p>
	 * This get the connection instance over which the client communicate with the server.
	 * <p>
	 * @return the socket address
	 */
	public Connection getConnection();
}

