package de.wag_web.jubp2.interfaces;

import java.util.concurrent.Future;

/**
 * A connection to a server or a client.
 * <p/>
 * This could be once either a connection to a client or to a server.
 * Anyway it has the same functionality.
 * <p/>
 * @author held03
 */
public interface Connection {
	
	/**
	 * Closes the connection to the target.
	 */
	public void close();
	
	/**
	 * Sends a message over this connection.
	 * <p/>
	 * With the Future object it is possible to check if the message is sent and to abort it.
	 * Also the Future object informs if the transfer was successfully or not.
	 * <p/>
	 * @param message the message to send
	 * @return a tracing object
	 */
	public Future<Boolean> send(Message message);
	
	/**
	 * Gets the calculated ping time.
	 * <p/>
	 * This didn't send any ping.
	 * It returns the average ping time.
	 * <p/>
	 * The period of time within the ping time is collected can vary, but should be below 5 minutes. 
	 * <p/>
	 * @return the average ping time
	 */
	public float getPingTime();
	
	/**
	 * Gets the relative time the sender takes to send the data.
	 * <p/>
	 * This method indicates how many time the sender was blocked by the output stream
	 * to send the data within the last minute.
	 * <p/>
	 * For the load time of the last 5 minutes see {@link #getConectionOutputLoad5()}.
	 * <p/>
	 * @return the connection busy time
	 */
	public float getConectionOutputLoad1();
	
	/**
	 * Gets the relative time the sender takes to send the data.
	 * <p/>
	 * This method indicates how many time the sender was blocked by the output stream
	 * to send the data within the last 5 minutes.
	 * <p/>
	 * For the load time of the last minute see {@link #getConectionOutputLoad1()}.
	 * <p/>
	 * @return the connection busy time
	 */
	public float getConectionOutputLoad5();
	
	/**
	 * Adds a listener for this connection.
	 * <p/>
	 * @param listener the listener to add
	 */
	public void addListener(Object listener);
	
	/**
	 * Removes a listener from this connection.
	 * <p/>
	 * @param listener the listener to remove
	 */
	public void removeListener(Object listener);
}
