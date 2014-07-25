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
 * Allows the receiving of client related events.
 * <p>
 * This interface is important to implement to receive client events. It gives
 * the client the ability to communicate with the user program.
 * 
 * @author held03
 */
public interface ClientListener {

	/**
	 * Established connection to server.
	 * <p>
	 * This is invoked if the client established successfully a connection to a
	 * server.
	 * 
	 * @return the established connection
	 */
	public void connected();

	/**
	 * The connection to the server was lost.
	 * <p>
	 * This is invoked if the connection to the server breaks/ends anyhow.
	 */
	public void connectionLost();

	/**
	 * New message received.
	 * <p>
	 * This is invoked if the server sends a message to this client.
	 * <p>
	 * This will be invoke on any message, whether a listener for the message
	 * was registered or not. Additional this method is called first, therefore
	 * it can be pre-filtered.
	 * <p>
	 * If this method returns <code>false</code> the message will be rejected
	 * and NOT be forward to the listeners. If it returns <code>true</code> the
	 * message will be sent to the listeners.
	 * <p>
	 * The default should be <code>true</code>, if used with the listeners.
	 * 
	 * @param msg the new message to accept
	 * @return <code>true</code> if the given message was accepted
	 */
	public boolean newMessage(Message msg);
}
