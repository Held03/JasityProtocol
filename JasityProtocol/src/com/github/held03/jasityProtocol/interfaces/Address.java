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
 * An address to a node specific for a protocol.
 * <p>
 * An address relates always to a specific back end. That means every back end
 * has its own address implementation.
 * <p>
 * An address must unambiguously identify a node. If any tow addresses point
 * anyhow to the same node the {@link #equals(Object)} method must return
 * <code>true</code>.
 * <p>
 * The back end has to provide a proper constructor for its address
 * implementation.
 * <p>
 * This interface has to be implemented by the back end.
 * 
 * @author held03
 */
public interface Address {

	/**
	 * Initiate a connection to the node.
	 * <p>
	 * This method will return immediately with a new node instance, even if it
	 * exist is already a connection to this address.
	 * 
	 * @return a new node connected to this address
	 */
	public Node connectTo();

	/**
	 * Gets the back end for which this address is specified.
	 * <p>
	 * 
	 * @return the protocol, which uses this address
	 */
	public BackEnd getBackEnd();
}
