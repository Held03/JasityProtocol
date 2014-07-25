/**
 * Copyright 2014 Adam Wagenhäuser <adam@wag-web.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
 *
 * This file is part of the JasityProtocol. It is a library to provide easy
 * server-client networking.
 * 
 * It can be accessed on github:
 *
 *     https://github.com/Held03/JasityProtocol.git
 *
 */

package com.github.held03.jasityProtocol.interfaces;

import java.util.List;
import java.util.Set;

import com.github.held03.jasityProtocol.base.ListenerContainer;


/**
 * A connection to a server or a client.
 * <p>
 * This could be once either a connection to a client or to a server. Anyway it
 * has the same functionality.
 * <p>
 * A connection is opened as construction. The class defines the real type of
 * the underlying system.
 * <p>
 * 
 * @author held03
 */
public interface Connection {

	/**
	 * Closes the connection to the target.
	 */
	public void close();

	/**
	 * Gets the calculated ping time.
	 * <p>
	 * This didn't send any ping. It returns the average ping time.
	 * <p>
	 * The period of time within the ping time is collected can vary, but should
	 * be below 5 minutes.
	 * 
	 * @return the average ping time
	 */
	public float getPingTime();

	/**
	 * Gets the relative time the sender takes to send the data.
	 * <p>
	 * This method indicates how many time the sender was blocked by the output
	 * stream to send the data within the last minute.
	 * <p>
	 * For the load time of the last 5 minutes see
	 * {@link #getConectionOutputLoad5()}.
	 * 
	 * @return the connection busy time
	 */
	public float getConectionOutputLoad1();

	/**
	 * Gets the relative time the sender takes to send the data.
	 * <p>
	 * This method indicates how many time the sender was blocked by the output
	 * stream to send the data within the last 5 minutes.
	 * <p>
	 * For the load time of the last minute see
	 * {@link #getConectionOutputLoad1()}.
	 * 
	 * @return the connection busy time
	 */
	public float getConectionOutputLoad5();

	/**
	 * Adds a listener object for this connection.
	 * <p>
	 * These listeners will be only invoked if a message arrives for this
	 * connection, but independent from the target NodeConnecton it has.
	 * <p>
	 * This method will check all methods of the given object to match the
	 * precondition described in {@link JPListener}. If any found, it will be
	 * added to the connection. If more found they are added individual. If no
	 * one found it will return without adding anything.
	 * <p>
	 * Therefore the implementation needs to process such a test before adding
	 * the listener. It is recommended for the implementation to give out a
	 * warning (do NOT throw an exception) if a method has the
	 * {@link JPListener} annotation, but do not match the other precondition.
	 * <p>
	 * To perform this check and generate the {@link ListenerContainer}s, the
	 * {@link ListenerContainer#getListeners(Object)} method can be used.
	 * 
	 * @param listener the listener to add
	 */
	public void addListener(Object listener);

	/**
	 * Removes a listener from this connection.
	 * <p>
	 * Removes all listener of the given object.
	 * 
	 * @param listener the listener to remove
	 */
	public void removeListener(Object listener);

	/**
	 * Gets added listeners.
	 * <p>
	 * This gets all listeners add to this connection. Notice that this will not
	 * contain the listeners added to a single {@link NodeConnection} of this
	 * connection.
	 * <p>
	 * The returned list should not be changed. Instant use
	 * {@link #addListener(Object)} and {@link #removeListener(Object)}.
	 * 
	 * @return all listeners of this connection
	 */
	public Set<ListenerContainer> getListeners();

	/**
	 * Returns all related {@link NodeConnection}s.
	 * <p>
	 * For client side this should return only one NodeConnection.
	 * <p>
	 * For server side this should return all NodeConnection which sends and
	 * receives over this connection.
	 * 
	 * @return
	 */
	public List<NodeConnection> getRelatedNodes();

	/**
	 * Indicate if the connection is on the server side.
	 * 
	 * @return <code>true</code> if this is the server end of a connection,
	 *         <code>false</code> if client end.
	 */
	public boolean isOnServerSide();
}
