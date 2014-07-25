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

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.held03.jasityProtocol.interfaces.Connection;
import com.github.held03.jasityProtocol.interfaces.ConnectionManager;
import com.github.held03.jasityProtocol.interfaces.JPListener;
import com.github.held03.jasityProtocol.interfaces.Message;
import com.github.held03.jasityProtocol.interfaces.NodeConnection;


/**
 * The general implementation.
 * <p>
 * This class implements some general purpose features of the connection. But is
 * still flexible.
 * <p>
 * 
 * @author held03
 */
public abstract class AbstractConnection implements Connection {

	/**
	 * The set of all registered connection listeners.
	 */
	protected HashSet<ListenerContainer> messageListeners = new HashSet<>();

	/**
	 * The manager of this connection.
	 * <p>
	 * This is the connection callback.
	 */
	protected ConnectionManager manager;

	/**
	 * Normal constructor.
	 */
	public AbstractConnection(final ConnectionManager manager) {
		this.manager = manager;

	}

	@Override
	public void addListener(final Object listener) {
		// list for adding the new listeners
		Set<ListenerContainer> msgToAdd = ListenerContainer.getListeners(listener);

		// checks if at least one valid listener was found 
		if (msgToAdd.isEmpty()) {
			Logger.getLogger(AbstractConnection.class.getName()).log(Level.WARNING,
					"A listener was added but it could NOT find a valid listener method! For object {1} into {2}",
					new Object[] { listener, this });

			return;
		}

		// synchronize and add the listeners
		synchronized (messageListeners) {
			messageListeners.addAll(msgToAdd);
		}
	}

	@Override
	public void removeListener(final Object listener) {
		synchronized (messageListeners) {
			// creates a list for removing the proper entries later
			HashSet<ListenerContainer> msgToRm = new HashSet<>();

			// search for all listeners related to the given object
			for (ListenerContainer container : messageListeners) {

				if (container.object.equals(listener)) {
					// if one found add it to the list
					msgToRm.add(container);
				}
			}

			// finally removes all selected listeners
			messageListeners.removeAll(msgToRm);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<ListenerContainer> getListeners() {
		synchronized (messageListeners) {
			return (Set<ListenerContainer>) messageListeners.clone();
		}
	}

	/**
	 * Delivers a message to the specific listeners.
	 * <p>
	 * First this method will check the message with the manager
	 * {@link ConnectionManager#checkMessage(Message, Connection)}. If it
	 * returns <code>true</code> it will be delivered to all fitting and
	 * registered listeners. Until one listener returned <code>true</code> or
	 * the end of the list is reached.
	 * <p>
	 * If a listener of a specific level returns <code>true</code> all other
	 * listener of the same level gets the message still before this method
	 * returns. At least all listeners of lower levels don't get the message.
	 * 
	 * @param msg the message to send
	 * @param node the node from which it was received
	 */
	protected void deliverMessage(final Message msg, final NodeConnection node) {
		// check if a manager was preset
		if (manager == null) {
			Logger.getLogger(AbstractConnection.class.getName()).log(Level.WARNING,
					"A message was received but no manager was set for the connection!");
			throw new IllegalStateException("Message couldn't be delivered, because no manager was set.");
		}

		// lets the manager check the incoming message
		// the manager is either the server or the client instance, or the appropriate listener
		if (manager.checkMessage(msg, node)) {
			//manager accepted message, go on with listeners

			// map listeners to their appropriate level
			HashMap<JPListener.Level, HashSet<ListenerContainer>> msgToSend = new HashMap<>();

			// adds a list/set for every level
			for (JPListener.Level level : JPListener.Level.values()) {
				msgToSend.put(level, new HashSet<ListenerContainer>());
			}

			// gets the listener from connection to send to, and map them by level
			synchronized (messageListeners) {
				for (ListenerContainer container : messageListeners) {

					// check if the listener accepts the give message type
					if (container.message.isAssignableFrom(msg.getClass())) {
						msgToSend.get(container.priority).add(container);
					}
				}
			}

			// gets the listener from the node to send to, and map them by level
			for (ListenerContainer container : node.getListeners()) {

				// check if the listener accepts the give message type
				if (container.message.isAssignableFrom(msg.getClass())) {
					msgToSend.get(container.priority).add(container);
				}
			}

			// indicates if a listener returned true
			boolean consumed = false;

			// sends the message
			for (JPListener.Level level : JPListener.Level.values()) {
				for (ListenerContainer container : msgToSend.get(level)) {
					try {
						// check the argument configuration 
						if (container.callback.getParameterTypes().length == 1) {
							// call the listener with one argument
							// and check the return value
							if (container.callback.invoke(container.object, msg).equals(Boolean.TRUE)) {
								consumed = true;
							}
						} else if (container.callback.getParameterTypes().length == 2) {
							// call the listener with the additional nodeConnection arg
							// and check the return value
							if (container.callback.invoke(container.object, msg, node).equals(Boolean.TRUE)) {
								consumed = true;
							}
						} else {

						}

					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						e.printStackTrace();
						Logger.getLogger(AbstractConnection.class.getName()).log(Level.WARNING,
								"Listener invokation fails! object {1}; method {2}",
								new Object[] { container.object, container.callback.getName() });
						Logger.getLogger(AbstractConnection.class.getName()).log(Level.WARNING, "", e);
					}
				}

				// if one listener returns true, don't call lower listeners 
				if (consumed)
					break;
			}

		} else {
			//manager reject message
		}
	}

}
