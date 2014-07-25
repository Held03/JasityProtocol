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

package com.github.held03.jasityProtocol.base;

import java.util.HashSet;

import com.github.held03.jasityProtocol.interfaces.ConnectionManager;
import com.github.held03.jasityProtocol.interfaces.Message;
import com.github.held03.jasityProtocol.interfaces.NodeConnection;
import com.github.held03.jasityProtocol.interfaces.Server;
import com.github.held03.jasityProtocol.interfaces.ServerListener;


/**
 * Implements some simple systems.
 * <p>
 * This class provides a simple broadcast method and listener adding and
 * removing as well as check message forwarding.
 * <p>
 * Additional it implements the {@link ConnectionManager}, what is recommended
 * for every server. So this implementation provides a default
 * {@link ConnectionManager#checkMessage(Message, NodeConnection)} forwarding to
 * the {@link ServerListener}s.
 * 
 * @author held03
 */
public abstract class AbstractServer implements Server, ConnectionManager {

	/**
	 * All registered ServerListeners.
	 * <p>
	 * This field should be synchronized if accessed. Like:
	 * 
	 * <pre>
	 * synchronized (listeners) {
	 * 	// access or edit list ...
	 * }
	 * </pre>
	 */
	protected HashSet<ServerListener> listeners = new HashSet<>();

	/**
	 * Empty constructor.
	 */
	public AbstractServer() {

	}

	@Override
	public boolean checkMessage(final Message msg, final NodeConnection con) {
		boolean accepted = true;

		// lets check the message by all listeners
		for (ServerListener listener : listeners) {
			// if returned false, set return to false
			if (!listener.newMessage(msg, con)) {
				accepted = false;
			}
		}

		return accepted;
	}

	@Override
	public void broadcast(final Message msg) {
		// if the transport layer supports a better method like a native
		// broadcast override this method and use it.

		// forward message to all nodes.
		for (NodeConnection nc : getNodes()) {
			nc.send(msg);
		}
	}

	@Override
	public void addListener(final ServerListener listener) {
		synchronized (listeners) {
			listeners.add(listener);
		}
	}

	@Override
	public void removeListener(final ServerListener listener) {
		synchronized (listeners) {
			listeners.remove(listener);
		}
	}



}
