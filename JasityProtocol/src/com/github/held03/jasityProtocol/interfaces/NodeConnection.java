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

import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.Set;
import java.util.concurrent.Future;

import com.github.held03.jasityProtocol.base.ListenerContainer;


/**
 * This represents a connection to a specific node.
 * <p>
 * This is useful if multiple nodes are communicate over a single local socket.
 * <p>
 * It identifies a node depending on the type of the connection.
 * 
 * @author held03
 */
public interface NodeConnection {

	/**
	 * Sends a message to the represented client.
	 * <p>
	 * Anyway this will send the message to the client.
	 * 
	 * @param msg the message to send
	 */
	public void sendMessage(Message msg);

	/**
	 * Gets the Internet address of the client.
	 * <p>
	 * Notice that this is NOT a unique identifier for this client, due it is
	 * possible that multiple clients can connect to this server from the same
	 * address.
	 * 
	 * @return the Internet address
	 */
	public InetAddress getInetAddress();


	/**
	 * Gets the socket address of the client.
	 * <p>
	 * If the underlying system supports no socket address this method will
	 * return <code>null</code>.
	 * 
	 * @return the socket address
	 */
	public SocketAddress getSocketAddress();


	/**
	 * Gets the connection of client to communicate.
	 * <p>
	 * This get the connection instance over which the client communicate with
	 * the server.
	 * 
	 * @return the socket address
	 */
	public Connection getConnection();

	/**
	 * Sends a message over this connection.
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
	 * Adds a listener object for this connection.
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
	 * {@link JPListener} annotation, but do not match the other
	 * precondition.
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
	 * This gets all listeners add to this NodeConnection.
	 * <p>
	 * The returned list should not be changed. Instant use
	 * {@link #addListener(Object)} and {@link #removeListener(Object)}.
	 * 
	 * @return all listeners of this connection
	 */
	public Set<ListenerContainer> getListeners();

}
