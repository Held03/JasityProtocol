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

import java.nio.ByteBuffer;


/**
 * Self coding and decoding message.
 * <p>
 * This represents a message as described in {@link Message}, but this messages
 * will have a facility to code and decode them self to and from binary forms.
 * <p>
 * Notice that such a message need to have at least an empty constructor, this
 * means that the constructor has no arguments. For safety it is recommend to
 * declare it explicit. If not the reassembling will fail.
 * <p>
 * It is recommend that they can at least decode messages of old implementation
 * and/or ignore new/unknown arguments.
 * <p>
 * So this can be very complex but offers the ability to code a message as good
 * as possible and save traffic.
 * <p>
 * If used the {@link Connection} will call the {@link #codeMessage()} method on
 * the object to send and gets the {@link ByteBuffer} to send. The returned
 * {@link ByteBuffer} can have any length, if it is too long the
 * {@link Connection} will break it into smaller packages, send them separately
 * and finally reassemble them on the receiving side.
 * <p>
 * If received a empty instance will be created an the
 * {@link #decodeMessage(ByteBuffer)} method on the empty instance will be
 * called. That method has to read and set up all the fields from the given and
 * reassembled {@link ByteBuffer}.
 * 
 * @author held03
 */
public interface BinaryMessage extends Message {

	/**
	 * Codes the message into a ByteBuffer.
	 * <p>
	 * This can return a {@link ByteBuffer} of any length.
	 * 
	 * @return the coded message as ByteBuffer
	 */
	public ByteBuffer codeMessage();

	/**
	 * Decodes a message from a ByteBuffer.
	 * <p>
	 * This method will be called on a empty (new created) instance.
	 * 
	 * @param data
	 */
	public void decodeMessage(ByteBuffer data);
}
