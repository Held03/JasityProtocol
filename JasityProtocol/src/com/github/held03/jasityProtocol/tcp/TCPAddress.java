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

package com.github.held03.jasityProtocol.tcp;

import java.io.IOException;
import java.net.InetAddress;

import com.github.held03.jasityProtocol.interfaces.Address;
import com.github.held03.jasityProtocol.interfaces.BackEnd;
import com.github.held03.jasityProtocol.interfaces.Node;


/**
 * @author held03
 */
public class TCPAddress implements Address {

	public final InetAddress inetAddress;
	public final int port;

	/**
	 * 
	 */
	public TCPAddress(final InetAddress ipAddress, final int port) {
		this.inetAddress = ipAddress;
		this.port = port;
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * @see com.github.held03.jasityProtocol.interfaces.Address#connectTo()
	 */
	@Override
	public Node connectTo() {
		try {
			return TCPConnection.newConnection(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.github.held03.jasityProtocol.interfaces.Address#getBackEnd()
	 */
	@Override
	public BackEnd getBackEnd() {
		return TCPBackEnd.instance;
	}

	@Override
	public boolean equals(final Object o) {
		if (o instanceof TCPAddress) {
			TCPAddress ad = (TCPAddress) o;
			return inetAddress.equals(ad.inetAddress) && port == ad.port;
		}
		return false;
	}

}
