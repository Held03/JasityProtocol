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

import com.github.held03.jasityProtocol.interfaces.Address;
import com.github.held03.jasityProtocol.interfaces.Message;
import com.github.held03.jasityProtocol.interfaces.Message.Priority;
import com.github.held03.jasityProtocol.interfaces.Node;
import com.github.held03.jasityProtocol.interfaces.NodeClosedException;
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
public abstract class AbstractServer extends AbstractConnection implements Server {

	/**
	 * Set of all server listeners.
	 */
	protected HashSet<ServerListener> listeners = new HashSet<ServerListener>();

	/**
	 * Normal constructor.
	 */
	public AbstractServer(final Address localAddress) {
		super(localAddress);

	}

	/**
	 * Normal constructor.
	 */
	public AbstractServer(final Address localAddress, final boolean closeIfEmpty) {
		super(localAddress, closeIfEmpty);

	}

	/*
	 * (non-Javadoc)
	 * @see com.github.held03.jasityProtocol.interfaces.Server#close()
	 */
	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.github.held03.jasityProtocol.interfaces.Server#broadcast(com.github
	 * .held03.jasityProtocol.interfaces.Message)
	 */
	@Override
	public void broadcast(final Message msg) {
		broadcast(msg, Priority.NORMAL);

	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.github.held03.jasityProtocol.interfaces.Server#broadcast(com.github
	 * .held03.jasityProtocol.interfaces.Message,
	 * com.github.held03.jasityProtocol.interfaces.Message.Priority)
	 */
	@Override
	public void broadcast(final Message msg, final Priority priority) {
		synchronized (nodes) {
			for (Node n : nodes.values()) {
				try {
					n.sendMessage(msg, priority);
				} catch (NodeClosedException e) {
					// will be handled if try to read from node
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.github.held03.jasityProtocol.interfaces.Server#addListener(com.github
	 * .held03.jasityProtocol.interfaces.ServerListener)
	 */
	@Override
	public void addListener(final ServerListener listener) {
		listeners.add(listener);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.github.held03.jasityProtocol.interfaces.Server#removeListener(com
	 * .github.held03.jasityProtocol.interfaces.ServerListener)
	 */
	@Override
	public void removeListener(final ServerListener listener) {
		listeners.remove(listener);
	}

	@Override
	public boolean addNode(final Node n) {

		boolean accept = true;

		/*
		 * Accepts only if all listeners return true.
		 */
		for (ServerListener sl : listeners) {
			accept &= (sl.newNode(n));
		}

		/*
		 * Add if it was accepted.
		 */
		if (accept) {
			return super.addNode(n);
		} else {
			n.close();
		}

		return false;
	}

	@Override
	public boolean rmNode(final Node n) {

		boolean wasRm = super.rmNode(n);

		if (wasRm) {
			for (ServerListener sl : listeners) {
				sl.nodeLost(n);
			}
		}

		return wasRm;
	}



}
