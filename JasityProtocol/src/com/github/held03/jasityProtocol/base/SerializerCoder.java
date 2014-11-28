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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import com.github.held03.jasityProtocol.interfaces.BinaryMessage;
import com.github.held03.jasityProtocol.interfaces.Message;
import com.github.held03.jasityProtocol.interfaces.MessageCoder;


/**
 * @author held03
 */
public class SerializerCoder implements MessageCoder {

	private static final byte BINARY_MESSAGE_PREFIX = (byte) 0xBB;

	/**
	 * 
	 */
	public SerializerCoder() {
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.github.held03.jasityProtocol.base.managedCon.MessageCoder#codeMessage
	 * (com.github.held03.jasityProtocol.interfaces.Message)
	 */
	@Override
	public ByteBuffer encodeMessage(final Message msg) {
		if (msg instanceof BinaryMessage) {
			ByteBuffer buf = ((BinaryMessage) msg).codeMessage();

			byte[] name = null;

			try {
				name = msg.getClass().getCanonicalName().getBytes("UTF-8");
			} catch (UnsupportedEncodingException e) {
				// never called
			}

			ByteBuffer buffer = ByteBuffer.allocate(1 + 2 + name.length + buf.remaining());

			buffer.put(BINARY_MESSAGE_PREFIX);

			buffer.putShort((short) name.length);

			buffer.put(name);

			buffer.put(buf);

			return buffer;
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		try {
			ObjectOutputStream oos = new ObjectOutputStream(baos);

			oos.writeObject(msg);

			oos.flush();

			byte[] bytes = baos.toByteArray();

			ByteBuffer buf = ByteBuffer.allocate(bytes.length);

			buf.put(bytes);

			buf.rewind();

			return buf;

		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.github.held03.jasityProtocol.base.managedCon.MessageCoder#decodeMessage
	 * (java.nio.ByteBuffer)
	 */
	@Override
	public Message decodeMessage(final ByteBuffer buffer) {

		if (buffer.get() == BINARY_MESSAGE_PREFIX) {
			byte[] name = new byte[buffer.getShort()];

			buffer.get(name);

			String s = null;

			try {
				s = new String(name, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// never called
			}

			try {
				Object o = Class.forName(s).newInstance();

				if (o instanceof BinaryMessage) {
					BinaryMessage msg = (BinaryMessage) o;

					msg.decodeMessage(buffer);

					return msg;
				}

			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}

			return null;
		}

		buffer.rewind();

		byte[] buf = new byte[buffer.remaining()];

		buffer.get(buf);

		ByteArrayInputStream baos = new ByteArrayInputStream(buf);

		try {
			ObjectInputStream ois = new ObjectInputStream(baos);

			Object o = ois.readObject();

			if (o instanceof Message) {
				return (Message) o;
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return null;
	}

}
