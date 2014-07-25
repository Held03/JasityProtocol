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


/**
 * Manage the general connections and organization of the connections to the
 * clients.
 * 
 * @author held03
 */
public interface Server {

	/**
	 * Starts the server.
	 * <p>
	 * This means that the server starts listening on the server port.
	 * <p>
	 * Notice that this will only work on a new server instance. If it was
	 * already stated, this method will fail, also if it was stopped with
	 * {@link #stopConnection()}.
	 */
	public void startConnection();

	/**
	 * Stops the server.
	 * <p>
	 * This will break all connections to the server, all related threads are
	 * interrupted.
	 */
	public void stopConnection();

	/**
	 * Broadcasts a message to all clients.
	 */
	public void broadcast(Message msg);

	/**
	 * Gets all connections to clients.
	 * <p>
	 * 
	 * @return a list of all connection nodes
	 */
	public Set<NodeConnection> getNodes();

	/**
	 * Adds a listener for this server.
	 * <p>
	 * 
	 * @param listener the listener to add
	 */
	public void addListener(ServerListener listener);

	/**
	 * Removes a listener from this server.
	 * <p>
	 * 
	 * @param listener the listener to remove
	 */
	public void removeListener(ServerListener listener);
}
