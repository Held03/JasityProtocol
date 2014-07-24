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

package de.wag_web.jubp2.base;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.wag_web.jubp2.interfaces.Connection;
import de.wag_web.jubp2.interfaces.JUBP2Listener;
import de.wag_web.jubp2.interfaces.Message;
import de.wag_web.jubp2.interfaces.ConnectionManager;
import de.wag_web.jubp2.interfaces.NodeConnection;


/**
 * The general implementation.
 * <p>
 * This class implements some general purpose features of the connection. But is still flexible.
 * <p>
 * 
 * @author held03
 */
public abstract class AbstractConnection implements Connection {

	/**
	 * The set of all registered connection listeners.
	 */
	protected Set<ListenerContainer> messageListeners = new HashSet<>();

	/**
	 * The manager of this connection.
	 * <p>
	 * This is the connection callback.
	 */
	protected ConnectionManager manager;

	/**
	 * Normal constructor.
	 */
	public AbstractConnection(ConnectionManager manager) {
		this.manager = manager;

	}

	@Override
	public Future<Boolean> send(Message message) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addListener(Object listener) {
		// list for adding the new listeners
		HashSet<ListenerContainer> msgToAdd = new HashSet<>();

		Class<?> clazz = listener.getClass();

		// gets the methods and check them if there is a listener
		for (Method method : clazz.getMethods()) {

			// first check if the annotation is set
			JUBP2Listener list = method.getAnnotation(JUBP2Listener.class);

			if (list == null) {
				continue; // if not go on with the next one
			}

			// check if the return value is boolean
			Class<?> returning = method.getReturnType();

			if (!returning.equals(Boolean.class)) {
				// if not warn and go on with the next one
				Logger.getLogger(AbstractConnection.class.getName())
						.log(Level.FINE,
								"A method ({5}) was added makred as listener, but it has the worong return value {3}, expect {4}! For object {1} into {2}",
								new Object[] { listener, this, returning.getCanonicalName(),
										Boolean.class.getCanonicalName(), method.getName() });
				continue;
			}

			// check the parameter configuration
			Class<?>[] parameter = method.getParameterTypes();

			// either it has one argument with a message
			if (parameter.length == 1 && Message.class.isAssignableFrom(parameter[0]))
				;
			// or tow argument, first a message, second a nodeConnection
			else if (parameter.length == 2
					&& (Message.class.isAssignableFrom(parameter[0]) && NodeConnection.class
							.isAssignableFrom(parameter[1])))
				;
			// any other configuration are rejected!
			else {
				// warn and go on with the next one
				Logger.getLogger(AbstractConnection.class.getName())
						.log(Level.FINE,
								"A method ({5}) was added makred as listener, but it has the worong return value {3}, expect {4}! For object {1} into {2}",
								new Object[] { listener, this, returning.getCanonicalName(),
										Boolean.class.getCanonicalName(), method.getName() });
				continue;
			}

			// add method to list
			msgToAdd.add(new ListenerContainer(listener, parameter[0].asSubclass(Message.class), method, list.level()));
		}

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
	public void removeListener(Object listener) {
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

	/**
	 * Delivers a message to the specific listeners.
	 * <p>
	 * First this method will check the message with the manager
	 * {@link ConnectionManager#checkMessage(Message, Connection)}. If it returns <code>true</code> it will be delivered
	 * to all fitting and registered listeners. Until one listener returned <code>true</code> or the end of the list is
	 * reached.
	 * <p>
	 * If a listener of a specific level returns <code>true</code> all other listener of the same level gets the message
	 * still before this method returns. At least all listeners of lower levels don't get the message.
	 * 
	 * @param msg the message to send
	 * @param node the node from which it was received
	 */
	protected void deliverMessage(Message msg, NodeConnection node) {
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
			HashMap<JUBP2Listener.Level, HashSet<ListenerContainer>> msgToSend = new HashMap<>();

			// adds a list/set for every level
			for (JUBP2Listener.Level level : JUBP2Listener.Level.values()) {
				msgToSend.put(level, new HashSet<ListenerContainer>());
			}

			// gets the listener to send to, and map them by level
			synchronized (messageListeners) {
				for (ListenerContainer container : messageListeners) {

					// check if the listener accepts the give message type
					if (container.message.isAssignableFrom(msg.getClass())) {
						msgToSend.get(container.priority).add(container);
					}
				}
			}

			// indicates if a listener returned true
			boolean consumed = false;

			// sends the message
			for (JUBP2Listener.Level level : JUBP2Listener.Level.values()) {
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

	/**
	 * Contains a listener object.
	 * <p>
	 * This class is for holding the listeners related objects together. They are the Listener instance, the message to
	 * listening for and the method signature to invoke.
	 * <p>
	 * Since it is possible that a single listener object can have multiple listener methods, each method gets its own
	 * entry of this class.
	 * <p>
	 * 
	 * @see AbstractConnection#messageListeners
	 * @author held03
	 */
	protected class ListenerContainer {

		/**
		 * The instance of the listener.
		 */
		public Object object;

		/**
		 * The message type the listener is for.
		 */
		public Class<? extends Message> message;

		/**
		 * The method of the listener to invoke.
		 * <p>
		 * This method must have the {@link JUBP2Listener} annotation, must return a boolean value and must have exactly
		 * one argument which takes a {@link Message} or a sub class/interface.
		 */
		public Method callback;

		/**
		 * The priority of the listener.
		 */
		public JUBP2Listener.Level priority;

		/**
		 * Creates a new container with given content.
		 */
		public ListenerContainer(Object object, Class<? extends Message> message, Method callback,
				JUBP2Listener.Level priority) {
			this.object = object;
			this.message = message;
			this.callback = callback;
			this.priority = priority;

		}

		/**
		 * Creates a empty instance.
		 */
		public ListenerContainer() {

		}
	}
}
