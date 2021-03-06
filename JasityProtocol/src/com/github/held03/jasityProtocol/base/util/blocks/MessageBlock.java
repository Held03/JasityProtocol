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

import java.nio.ByteBuffer;
import java.util.Arrays;


/**
 * Data block of message.
 * <p>
 * Block containing data about a specific message.
 * 
 * <pre>
 * Structure:
 * 
 * - long: message ID
 * - int: data offset
 * - int: data length
 * - byte[]: message data
 * </pre>
 * 
 * The sender sends this to transmit data of a message. These blocks are
 * controlled by {@link #BLOCK_MESSAGE} and
 * {@link #BLOCK_MESSAGE_BLOCK_FEEDBACK}. The receiver of this block has to
 * answer with a {@link #BLOCK_MESSAGE_BLOCK_FEEDBACK}.
 * 
 * @see NodeBlock#BLOCK_MESSAGE_BLOCK
 * @author held03
 */
public class MessageBlock extends NodeBlock {

	public static final int STATIC_COST = 17;

	/**
	 * The id of the message, this block relates to.
	 */
	long id = 0;

	/**
	 * The offset of the send data in the message.
	 */
	int offset;

	/**
	 * The binary data of the message.
	 */
	byte[] data = new byte[0];

	/**
	 * Create a empty ping.
	 */
	public MessageBlock() {

	}

	/**
	 * Create a new message block with given id, offset and data.
	 * 
	 * @param type the
	 * @param id
	 */
	public MessageBlock(final long id, final int offset, final byte[] data) {
		this.id = id;
		this.offset = offset;
		this.data = data;

	}

	/**
	 * Create a new message block with given id and extract the data from the
	 * given array.
	 * 
	 * @param type the
	 * @param id
	 */
	public MessageBlock(final long id, final byte[] data, final int offset, final int length) {
		this.id = id;
		this.offset = offset;
		this.data = Arrays.copyOfRange(data, offset, offset + length);

	}

	/**
	 * The id of the message, this block relates to.
	 */
	public long getId() {
		return id;
	}

	/**
	 * The offset of the send data in the message.
	 */
	public int getOffset() {
		return offset;
	}

	/**
	 * The binary data of the message.
	 */
	public byte[] getData() {
		return data;
	}

	/*
	 * (non-Javadoc)
	 * @see com.github.held03.jasityProtocol.base.util.NodeBlock#encode()
	 * - long: message ID
	 * - int: data offset
	 * - int: data length
	 * - byte[]: message data
	 */
	@Override
	public ByteBuffer encode() {
		ByteBuffer bb = ByteBuffer.allocate(getSize());

		/*
		 * Write the native type.
		 */
		bb.put(getNativeType());

		/*
		 * Write the actual data.
		 */
		bb.putLong(id);
		bb.putInt(offset);
		bb.putInt(data.length);
		bb.put(data);

		/*
		 * Flush and return data.
		 */
		bb.rewind();
		return bb;
	}

	/*
	 * (non-Javadoc)
	 * - long: message ID
	 * - int: data offset
	 * - int: data length
	 * - byte[]: message data
	 */
	@Override
	public MessageBlock decode(final ByteBuffer data) {

		/*
		 * Get the data.
		 */
		id = data.getLong();
		this.offset = data.getInt();
		int len = data.getInt();
		this.data = new byte[len];
		data.get(this.data);

		return this;
	}

	/*
	 * (non-Javadoc)
	 */
	@Override
	public int getSize() {
		/*
		 * Takes always 17 + length od data bytes:
		 * - 1 byte: native type (byte)
		 * - 8 byte: id (long)
		 * - 4 bytes: offset (int)
		 * - 4 bytes: lenght (int)
		 * - length bytes: data (byte[])
		 */
		return STATIC_COST + data.length;

	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.github.held03.jasityProtocol.base.util.blocks.NodeBlock#getNativeType
	 * ()
	 */
	@Override
	public byte getNativeType() {
		return BLOCK_MESSAGE_BLOCK;
	}

	@Override
	public String toString() {
		return "MessageBlock(" + id + ", " + offset + ", " + data.length + ")";
	}

}
