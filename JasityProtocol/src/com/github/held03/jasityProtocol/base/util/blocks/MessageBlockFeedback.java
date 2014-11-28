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


/**
 * Feedback block for a message.
 * <p>
 * This sends the receiver of an block back to the sender to indicate if a block
 * was received or not, if no answer was send after a specific time the block
 * will be send again.
 * 
 * <pre>
 * Structure:
 * 
 * - long: message ID to answer to
 * - int: data offset
 * - int: data length
 * - byte: answer:
 *         0: Acknowledge - get successfully block
 *         1: Repeat      - resent block
 * </pre>
 * 
 * The receiver of a {@link #BLOCK_MESSAGE_BLOCK} answers with this block either
 * by sending the <code>Acknowledge</code> command on success or the
 * <code>Repeat</code> if something failed.
 * 
 * @see NodeBlock#BLOCK_MESSAGE_BLOCK_FEEDBACK
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

	/**
	 * The id of this block relates to.
	 */
	public long getId() {
		return id;
	}

	/**
	 * The data offset of the received block.
	 */
	public int getOffset() {
		return offset;
	}

	/**
	 * The data length of the received block.
	 */
	public int getLength() {
		return length;
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
	 * - long: message ID to answer to
	 * - int: data offset
	 * - int: data length
	 * - byte: answer:
	 * 0: Acknowledge - get successfully block
	 * 1: Repeat - resent block
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
		bb.putInt(this.offset);
		bb.putInt(this.length);
		bb.put(type);

		/*
		 * Flush and return data.
		 */
		bb.rewind();
		return bb;
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
	public MessageBlockFeedback decode(final ByteBuffer data) {

		/*
		 * Get the data.
		 */
		id = data.getLong();
		this.offset = data.getInt();
		this.length = data.getInt();
		type = data.get();

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
