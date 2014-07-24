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

/**
 * Manage the general connections and organization of the connection to the server.
 * <p/>
 * 
 * @author held03
 */
public interface Client {

	/**
	 * Starts a client.
	 * <p/>
	 * This means that the client connects to the server.
	 * <p/>
	 * Notice that this will only work on a new client instance. If it was already stated, this method will fail, also
	 * if it was stopped with {@link #stopConnection()}.
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
	 * 
	 * @return the local connection
	 */
	public Connection getConnection();

	/**
	 * Adds a listener for this client.
	 * <p/>
	 * 
	 * @param listener the listener to add
	 */
	public void addListener(ServerListener listener);

	/**
	 * Removes a listener from this client.
	 * <p/>
	 * 
	 * @param listener the listener to remove
	 */
	public void removeListener(ServerListener listener);
}
