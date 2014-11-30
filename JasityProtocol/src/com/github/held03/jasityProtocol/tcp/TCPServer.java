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
import java.net.ServerSocket;
import java.net.Socket;

import com.github.held03.jasityProtocol.base.AbstractServer;
import com.github.held03.jasityProtocol.interfaces.Address;
import com.github.held03.jasityProtocol.interfaces.Node;


/**
 * @author held03
 */
public class TCPServer extends AbstractServer {

	ServerSocket server;

	Thread waiter;

	/**
	 * @param localAddress
	 */
	TCPServer(final Address localAddress) {
		super(localAddress);
	}

	public static TCPServer openServer(final int port) throws IOException {

		@SuppressWarnings("resource")
		ServerSocket server = new ServerSocket(port);

		Address local = new TCPAddress(server.getInetAddress(), server.getLocalPort());

		TCPServer s = new TCPServer(local);

		return s;

	}

	/*
	 * (non-Javadoc)
	 * @see com.github.held03.jasityProtocol.interfaces.Server#open()
	 */
	@Override
	public void open() {
		if (waiter == null) {
			waiter = new Thread(new Waiter());
			waiter.setDaemon(false);
			waiter.start();
		}
	}

	@Override
	public void close() {
		try {
			if (server != null) {
				server.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.github.held03.jasityProtocol.interfaces.Connection#getBlockSize()
	 */
	@Override
	public int getBlockSize() {
		// Nothing to send.
		return 0;
	}

	class Waiter implements Runnable {

		/*
		 * (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			try {
				Socket s = server.accept();

				Node n = TCPConnection.newConnection(s);

				addNode(n);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

}
