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

package com.github.held03.jasityProtocol.JUnit;

import java.net.InetAddress;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.github.held03.jasityProtocol.base.util.StringMessage;
import com.github.held03.jasityProtocol.interfaces.Address;
import com.github.held03.jasityProtocol.interfaces.JPListener;
import com.github.held03.jasityProtocol.interfaces.Node;
import com.github.held03.jasityProtocol.interfaces.NodeClosedException;
import com.github.held03.jasityProtocol.interfaces.ServerListener;
import com.github.held03.jasityProtocol.tcp.TCPAddress;
import com.github.held03.jasityProtocol.tcp.TCPServer;


/**
 * @author held03
 */
public class TestTCP {

	TCPServer server;

	Node sender;
	Address senderAddr;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		senderAddr = new TCPAddress(InetAddress.getLocalHost(), 12345);

		server = TCPServer.openServer(12345);

		server.addListener(new ServerListener() {

			@Override
			public void nodeLost(final Node node) {
				System.out.println("Server: -lost node-");
			}

			@Override
			public boolean newNode(final Node node) {
				System.out.println("Server: -get new node-");

				node.addListener(new Object() {

					@JPListener
					public Boolean receive(final StringMessage msg) {
						System.out.println("Server: " + msg.getText());

						try {
							node.sendMessage(new StringMessage("I've got it: " + msg.getText()));
						} catch (NodeClosedException e) {
							e.printStackTrace();
						}

						return false;
					}
				});

				return true;
			}
		});

		server.open();

		System.out.println("Setup done.");
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		System.out.println("BRING IT DOWN !!!");

		server.close();
	}

	/**
	 * Test method for
	 * {@link com.github.held03.jasityProtocol.tcp.TCPBackEnd#TCPBackEnd()}.
	 */
	@Test(timeout = 10_000)
	public void testTCPBackEnd() {
		Node n = senderAddr.connectTo();

		System.out.println("Connected.");

		final Thread local = Thread.currentThread();

		n.addListener(new Object() {

			@JPListener
			public Boolean rec(final StringMessage s) {
				System.out.println("Client: " + s.getText());

				if (s.getText().equals("I've got it: " + "Hello world.\nIn Unicode ☮☯♪♻⛔."))
					local.interrupt();

				return false;
			}
		});

		try {
			n.sendMessage(new StringMessage("Hello world.\nIn Unicode ☮☯♪♻⛔."));
		} catch (NodeClosedException e) {
			e.printStackTrace();
		}

		try {
			synchronized (this) {
				this.wait();
			}
		} catch (InterruptedException e) {
			//e.printStackTrace();
		}

		n.close();

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
