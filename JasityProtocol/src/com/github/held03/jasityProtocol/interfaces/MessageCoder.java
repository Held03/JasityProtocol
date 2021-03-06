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
 * Enables the managed connection to code and decode a message.
 * <p>
 * This interface declares a message coder/decoder, which can code a
 * {@link Message} into a {@link ByteBuffer} and such a {@link ByteBuffer} back
 * into a {@link Message}.
 * <p>
 * Every MessageCode has to handle {@link BinaryMessage}s. It is necessary to
 * add to such a message a identity to distinguish which message it codes to
 * decode it properly.
 * 
 * @author held03
 */
public interface MessageCoder {

	public ByteBuffer encodeMessage(Message msg);

	public Message decodeMessage(ByteBuffer buffer);
}
