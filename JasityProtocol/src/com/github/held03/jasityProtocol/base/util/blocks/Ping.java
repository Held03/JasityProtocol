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
	 * Create a type and id.
	 * 
	 * @param type the
	 * @param id
	 */
	public Ping(final byte type, final long id) {
		this.type = type;
		this.id = id;

	}

	/*
	 * (non-Javadoc)
	 * @see com.github.held03.jasityProtocol.base.util.NodeBlock#encode()
	 */
	@Override
	public byte[] encode() {
		try (ByteArrayOutputStream out = new ByteArrayOutputStream(); DataOutputStream dout = new DataOutputStream(out)) {
			dout.writeByte(getNativeType());
			dout.writeByte(type);
			dout.writeLong(id);

			dout.flush();

			return out.toByteArray();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new byte[0];
	}

	@Override
	public Ping decode(final byte[] data, final int offset, final int length) {
		try (ByteArrayInputStream in = new ByteArrayInputStream(data, offset, length);
				DataInputStream dis = new DataInputStream(in)) {
			type = dis.readByte();
			id = dis.readLong();

			return this;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return this;
	}

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
