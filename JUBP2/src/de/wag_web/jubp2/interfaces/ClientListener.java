/**
 * 
 */
package de.wag_web.jubp2.interfaces;

/**
 * Allows the receiving of client related events.
 * <p/>
 * This interface is important to implement to receive client events.
 * It gives the client the ability to communicate with the user program. 
 * <p/>
 * @author held03
 */
public interface ClientListener {
	
	/**
	 * Established connection to server.
	 * <p/>
	 * This is invoked if the client established successfully a connection to a server.
	 * <p/>
	 * @return the established connection
	 */
	public void connected(Connection conn);
	
	/**
	 * The connection to the server was lost.
	 * <p/>
	 * This is invoked if the connection to the server breaks/ends anyhow.
	 */
	public void connectionLost();
	
	/**
	 * New message received.
	 * <p/>
	 * This is invoked if the server sends a message to this client.
	 * <p/>
	 * This will be invoke on any message, whether a listener for the message was registered or not.
	 * Additional this method is called first, therefore it can be pre-filtered.
	 * <p/>
	 * If this method returns <code>false</code> the message will be rejected and
	 * NOT be forward to the listeners.
	 * If it returns <code>true</code> the message will be sent to the listeners.
	 * <p/>
	 * The default should be <code>true</code>, if used with the listeners.
	 * <p/>
	 * @param msg the received message
	 * @return <code>true</code> if the given message was accepted
	 */
	public boolean newMessage(Message msg);
}
