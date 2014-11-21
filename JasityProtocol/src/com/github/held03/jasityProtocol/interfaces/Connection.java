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
	 * Closes the connection to the target.
	 */
	public void close();

	/**
	 * Check is the connection connected.
	 * 
	 * @return <code>true</code> if the connection is online, or
	 *         <code>false</code> if it is disconnected
	 */
	public boolean isConnected();

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
	 * 
	 * @return list of nodes which communicates over this node
	 */
	public List<Node> getRelatedNodes();
}
