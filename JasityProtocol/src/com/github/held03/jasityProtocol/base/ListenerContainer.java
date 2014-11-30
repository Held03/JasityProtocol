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

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.held03.jasityProtocol.interfaces.Connection;
import com.github.held03.jasityProtocol.interfaces.JPListener;
import com.github.held03.jasityProtocol.interfaces.Message;
import com.github.held03.jasityProtocol.interfaces.Node;


/**
 * Contains a listener object.
 * <p>
 * It can be a listener for a {@link Connection} or for a {@link NodeConnection}
 * <p>
 * This class is for holding the listeners related objects together. They are
 * the Listener instance, the message to listening for and the method signature
 * to invoke.
 * <p>
 * Since it is possible that a single listener object can have multiple listener
 * methods, each method gets its own entry of this class.
 * 
 * @see AbstractConnection#messageListeners
 * @author held03
 */
public class ListenerContainer {

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
	 * This method must have the {@link JPListener} annotation, must return a
	 * boolean value and must have exactly one argument which takes a
	 * {@link Message} or a sub class/interface.
	 */
	public Method callback;

	/**
	 * The priority of the listener.
	 */
	public JPListener.Level priority;

	/**
	 * Creates a new container with given content.
	 */
	public ListenerContainer(final Object object, final Class<? extends Message> message, final Method callback,
			final JPListener.Level priority) {
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

	/**
	 * Gets a all listeners of the given object.
	 * <p>
	 * This method get all listeners as described at {@link JPListener} of the
	 * give object.
	 * <p>
	 * This method will check all methods of the given object to match the
	 * precondition described in {@link JPListener}. If any found, it will be
	 * added to the returned set. If more found they are all added. If no one
	 * found it will return with a empty set.
	 * 
	 * @param listener the object to get the listeners from
	 * @return a set of all valid listener methods
	 */
	public static Set<ListenerContainer> getListeners(final Object listener) {
		// list for adding the new listeners
		HashSet<ListenerContainer> listeners = new HashSet<>();

		Class<?> clazz = listener.getClass();

		// gets the methods and check them if there is a listener
		for (Method method : clazz.getMethods()) {

			// first check if the annotation is set
			JPListener list = method.getAnnotation(JPListener.class);

			if (list == null) {
				continue; // if not go on with the next one
			}

			// check if the return value is boolean
			Class<?> returning = method.getReturnType();

			if (!returning.equals(Boolean.class)) {
				// if not warn and go on with the next one
				Logger.getLogger(ListenerContainer.class.getName()).log(
						Level.INFO,
						"A method ({3}) was marked as listener, but it has the worong return value {1},"
								+ " expect {2}! For object {0}",
						new Object[] { listener, returning.getCanonicalName(), Boolean.class.getCanonicalName(),
								method.getName() });
				continue;
			}

			// check the parameter configuration
			Class<?>[] parameter = method.getParameterTypes();

			// either it has one argument with a message
			if (parameter.length == 1 && Message.class.isAssignableFrom(parameter[0]))
				;
			// or tow argument, first a message, second a node
			else if (parameter.length == 2
					&& (Message.class.isAssignableFrom(parameter[0]) && Node.class.isAssignableFrom(parameter[1])))
				;
			// any other configuration are rejected!
			else {
				// warn and go on with the next one
				Logger.getLogger(ListenerContainer.class.getName())
						.log(Level.INFO,
								"A method ({1}) was marked as listener, but it has the worong parameter cofiguration! For object {0}",
								new Object[] { listener, method.getName() });
				continue;
			}

			method.setAccessible(true);

			// add method to list
			listeners
					.add(new ListenerContainer(listener, parameter[0].asSubclass(Message.class), method, list.level()));
		}

		return listeners;
	}
}
