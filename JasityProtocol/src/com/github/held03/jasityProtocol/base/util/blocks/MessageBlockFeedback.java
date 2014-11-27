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
public class MessageBlockFeedback extends NodeBlock {


	/**
	 * Illegal block type.
	 */
	public static final byte TYPE_ILLEGAL = -1;

	/**
	 * A ping request.
	 */
	public static final byte TYPE_ACKNOWLEDGE = 0;

	/**
	 * A ping response.
	 */
	public static final byte TYPE_REPEAT = 1;

	/**
	 * The id of this block relates to.
	 */
	long id = 0;

	/**
	 * The data offset of the received block.
	 */
	int offset;

	/**
	 * The data length of the received block.
	 */
	int length;

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
	public MessageBlockFeedback() {

	}

	/**
	 * Create a type and id.
	 * 
	 * @param type the
	 * @param id
	 */
	public MessageBlockFeedback(final byte type, final long id, final int offset, final int length) {
		this.type = type;
		this.id = id;
		this.offset = offset;
		this.length = length;

	}

	/*
	 * (non-Javadoc)
	 * @see com.github.held03.jasityProtocol.base.util.NodeBlock#encode()
	 * - long: message ID to answer to
	 * - int: data offset
	 * - int: data length
	 * - byte: answer:
	 * 0: Acknowledge - get successfully block
	 * 1: Repeat - resent block
	 */
	@Override
	public byte[] encode() {
		try (ByteArrayOutputStream out = new ByteArrayOutputStream(); DataOutputStream dout = new DataOutputStream(out)) {
			dout.writeByte(getNativeType());
			dout.writeLong(id);
			dout.writeInt(this.offset);
			dout.writeInt(this.length);
			dout.writeByte(type);

			dout.flush();

			return out.toByteArray();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new byte[0];
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.github.held03.jasityProtocol.base.util.blocks.NodeBlock#decode(byte
	 * [], int, int)
	 * - long: message ID to answer to
	 * - int: data offset
	 * - int: data length
	 * - byte: answer:
	 * 0: Acknowledge - get successfully block
	 * 1: Repeat - resent block
	 */
	@Override
	public MessageBlockFeedback decode(final byte[] data, final int offset, final int length) {
		try (ByteArrayInputStream in = new ByteArrayInputStream(data, offset, length);
				DataInputStream dis = new DataInputStream(in)) {
			id = dis.readLong();
			this.offset = dis.readInt();
			this.length = dis.readInt();
			type = dis.readByte();

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
		 * Takes always 18 bytes:
		 * - 1 byte: native type (byte)
		 * - 8 bytes: id (long)
		 * - 4 bytes: offset (int)
		 * - 4 bytes: length (int)
		 * - 1 byte: type (byte)
		 */
		return 18;

	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.github.held03.jasityProtocol.base.util.blocks.NodeBlock#getNativeType
	 * ()
	 */
	@Override
	public byte getNativeType() {
		return BLOCK_MESSAGE_BLOCK_FEEDBACK;
	}

}
