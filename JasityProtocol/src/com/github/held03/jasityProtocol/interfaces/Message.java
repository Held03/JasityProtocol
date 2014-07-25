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
 * This represents a transferable message.
 * 
 * @author held03
 */
public interface Message {

	/**
	 * The kind this message will be transmitted.
	 * 
	 * @author held03
	 */
	public enum Transport {
		/**
		 * Prevents any modification of the message.
		 * <p>
		 * Messages defined as this, will be send without passing it though
		 * filters.
		 */
		PRE_FILLTERED,

		/**
		 * Sends message anyway.
		 * <p>
		 * This sends the message as possible with applies filters, but if not
		 * available with out passing it though filters.
		 */
		OPTIONALLY,

		/**
		 * Normal message sending.
		 * <p>
		 * Sends messages only if filters initiated. This means NOT that any
		 * filter must be preset, but if then the message must pass throug it.
		 */
		NORMAL
	}

	/**
	 * The priority a message can have.
	 * 
	 * @author held03
	 */
	public enum Priority {
		/**
		 * A system or the connection message.
		 * <p>
		 * This should NOT be used for any common or not connection depending
		 * message.
		 */
		SYSTEM,

		/**
		 * A filter message.
		 * <p>
		 * This message may be used by filters to do some status update or
		 * similar.
		 */

		FILTER,

		/**
		 * A very important message.
		 * <p>
		 * This is a common message priority.
		 * <p>
		 * This message will be send first of the common message priorities.
		 */
		URGENT,

		/**
		 * A important message.
		 * <p>
		 * This is a common message priority.
		 * <p>
		 * This message will be send as early as possible.
		 */
		HIGH,

		/**
		 * A normal message.
		 * <p>
		 * This is a common message priority.
		 * <p>
		 * This message will be send if no more important message is waiting.
		 */
		NORMAL,

		/**
		 * A very unimportant message.
		 * <p>
		 * This is a common message priority.
		 * <p>
		 * This message will be send if no more important message is waiting.
		 */
		LOW,

		/**
		 * A filling message.
		 * <p>
		 * This message or its parts are sent if the connection is idle, And
		 * will be interrupted if another message should be send.
		 */
		FILLER
	}

	/**
	 * Gets the transport kind of a message.
	 * 
	 * @return
	 */
	public Transport getTransport();
}
