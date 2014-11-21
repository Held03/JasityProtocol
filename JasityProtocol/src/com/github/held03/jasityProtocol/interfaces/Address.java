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
 * Am address to a node specific for a protocol.
 * <p>
 * This interface represents a protocol specific address.
 * <p>
 * Use {@link #equals(Object)} to check if to addresses point to the same node.
 * <p>
 * This interface has to be implemented by the back end.
 * 
 * @author held03
 */
public interface Address {

	/**
	 * Initiate a connection to the node.
	 * <p>
	 * This method will block until the connection was established. If something
	 * went wrong it throws an exception.
	 * 
	 * @return a new node connected to this address
	 */
	public Node connectTo();

	/**
	 * Gets the back end for which this address is specified.
	 * <p>
	 * 
	 * @return the protocol this address uses
	 */
	public BackEnd getBackEnd();
}
