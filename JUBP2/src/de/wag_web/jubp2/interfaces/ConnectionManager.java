package de.wag_web.jubp2.interfaces;

/**
 * Defines a class which generally manages the connection and the message transfer a bit.
 * <p>
 * This is the callback interface of the {@link Connection}.
 * General it reference either to a {@link Server} or to a {@link Client}.
 * <p>
 * @author held03
 */
public interface ConnectionManager {
	
	/**
	 * New message received to check.
	 * <p/>
	 * This is invoked if the a message was received to the connection.
	 * <p/>
	 * This will be invoke on any message, whether a listener for the message was registered or not.
	 * Additional this method is called first, therefore it can be pre-filtered.
	 * <p/>
	 * If this method returns <code>false</code> the message will be rejected and
	 * NOT be forward to the listeners.
	 * If it returns <code>true</code> the message will be sent to the listeners.
	 * <p/>
	 * The default should be <code>true</code>, if used with the listeners.
	 * <p>
	 * This call should be forwarded to the {@link ClientListener#newMessage(Message)}
	 * or the {@link ServerListener#newMessage(Message)}
	 * <p/>
	 * @param msg the new message to accept
	 * @return <code>true</code> if the given message was accepted
	 */
	public boolean checkMessage(Message msg);
}
