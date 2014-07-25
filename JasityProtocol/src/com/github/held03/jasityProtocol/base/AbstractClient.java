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
import java.util.concurrent.Future;

import com.github.held03.jasityProtocol.interfaces.Client;
import com.github.held03.jasityProtocol.interfaces.ClientListener;
import com.github.held03.jasityProtocol.interfaces.ConnectionManager;
import com.github.held03.jasityProtocol.interfaces.Message;
import com.github.held03.jasityProtocol.interfaces.Message.Priority;
import com.github.held03.jasityProtocol.interfaces.NodeConnection;


/**
 * Provides some simple implementations.
 * <p>
 * This provides a listener management, as well as sending forward to the
 * connection.
 * <p>
 * Additional it implements the {@link ConnectionManager}, what is recommended
 * for every client. So this implementation provides a default
 * {@link ConnectionManager#checkMessage(Message, NodeConnection)} forwarding to
 * the {@link ClientListener}s.
 * 
 * @author held03
 */
public abstract class AbstractClient implements Client, ConnectionManager {

	/**
	 * All registered ClientListeners.
	 * <p>
	 * This field should be synchronized if accessed. Like:
	 * 
	 * <pre>
	 * synchronized (listeners) {
	 * 	// access or edit list ...
	 * }
	 * </pre>
	 */
	protected HashSet<ClientListener> listeners = new HashSet<>();

	/**
	 * Empty constructor.
	 */
	public AbstractClient() {

	}

	@Override
	public Future<Boolean> send(final Message msg) {
		return send(msg, Priority.NORMAL);
	}

	@Override
	public Future<Boolean> send(final Message msg, final Priority priority) {
		return getConnection().send(msg, priority);
	}

	@Override
	public void addListener(final ClientListener listener) {
		synchronized (listeners) {
			listeners.add(listener);
		}
	}

	@Override
	public void removeListener(final ClientListener listener) {
		synchronized (listeners) {
			listeners.remove(listener);
		}
	}

	@Override
	public boolean checkMessage(final Message msg, final NodeConnection con) {
		boolean accepted = true;

		// lets check the message by all listeners
		for (ClientListener listener : listeners) {
			// if returned false, set return to false
			if (!listener.newMessage(msg, con)) {
				accepted = false;
			}
		}

		return accepted;
	}

}
