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

package com.github.held03.jasityProtocol.base.util;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import com.github.held03.jasityProtocol.interfaces.BinaryMessage;


/**
 * @author held03
 */
public class StringMessage implements BinaryMessage {

	String text;

	/**
	 * 
	 */
	public StringMessage() {
		text = "";
	}

	/**
	 * 
	 */
	public StringMessage(final String text) {
		this.text = text;

	}

	/*
	 * (non-Javadoc)
	 * @see com.github.held03.jasityProtocol.interfaces.Message#getTransport()
	 */
	@Override
	public Transport getTransport() {
		return Transport.NORMAL;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.github.held03.jasityProtocol.interfaces.BinaryMessage#codeMessage()
	 */
	@Override
	public ByteBuffer codeMessage() {
		ByteBuffer bb;

		try {
			bb = ByteBuffer.wrap(text.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			bb = ByteBuffer.wrap(text.getBytes());
		}

		return bb;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.github.held03.jasityProtocol.interfaces.BinaryMessage#decodeMessage
	 * (java.nio.ByteBuffer)
	 */
	@Override
	public void decodeMessage(final ByteBuffer data) {
		byte[] buf = new byte[data.remaining()];
		data.get(buf);

		try {
			text = new String(buf, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			text = new String(buf);
			e.printStackTrace();
		}
	}

	public void setText(final String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	@Override
	public boolean equals(final Object o) {
		if (o instanceof StringMessage) {
			StringMessage sm = (StringMessage) o;

			return sm.text.equals(text);
		}

		return false;
	}

}
