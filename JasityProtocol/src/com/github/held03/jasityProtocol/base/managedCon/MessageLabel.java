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

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
public class MessageLabel implements Future<Boolean> {

	/**
	 * The node to send the message to.
	 */
	protected NodeConnection node;

	/**
	 * The binary data of the message.
	 */
	protected ByteBuffer data;

	/**
	 * The message object.
	 */
	protected Message message;

	protected boolean isDone;

	protected boolean isCancelled;

	@Override
	public boolean isDone() {
		return isDone;
	}

	@Override
	public boolean isCancelled() {
		return isCancelled;
	}

	@Override
	public Boolean get(long val, final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		long time = 0;
		int nano = 0;

		switch (unit) {
		case DAYS:
			time = val * 24 * 3600 * 1000;
			break;
		case HOURS:
			time = val * 3600 * 1000;
			break;
		case MINUTES:
			time = val * 60 * 1000;
			break;
		case SECONDS:
			time = val * 1000;
			break;
		case MILLISECONDS:
			time = val;
			break;
		case MICROSECONDS:
			time = val / 1000;
			val -= time * 1000;
			nano = (int) (val * 1000);
			break;
		case NANOSECONDS:
			time = val / 1000 / 1000;
			val -= time * 1000 * 1000;
			nano = (int) (val);
			break;
		}


		this.wait(time, nano);
		return null;
	}

	@Override
	public Boolean get() throws InterruptedException, ExecutionException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean cancel(final boolean arg0) {
		// TODO Auto-generated method stub
		return false;
	}

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

	/**
	 * Applies all the queued filters.
	 * <p>
	 * This method applies the {@link #queuedFilters} onto this message.
	 * <p>
	 * After this call all entries of {@link #queuedFilters} will be moved to
	 * {@link #appliedFilters}.
	 */
	public void applyFilters() {
		Filter f;
		while (queuedFilters.size() > 0 && (f = queuedFilters.remove(0)) != null) {
			f.apply(data, this);
			appliedFilters.add(f);
		}
	}

}
