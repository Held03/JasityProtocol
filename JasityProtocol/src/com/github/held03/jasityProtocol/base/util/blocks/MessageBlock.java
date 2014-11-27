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
public class MessageBlock extends NodeBlock {



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

	private byte[] data2;

	/**
	 * Create a empty ping.
	 */
	public MessageBlock() {

	}

	/**
	 * Create a type and id.
	 * 
	 * @param type the
	 * @param id
	 */
	public MessageBlock(final long id, final int offset, final byte[] data) {
		this.id = id;
		this.offset = offset;
		data2 = data;

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
	public byte[] encode() {
		try (ByteArrayOutputStream out = new ByteArrayOutputStream(); DataOutputStream dout = new DataOutputStream(out)) {
			dout.writeByte(getNativeType());
			dout.writeLong(id);
			dout.writeInt(offset);
			dout.writeInt(data.length);
			dout.write(data);

			dout.flush();

			return out.toByteArray();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return new byte[0];
	}

	/*
	 * (non-Javadoc)
	 * - long: message ID
	 * - int: data offset
	 * - int: data length
	 * - byte[]: message data
	 */
	@Override
	public MessageBlock decode(final byte[] data, final int offset, final int length) {
		try (ByteArrayInputStream in = new ByteArrayInputStream(data, offset, length);
				DataInputStream dis = new DataInputStream(in)) {
			id = dis.readLong();
			this.offset = dis.readInt();
			int len = dis.readInt();
			this.data = new byte[len];
			dis.read(this.data);

			return this;

		} catch (IOException e) {
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
		 * Takes always 17 + length od data bytes:
		 * - 1 byte: native type (byte)
		 * - 8 byte: id (long)
		 * - 4 bytes: offset (int)
		 * - 4 bytes: lenght (int)
		 * - length bytes: data (byte[])
		 */
		return 17 + data.length;

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

}
