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
 * This represents a connection to a specific node.
 * <p>
 * This is useful if multiple nodes are communicate over a single local socket.
 * <p>
 * It identifies a node depending on the type of the connection. Anyway this
 * represent a unique remote communication entity.
 * 
 * @author held03
 */
public interface NodeConnection {

	/**
	 * The state indicate the usability of the connection.
	 * <p>
	 * Messages are only send while {@link #OPEN}. If any send while
	 * {@link #CLOSED} a {@link IllegalStateException} will be thrown. If send
	 * earlier it will be queued.
	 * <p>
	 * While {@link #FILTER_INITIATION} messages defined as
	 * {@link Message.Transport#PRE_FILLTERED}
	 * {@link Message.Transport#OPTIONALLY} can be send.
	 * 
	 * @author held03
	 */
	public enum State {
		/**
		 * State before the connection was established.
		 * <p>
		 * At this state no messages are transmitted.
		 */
		PRE_CONNECTION,

		/**
		 * State while the filters are initiating.
		 * <p>
		 * While this state only {@link Message.Transport#PRE_FILLTERED} and
		 * {@link Message.Transport#OPTIONALLY} are transmitted.
		 */
		FILTER_INITIATION,

		/**
		 * Normal open working state.
		 * <p>
		 * This is the normal state. While it messages can fairly be transmitted
		 * and received.
		 */
		OPEN,

		/**
		 * Post connection state.
		 * <p>
		 * At this state a connected was already closed again. It is no more
		 * possible to communicate over this connection. If a message send is
		 * requested a {@link IllegalStateException} will be thrown.
		 */
		CLOSED
	}

	/**
	 * Gets the remote identity of the node.
	 * 
	 * @return the socket address
	 * @see
	 */
	public NodeIdentity getIdentity();


	/**
	 * Gets the connection of node to communicate.
	 * <p>
	 * This get the connection instance over which the node communicate with the
	 * local node.
	 * 
	 * @return the socket address
	 */
	public Connection getConnection();

	/**
	 * Sends a message to this node.
	 * <p>
	 * With the Future object it is possible to check if the message is sent and
	 * to abort it. Also the Future object informs if the transfer was
	 * successfully or not.
	 * 
	 * @param message the message to send
	 * @return a tracking object
	 */
	public Future<Boolean> send(Message message);

	/**
	 * Sends a message to this node.
	 * <p>
	 * With the Future object it is possible to check if the message is sent and
	 * to abort it. Also the Future object informs if the transfer was
	 * successfully or not.
	 * 
	 * @param message the message to send
	 * @param priority the priority of the message
	 * @return a tracking object
	 */
	public Future<Boolean> send(Message message, Message.Priority priority);

	/**
	 * Adds a listener object for this node.
	 * <p>
	 * The added listener will be only invoked if an message arrives for this
	 * NodeConnection.
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
	public void addListener(final Object listener);

	/**
	 * Removes a listener from this node.
	 * <p>
	 * Removes all listener of the given object.
	 * 
	 * @param listener the listener to remove
	 */
	public void removeListener(final Object listener);

	/**
	 * Gets added listeners of this node.
	 * <p>
	 * This gets all listeners add to this NodeConnection.
	 * <p>
	 * The returned list should not be changed. Instant use
	 * {@link #addListener(Object)} and {@link #removeListener(Object)}.
	 * 
	 * @return all listeners of this connection
	 */
	public Set<ListenerContainer> getListeners();

	/**
	 * Gets the state of the underlying connection.
	 * <p>
	 * This relates to the state of the underlying connection but can differ
	 * from it. E.g. because of the filter initialization which is independent
	 * for every NodeConnection.
	 * 
	 * @return the local state of the connection
	 */
	public State getState();

}
