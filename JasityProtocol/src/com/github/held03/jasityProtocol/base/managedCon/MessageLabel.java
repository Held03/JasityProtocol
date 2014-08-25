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

import java.util.LinkedList;
import java.util.List;

import com.github.held03.jasityProtocol.interfaces.Message;
import com.github.held03.jasityProtocol.interfaces.NodeConnection;


/**
 * Describes a message, and holds additional informations about it.
 * <p>
 * This label relates always to a specific message as object, as well as a
 * binary coded message.
 * 
 * @author held03
 */
public class MessageLabel {

	/**
	 * The node to send the message to.
	 */
	protected NodeConnection node;

	/**
	 * The binary data of the message.
	 */
	protected byte[] data;

	/**
	 * The message object.
	 */
	protected Message message;

	/**
	 * List of filters already applied to the binary message.
	 * <p>
	 * The order of the list represents the order in which they was applied.
	 * <p>
	 * Notice if a filter is currently in progress on this message, it will be
	 * not listed neither in {@link #appliedFilters} nor in
	 * {@link #queuedFilters}
	 * 
	 * @see #queuedFilters
	 */
	protected List<Filter> appliedFilters = new LinkedList<Filter>();

	/**
	 * List of Queued filters.
	 * <p>
	 * The order of the list represents the order in which they will be applied.
	 * <p>
	 * Notice if a filter is currently in progress on this message, it will be
	 * not listed neither in {@link #appliedFilters} nor in
	 * {@link #queuedFilters}
	 * 
	 * @see #appliedFilters
	 */
	protected List<Filter> queuedFilters = new LinkedList<Filter>();

	/**
	 * Create a message label for a specific message.
	 */
	public MessageLabel(final Message msg) {
		message = msg;
	}

	/**
	 * Create a message label for a specific message and a list of filters.
	 * <p>
	 * The order of the list will be used as the order to apply the filters.
	 */
	public MessageLabel(final Message msg, final List<Filter> filters) {
		message = msg;
		queuedFilters.addAll(filters);
	}

}
