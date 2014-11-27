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

package com.github.held03.jasityProtocol.base.util.blocks;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


/**
 * Handshake block.
 * <p>
 * Block about establishing and breaking connections. This is comparable with
 * the handshake in TCP. The sender send always its version code to let the
 * receiver handle compatibility.
 * 
 * <pre>
 * Structure:
 * 
 * - byte: type:
 *          0: Knock - request connection
 *          1: Hello - connection accepted
 *          2: Busy  - connection refused
 *          3: Bye   - connection closed
 * - long: version code
 * </pre>
 * 
 * Opening socket have first to send the <code>Knock</code> order. It's allowed
 * that both send it. The receiver of such an order has to answer with either
 * <code>Hello</code> to accept or <code>Busy</code> to deny.
 * <p>
 * If any one wants to break the connection it sends <code>Bye</code>. Usually,
 * the receiver answer with <code>Bye</code> but that is not necessary. Due this
 * lazy process it is possible that one node sends the <code>Bye</code> command
 * but the other node never receives it. Therefore it is useful to check
 * frequently the ping time by {@link #BLOCK_PING} and if the ping fails
 * multiple times the connection should be threat as dead.
 * 
 * @see NodeBlock#BLOCK_HELLO
 * @author held03
 */
public class Hello extends NodeBlock {

	/**
	 * Illegal block type.
	 */
	public static final byte TYPE_ILLEGAL = -1;

	/**
	 * Request connection.
	 */
	public static final byte TYPE_KNOCK = 0;

	/**
	 * Connection accepted.
	 */
	public static final byte TYPE_HELLO = 1;

	/**
	 * Connection refused.
	 */
	public static final byte TYPE_BUSY = 2;

	/**
	 * connection closed
	 */
	public static final byte TYPE_BYE = 3;

	/**
	 * The version of the node which sent this.
	 */
	long version = 0;

	/**
	 * The type of this block.
	 * <p>
	 * Distinguish this type from the native type which is always the same for
	 * this kind of block.
	 */
	byte type = TYPE_ILLEGAL;

	/**
	 * Creates empty hello block.
	 */
	public Hello() {

	}

	/**
	 * Creates a specific hello block.
	 * 
	 * @param type the hello type.
	 * @param version the node version of the sender
	 */
	public Hello(final byte type, final long version) {
		this.type = type;
		this.version = version;

	}

	/**
	 * The version of the node which sent this.
	 */
	public long getVersion() {
		return version;
	}

	/**
	 * The type of this block.
	 * <p>
	 * Distinguish this type from the native type which is always the same for
	 * this kind of block.
	 */
	public byte getType() {
		return type;
	}

	/*
	 * (non-Javadoc)
	 * @see com.github.held03.jasityProtocol.base.util.NodeBlock#encode()
	 */
	@Override
	public byte[] encode() {
		try (ByteArrayOutputStream out = new ByteArrayOutputStream(); DataOutputStream dout = new DataOutputStream(out)) {
			/*
			 * Write the native type.
			 */
			dout.writeByte(getNativeType());

			/*
			 * Write the actual data.
			 */
			dout.writeByte(type);
			dout.writeLong(version);

			/*
			 * Flush and return data.
			 */
			dout.flush();

			return out.toByteArray();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return new byte[0];
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.github.held03.jasityProtocol.base.util.blocks.NodeBlock#decode(byte
	 * [], int, int)
	 */
	@Override
	public Hello decode(final byte[] data, final int offset, final int length) {
		try (ByteArrayInputStream in = new ByteArrayInputStream(data, offset, length);
				DataInputStream dis = new DataInputStream(in)) {
			/*
			 * Get the data.
			 */
			type = dis.readByte();
			version = dis.readLong();

			return this;

		} catch (IOException e) {
			e.printStackTrace();
		}

		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.github.held03.jasityProtocol.base.util.blocks.NodeBlock#getSize()
	 */
	@Override
	public int getSize() {
		/*
		 * Takes always 10 bytes:
		 * - 1 byte: native type (byte)
		 * - 1 byte: type (byte)
		 * - 8 bytes: version (long)
		 */
		return 10;

	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.github.held03.jasityProtocol.base.util.blocks.NodeBlock#getNativeType
	 * ()
	 */
	@Override
	public byte getNativeType() {
		return BLOCK_HELLO;
	}

}
