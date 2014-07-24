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

package de.wag_web.jubp2.interfaces;

import java.net.InetAddress;
import java.net.SocketAddress;


/**
 * This represents a connection to a specific node.
 * <p>
 * This is useful if multiple nodes are communicate over a single local socket.
 * <p>
 * It identifies a node depending on the type of the connection.
 * <p>
 * 
 * @author held03
 */
public interface NodeConnection {

	/**
	 * Sends a message to the represented client.
	 * <p>
	 * Anyway this will send the message to the client.
	 * <p>
	 * 
	 * @param msg the message to send
	 */
	public void sendMessage(Message msg);

	/**
	 * Gets the Internet address of the client.
	 * <p>
	 * Notice that this is NOT a unique identifier for this client, due it is possible that multiple clients can connect
	 * to this server from the same address.
	 * <p>
	 * 
	 * @return the Internet address
	 */
	public InetAddress getInetAddress();


	/**
	 * Gets the socket address of the client.
	 * <p>
	 * If the underlying system supports no socket address this method will return <code>null</code>.
	 * <p>
	 * 
	 * @return the socket address
	 */
	public SocketAddress getSocketAddress();


	/**
	 * Gets the connection of client to communicate.
	 * <p>
	 * This get the connection instance over which the client communicate with the server.
	 * <p>
	 * 
	 * @return the socket address
	 */
	public Connection getConnection();
}
