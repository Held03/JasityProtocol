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

package com.github.held03.jasityProtocol.interfaces;


/**
 * Application base node listener.
 * <p>
 * This listener is formed to be used on application side. To the feedbacks
 * about application interesting events.
 * 
 * @author held03
 */
public interface NodeListener {

	/**
	 * The connection to the remote was established.
	 * <p>
	 * The node will call this if the connection to the remote node was
	 * successfully established.
	 * 
	 * @param n the node which send the event
	 */
	public void connectionOpen(Node n);

	/**
	 * The connection to the remote was closed.
	 * <p>
	 * The node will call this if the connection to the remote node was any how
	 * closed.
	 * 
	 * @param n
	 */
	public void connectionClosed(Node n);

	/**
	 * New message received.
	 * <p>
	 * This will be invoked on any received message, whether a listener for the
	 * message was registered or not. Additional this method is called first,
	 * therefore it can be pre-filtered.
	 * <p>
	 * If this method returns <code>false</code> the message will be rejected
	 * and NOT be forward to the listeners. If it returns <code>true</code> the
	 * message will be sent to the listeners as well.
	 * <p>
	 * The default should be <code>true</code>, if used with the listeners.
	 * 
	 * @param msg the new message to accept
	 * @param con the node from which the message was received
	 * @return <code>true</code> if the given message was accepted
	 */
	public boolean newMessage(Message msg, Node node);

}
