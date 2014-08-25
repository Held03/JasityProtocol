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

package com.github.held03.jasityProtocol.base.managedCon;

import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.Set;
import java.util.concurrent.Future;

import com.github.held03.jasityProtocol.base.ListenerContainer;
import com.github.held03.jasityProtocol.interfaces.Connection;
import com.github.held03.jasityProtocol.interfaces.JPListener;
import com.github.held03.jasityProtocol.interfaces.Message;
import com.github.held03.jasityProtocol.interfaces.Message.Priority;
import com.github.held03.jasityProtocol.interfaces.NodeConnection;


/**
 * @author held03
 */
public class ManagedNode implements NodeConnection {

	/**
	 * 
	 */
	public ManagedNode() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Adds a listener object for this node.
	 * <p>
	 * The added listener will be only invoked if an message arrives for this
	 * NodeConnection.
	 * <p>
	 * This method will check all methods of the given object to match the
	 * precondition described in {@link JPListener}. If any found, it will be
	 * added to the connection. If more found they are added individual. If no
	 * one found it will return without adding anything.
	 * <p>
	 * Therefore the implementation needs to process such a test before adding
	 * the listener. It is recommended for the implementation to give out a
	 * warning (do NOT throw an exception) if a method has the
	 * {@link JPListener} annotation, but do not match the other precondition.
	 * <p>
	 * To perform this check and generate the {@link ListenerContainer}s, the
	 * {@link ListenerContainer#getListeners(Object)} method can be used.
	 * 
	 * @param listener the listener to add
	 */
	public void addListener(final Object listener) {
	}

	/**
	 * Removes a listener from this node.
	 * <p>
	 * Removes all listener of the given object.
	 * 
	 * @param listener the listener to remove
	 */
	public void removeListener(final Object listener) {
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.github.held03.jasityProtocol.interfaces.NodeConnection#getInetAddress
	 * ()
	 */
	@Override
	public InetAddress getInetAddress() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.github.held03.jasityProtocol.interfaces.NodeConnection#getSocketAddress
	 * ()
	 */
	@Override
	public SocketAddress getSocketAddress() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.github.held03.jasityProtocol.interfaces.NodeConnection#getConnection
	 * ()
	 */
	@Override
	public Connection getConnection() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.github.held03.jasityProtocol.interfaces.NodeConnection#send(com.github
	 * .held03.jasityProtocol.interfaces.Message)
	 */
	@Override
	public Future<Boolean> send(final Message message) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.github.held03.jasityProtocol.interfaces.NodeConnection#send(com.github
	 * .held03.jasityProtocol.interfaces.Message,
	 * com.github.held03.jasityProtocol.interfaces.Message.Priority)
	 */
	@Override
	public Future<Boolean> send(final Message message, final Priority priority) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.github.held03.jasityProtocol.interfaces.NodeConnection#getListeners()
	 */
	@Override
	public Set<ListenerContainer> getListeners() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.github.held03.jasityProtocol.interfaces.NodeConnection#getState()
	 */
	@Override
	public State getState() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.github.held03.jasityProtocol.interfaces.NodeConnection#addFilter(
	 * com.github.held03.jasityProtocol.base.managedCon.Filter, long)
	 */
	@Override
	public void addFilter(final Filter filter, final long order) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.github.held03.jasityProtocol.interfaces.NodeConnection#removeFilter
	 * (com.github.held03.jasityProtocol.base.managedCon.Filter)
	 */
	@Override
	public void removeFilter(final Filter filter) {
		// TODO Auto-generated method stub

	}

}
