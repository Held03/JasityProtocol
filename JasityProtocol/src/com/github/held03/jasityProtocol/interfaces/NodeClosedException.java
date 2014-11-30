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
 * @author held03
 */
public class NodeClosedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7756345562666184355L;

	/**
	 * 
	 */
	public NodeClosedException() {

	}

	/**
	 * @param message
	 */
	public NodeClosedException(final String message) {
		super(message);

	}

	/**
	 * @param cause
	 */
	public NodeClosedException(final Throwable cause) {
		super(cause);

	}

	/**
	 * @param message
	 * @param cause
	 */
	public NodeClosedException(final String message, final Throwable cause) {
		super(message, cause);

	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public NodeClosedException(final String message, final Throwable cause, final boolean enableSuppression,
			final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);

	}

}
