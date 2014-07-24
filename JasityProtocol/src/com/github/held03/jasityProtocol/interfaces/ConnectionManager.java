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
 * Defines a class which generally manages the connection and the message transfer a bit.
 * <p>
 * This is the callback interface of the {@link Connection}. General it reference either to a {@link Server} or to a
 * {@link Client}.
 * <p>
 * 
 * @author held03
 */
public interface ConnectionManager {

	/**
	 * New message received to check.
	 * <p/>
	 * This is invoked if the a message was received to the connection.
	 * <p/>
	 * This will be invoke on any message, whether a listener for the message was registered or not. Additional this
	 * method is called first, therefore it can be pre-filtered.
	 * <p/>
	 * If this method returns <code>false</code> the message will be rejected and NOT be forward to the listeners. If it
	 * returns <code>true</code> the message will be sent to the listeners.
	 * <p/>
	 * The default should be <code>true</code>, if used with the listeners.
	 * <p>
	 * This call should be forwarded to the {@link ClientListener#newMessage(Message)} or the
	 * {@link ServerListener#newMessage(Message)}
	 * <p/>
	 * 
	 * @param msg the new message to accept
	 * @param con the node the message was received from
	 * @return <code>true</code> if the given message was accepted
	 */
	public boolean checkMessage(Message msg, NodeConnection con);
}
