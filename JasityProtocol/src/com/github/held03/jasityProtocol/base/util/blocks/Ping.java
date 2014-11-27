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
 * Ping-Pong block.
 * <p>
 * Block about sending ping pong signals to check a connection.
 * <p>
 * Every ping has an ID. A pong answer with the ID of the originally ping. So
 * every pong can be assigned to a send ping. So it is possible to send multiple
 * pings.
 * 
 * <pre>
 * Structure:
 * 
 * - byte: type:
 *          0: Ping - request
 *          1: Pong - response
 * - long: ping ID
 * </pre>
 * 
 * The sender sends <code>Ping</code> with an ID. The receiver answers with
 * <code>Pong</code> and the same ID. Both nodes can send Pings independently.
 * 
 * @see NodeBlock#BLOCK_PING
 * @author held03
 */
public class Ping extends NodeBlock {

	/**
	 * Illegal block type.
	 */
	public static final byte TYPE_ILLEGAL = -1;

	/**
	 * A ping request.
	 */
	public static final byte TYPE_PING = 0;

	/**
	 * A ping response.
	 */
	public static final byte TYPE_PONG = 1;

	/**
	 * The id of the ping.
	 */
	long id = 0;

	/**
	 * The type of this block.
	 * <p>
	 * Distinguish this type from the native type which is always the same for
	 * this kind of block.
	 */
	byte type = TYPE_ILLEGAL;

	/**
	 * Create a empty ping.
	 */
	public Ping() {

	}

	/**
	 * Create a ping with type and id.
	 * 
	 * @param type the of the ping
	 * @param id the id of the ping
	 */
	public Ping(final byte type, final long id) {
		this.type = type;
		this.id = id;

	}

	/**
	 * The id of the ping.
	 */
	public long getId() {
		return id;
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
			dout.writeLong(id);

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
	public Ping decode(final byte[] data, final int offset, final int length) {
		try (ByteArrayInputStream in = new ByteArrayInputStream(data, offset, length);
				DataInputStream dis = new DataInputStream(in)) {
			/*
			 * Get the data.
			 */
			type = dis.readByte();
			id = dis.readLong();

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
		 * - 8 bytes: id (long)
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
		return BLOCK_PING;
	}

}
