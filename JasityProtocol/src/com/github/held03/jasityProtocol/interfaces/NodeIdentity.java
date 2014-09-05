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


/**
 * Unique identity for a {@link NodeConnection}.
 * <p>
 * This class identify a node base on its remote address and on its underlying
 * protocol.
 * <p>
 * It is recommended that every transport has its own implementation of this
 * identity.
 * <p>
 * For TCP this identity will consist of the IP address and the port.
 * 
 * @author held03
 */
public interface NodeIdentity {

	/**
	 * Gets the Internet address of the remote node.
	 * <p>
	 * Notice that this is NOT a unique identifier for this node, due it is
	 * possible that multiple nodes can connect to local node from the same IP
	 * address.
	 * 
	 * @return the Internet address
	 */
	public InetAddress getInetAddress();
}
