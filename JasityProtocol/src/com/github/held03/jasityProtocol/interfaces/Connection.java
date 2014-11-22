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


/**
 * A connection to a server or a client.
 * <p>
 * This interface has to be implemented by the back end. It should check all
 * related nodes by calling {@link Node#getNextBlock()} or
 * {@link Node#getNextBlockDirectly()} on them. If some thing was received for a
 * specific node, {@link Node#receivedBlock(byte[])} should be called on it.
 * <p>
 * 
 * @author held03
 */
public interface Connection {

	/**
	 * Closes the connection from the Internet.
	 * <p>
	 * This will close all related nodes as well.
	 */
	public void close();

	/**
	 * Check if the connection is connected.
	 * 
	 * @return <code>true</code> if the connection is online, or
	 *         <code>false</code> if it is disconnected
	 */
	public boolean isConnected();

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
	 * Returns all related {@link Node}s.
	 * <p>
	 * All nodes of this list communicate over this connection.
	 * 
	 * @return list of nodes which communicates over this node
	 */
	public List<Node> getRelatedNodes();

	/**
	 * Returns the maximum size of a sending block.
	 * <p>
	 * On a block based or package based back end this is to be set to the usage
	 * size.
	 * <p>
	 * The {@link Node} will use this size to adjust the size of the requested
	 * blocks.
	 * <p>
	 * If the back end does not depend on blocks or packages it should use a
	 * default size perhaps depending on the through put.
	 * 
	 * @return the size of blocks
	 */
	public int getBlockSize();
}
