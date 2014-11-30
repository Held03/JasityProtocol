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
import java.util.Random;


/**
 * Ignoring block.
 * 
 * <pre>
 * Structure:
 * 
 * - int: size
 * - byte[]: data(ignore-able)
 * </pre>
 * 
 * Ignoring data.
 * Can have purpose for cipher stuff.
 * 
 * @see NodeBlock#BLOCK_IGNORE
 * @author held03
 */
public class Ignore extends NodeBlock {

	/**
	 * The data of the block.
	 */
	byte[] data = new byte[0];

	/**
	 * Create a empty ignoring block.
	 */
	public Ignore() {

	}

	/**
	 * Create a ignoring block with the given data.
	 * <p>
	 * It will contain pseudo-randomly filled bytes.
	 * 
	 * @param size the length of data which it should contain
	 */
	public Ignore(final int size) {
		data = new byte[size];

		Random ran = new Random();

		ran.nextBytes(data);
	}

	/**
	 * Create a specific ignoring block.
	 * 
	 * @param data the data it contains
	 */
	public Ignore(final byte[] data) {
		this.data = data;
	}

	/**
	 * The data of the block.
	 */
	public byte[] getData() {
		return data;
	}

	/*
	 * (non-Javadoc)
	 * @see com.github.held03.jasityProtocol.base.util.NodeBlock#encode()
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
	 * @see
	 * com.github.held03.jasityProtocol.base.util.blocks.NodeBlock#decode(byte
	 * [], int, int)
	 */
	@Override
	public Ignore decode(final ByteBuffer data) {
		/*
		 * Get the data.
		 */
		int len = data.getInt();

		this.data = new byte[len];

		data.get(this.data);

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
		 * Takes always 4 + length of data bytes:
		 * - 1 byte: native type (byte)
		 * - 4 byte: length (int)
		 * - length bytes: data (byte[])
		 */
		return 5 + data.length;

	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.github.held03.jasityProtocol.base.util.blocks.NodeBlock#getNativeType
	 * ()
	 */
	@Override
	public byte getNativeType() {
		return BLOCK_IGNORE;
	}

	@Override
	public String toString() {
		return "Ignore(" + data.length + ")";
	}

}
