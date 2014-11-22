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

import java.util.Set;
import java.util.concurrent.Future;

import com.github.held03.jasityProtocol.base.ListenerContainer;


/**
 * The central interface of the protocol.
 * <p>
 * The Node is the central point. It is the access point between the back end
 * and the application. It manages the central processes of the protocol.
 * Therefore it manages the back end independent protocol system, like message
 * splitting and ordering and assembly.
 * 
 * @author held03
 */
public interface Node {

	public enum State {
		/**
		 * The back end tries to open the connection to the remote node.
		 */
		opening,

		/**
		 * The node is connected and ready to send an receive messages.
		 */
		connected,

		/**
		 * The node was anyhow closed.
		 */
		closed
	}

	/**
	 * The connection back end received a block for this node.
	 * <p>
	 * This should be called by the back end implementation of the connection if
	 * data was received.
	 * <p>
	 * The method receives the raw block like received by the connection. Only
	 * the things added or modified by the connection got reverted. The node
	 * will parse the block and determinate the message and the applied filters,
	 * and decode and reassemble it.
	 * 
	 * @param block the received raw block
	 */
	public void receivedBlock(byte[] block);

	/**
	 * Returns the next raw block to send.
	 * <p>
	 * This should be called by the back end implementation of the connection to
	 * get data to send.
	 * <p>
	 * The method will return the next raw data block to be send over the
	 * connection. If there is currently no block to send, the method will block
	 * until a new block gets available through sending a new message for
	 * instance.
	 * <p>
	 * The node took a lot of care about the data which goes out this way. Due
	 * special statements the nodes assure that Messages gets correctly send
	 * also if one or more blocks get lost.
	 * <p>
	 * The blocks are at most as long like the connection block size is.
	 * 
	 * @return a raw data block to send
	 * @see #getNextBlockDirectly()
	 */
	public byte[] getNextBlock();

	/**
	 * Returns immediately the next raw block to send.
	 * <p>
	 * This behaves similar to {@link #getNextBlock()}, but woun't block if no
	 * block is currently available. If no block is ready, it will return
	 * <code>null</code>.
	 * 
	 * @return a raw data block to send, or <code>null</code>
	 * @see #getNextBlock()
	 */
	public byte[] getNextBlockDirectly();

	/**
	 * Gets the calculated ping time in seconds.
	 * <p>
	 * This didn't send any ping. It returns the average ping time.
	 * <p>
	 * The period of time within the ping time is collected can vary, but should
	 * be below 5 minutes.
	 * <p>
	 * If no ping time was available, it is recommended to return
	 * <code>10s</code>, but this depends on the implementation.
	 * 
	 * @return the average ping time
	 */
	public float getPingTime();

	/**
	 * Send a message to the node.
	 * <p>
	 * This should be called by the application to send a message.
	 * <p>
	 * This method accepts messages to be send to the connected node. The node
	 * will assure the sending of the message. According to the priority of a
	 * message the node will send it privilege immediately or delay if other
	 * messages are privileged.
	 * <p>
	 * Additionally the node will encode the message and apply the specified
	 * filters.
	 * <p>
	 * With the Future object it is possible to check if the message is sent and
	 * to abort it. Also the Future object informs if the transfer was
	 * successfully or not.
	 * 
	 * @param msg the message to send
	 * @return a future to track the message
	 */
	public Future<Boolean> sendMessage(Message msg);

	/**
	 * Closes the connection to the remote node and interrupts the connection.
	 */
	public void close();

	/**
	 * Returns is the node still connected.
	 * <p>
	 * It returns <code>true</code>, only if the node is currently connected and
	 * able to send and receive messages.
	 * 
	 * @return <code>true</code> if connected, otherwise <code>false</code>
	 */
	public boolean isConnected();

	/**
	 * Gets the address of this node locally.
	 * <p>
	 * 
	 * @return the address of this node
	 */
	public Address getLocalAddress();

	/**
	 * Gets the address this node is connected to.
	 * <p>
	 * 
	 * @return the address of the remote node
	 */
	public Address getRemoteAddress();

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

	public State getState();
}
