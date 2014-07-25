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

import java.util.concurrent.Future;


/**
 * Manage the general connections and organization of the connection to the
 * server.
 * 
 * @author held03
 */
public interface Client {

	/**
	 * Starts a client.
	 * <p>
	 * This means that the client connects to the server.
	 * <p>
	 * Notice that this will only work on a new client instance. If it was
	 * already stated, this method will fail, also if it was stopped with
	 * {@link #stopConnection()}.
	 */
	public void startConnection();

	/**
	 * Stops the client.
	 * <p>
	 * This will break the connection to the server, all related threads are
	 * interrupted.
	 */
	public void stopConnection();

	/**
	 * Send a message to the server.
	 * <p>
	 * With the Future object it is possible to check if the message is sent and
	 * to abort it. Also the Future object informs if the transfer was
	 * successfully or not.
	 * 
	 * @see #send(Message,
	 *      com.github.held03.jasityProtocol.interfaces.Message.Priority)
	 * @param msg the message to send
	 * @return a tracking object
	 */
	public Future<Boolean> send(Message msg);

	/**
	 * Send a message to the server.
	 * <p>
	 * With the Future object it is possible to check if the message is sent and
	 * to abort it. Also the Future object informs if the transfer was
	 * successfully or not.
	 * 
	 * @see #send(Message)
	 * @param msg the message to send
	 * @param priority the priority of the message
	 * @return a tracking object
	 */
	public Future<Boolean> send(Message msg, Message.Priority priority);

	/**
	 * Gets the underlying connection of the client.
	 * 
	 * @return the local connection
	 */
	public Connection getConnection();

	/**
	 * Adds a listener for this client.
	 * 
	 * @param listener the listener to add
	 */
	public void addListener(ServerListener listener);

	/**
	 * Removes a listener from this client.
	 * 
	 * @param listener the listener to remove
	 */
	public void removeListener(ServerListener listener);
}
