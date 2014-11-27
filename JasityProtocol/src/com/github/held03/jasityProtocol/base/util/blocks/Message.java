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
public class Message extends NodeBlock {

	/**
	 * Illegal block type.
	 */
	public static final byte TYPE_ILLEGAL = -1;

	/**
	 * Start a new message.
	 */
	public static final byte TYPE_NEW = 0;

	/**
	 * Received block about a unknown message.
	 */
	public static final byte TYPE_UNKNOWN = 1;

	/**
	 * Completely sent a message.
	 */
	public static final byte TYPE_SENT = 2;

	/**
	 * Successfully received message.
	 */
	public static final byte TYPE_COMPLETE = 3;

	/**
	 * Message fail [TODO].
	 */
	public static final byte TYPE_ERROR = 4;

	/**
	 * Too long no request.
	 */
	public static final byte TYPE_WHATS_UP = 5;

	/**
	 * Message is waiting in the queue.
	 */
	public static final byte TYPE_PENDING = 6;

	/**
	 * The message id about it is.
	 */
	long id = 0;

	/**
	 * CRC checksum or zero (only parsed on NEW).
	 */
	long crc = 0;

	/**
	 * The type of this block.
	 * <p>
	 * Distinguish this type from the native type which is always the same for
	 * this kind of block.
	 */
	byte type = TYPE_ILLEGAL;

	/**
	 * Creates an empty message.
	 */
	public Message() {

	}

	/**
	 * Creates a block with the given data.
	 * 
	 * @param type the type of this block
	 * @param id the id of the message this is about
	 */
	public Message(final byte type, final long id) {
		this.type = type;
		this.id = id;
	}

	/**
	 * Creates a new message block with given id and crc.
	 * 
	 * @param id the id of the message this is about
	 * @param crc the crc check sum of the message
	 */
	public Message(final long id, final long crc) {
		this.type = TYPE_NEW;
		this.id = id;
		this.crc = crc;

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
			dout.writeLong(crc);

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
	 */
	@Override
	public Message decode(final byte[] data, final int offset, final int length) {
		try (ByteArrayInputStream in = new ByteArrayInputStream(data, offset, length);
				DataInputStream dis = new DataInputStream(in)) {
			type = dis.readByte();
			id = dis.readLong();
			crc = dis.readLong();

			return this;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return this;
	}

	/*
	 * (non-Javadoc)
	 */
	@Override
	public int getSize() {
		/*
		 * Takes always 18 bytes:
		 * - 1 byte: native type (byte)
		 * - 1 byte: type (byte)
		 * - 8 bytes: id (long)
		 * - 8 bytes: id (long)
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
		return BLOCK_MESSAGE;
	}

}
