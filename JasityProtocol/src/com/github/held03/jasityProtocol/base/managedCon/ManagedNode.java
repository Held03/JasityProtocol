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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.Future;

import com.github.held03.jasityProtocol.base.ListenerContainer;
import com.github.held03.jasityProtocol.interfaces.Connection;
import com.github.held03.jasityProtocol.interfaces.Message;
import com.github.held03.jasityProtocol.interfaces.Message.Priority;
import com.github.held03.jasityProtocol.interfaces.NodeConnection;
import com.github.held03.jasityProtocol.interfaces.NodeIdentity;


/**
 * @author held03
 */
public class ManagedNode implements NodeConnection {

	/**
	 * All registered filters of this node.
	 */
	LinkedList<Filter> filters = new LinkedList<Filter>();

	/**
	 * All listeners registered to this node.
	 */
	HashSet<ListenerContainer> listeners = new HashSet<ListenerContainer>();

	/**
	 * The related connection of this node.
	 */
	ManagedConnection connection;

	/**
	 * The specific ID of this connection.
	 */
	NodeIdentity id;

	/**
	 * The current state of the node.
	 */
	State status = State.PRE_CONNECTION;

	/**
	 * Creates a simple unconnected node.
	 */
	public ManagedNode(final ManagedConnection connection) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void addListener(final Object listener) {
		listeners.addAll(ListenerContainer.getListeners(listener));
	}

	@Override
	public void removeListener(final Object listener) {
		listeners.removeAll(getListenersOfObject(listener));
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.github.held03.jasityProtocol.interfaces.NodeConnection#getListeners()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Set<ListenerContainer> getListeners() {
		return (Set<ListenerContainer>) listeners.clone();
	}

	/**
	 * Gets the registered listeners of the given listener object.
	 * 
	 * @param o the object to get the listeners of
	 * @return the listeners
	 */
	public Set<ListenerContainer> getListenersOfObject(final Object o) {
		HashSet<ListenerContainer> results = new HashSet<ListenerContainer>();

		for (ListenerContainer cont : listeners) {
			// Notice: it must be exactly the same instance and
			// NOT only have the same content like checked with equals() 
			if (cont.object == o) // do NOT use equals()
				results.add(cont);
		}

		return results;
	}


	/**
	 * Gets the listeners of a specific message.
	 * <p>
	 * This returns all the listeners of this node which have to be called if
	 * the given message was received.
	 * 
	 * @param m the message to get the listeners for
	 * @return the listeners of the given message
	 */
	public Set<ListenerContainer> getListenersOfMessage(final Message m) {
		return getListenersOfMessage(m.getClass());
	}

	/**
	 * Gets the listeners of a specific message class.
	 * <p>
	 * This returns all the listeners of this node which have to be called if
	 * the given type of message was received.
	 * 
	 * @param c the class to get the listeners for
	 * @return the listeners of the given class
	 */
	public Set<ListenerContainer> getListenersOfMessage(final Class<?> c) {
		HashSet<ListenerContainer> results = new HashSet<ListenerContainer>();

		for (ListenerContainer cont : listeners) {
			// check if the given class is equals the listened type or
			// if the given class is a sub class
			if (cont.message.isAssignableFrom(c))
				results.add(cont);
		}

		return results;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.github.held03.jasityProtocol.interfaces.NodeConnection#getIdentity
	 * ()
	 */
	@Override
	public NodeIdentity getIdentity() {
		return id;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.github.held03.jasityProtocol.interfaces.NodeConnection#getConnection
	 * ()
	 */
	@Override
	public Connection getConnection() {
		return connection;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.github.held03.jasityProtocol.interfaces.NodeConnection#send(com.github
	 * .held03.jasityProtocol.interfaces.Message)
	 */
	@Override
	public Future<Boolean> send(final Message message) {
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
	 * com.github.held03.jasityProtocol.interfaces.NodeConnection#getState()
	 */
	@Override
	public State getState() {
		return status;
	}

	/**
	 * Adds a filter to this node.
	 * <p>
	 * A filter will modify every message passed by this connection for
	 * transmitting. After receiving the filters will restore the changes that
	 * the messages can be parsed.
	 * <p>
	 * This is useful for encryption or compression.
	 * <p>
	 * Every filter gets an index number for this connection. This is important
	 * to restore the filters in the exactly reveres order they are applied.
	 * <p>
	 * The filter with the lowest index will be applied first and restored last.
	 * <p>
	 * This method adds the filter at the end of the list.
	 * 
	 * @see #removeFilter(Filter)
	 * @param filter the filter to add
	 * @param the order of the filter
	 */
	public void addFilter(final Filter filter) {

	}

	/**
	 * Adds a filter to this node.
	 * <p>
	 * A filter will modify every message passed by this connection for
	 * transmitting. After receiving the filters will restore the changes that
	 * the messages can be parsed.
	 * <p>
	 * This is useful for encryption or compression.
	 * <p>
	 * Every filter gets an index number for this connection. This is important
	 * to restore the filters in the exactly reveres order they are applied.
	 * <p>
	 * The filter with the lowest index will be applied first and restored last.
	 * <p>
	 * This method adds the filter at the specific index. If there was already a
	 * entry that old one will be shifted down to the end of the list as well as
	 * all following entries. If the given index is far out of the boundary of
	 * the list it will be added to the end.
	 * 
	 * @see #removeFilter(Filter)
	 * @param filter the filter to add
	 * @param the order of the filter
	 */
	public void addFilter(final Filter filter, final int index) {

	}

	/**
	 * Removes a filter from this node.
	 * 
	 * @see #addFilter(Filter, long)
	 * @param filter
	 */
	public void removeFilter(final Filter filter) {

	}


}
