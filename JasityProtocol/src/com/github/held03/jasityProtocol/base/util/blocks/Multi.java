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
 * A block containing other blocks.
 * 
 * <pre>
 * Structure:
 * 
 * - int: count of sub blocks
 *  { for every block
 *   - int: block size
 *   - block data
 *  }
 * </pre>
 * 
 * Can be used to compose multiple block into on sent block.
 * 
 * @see NodeBlock#BLOCK_MULTIBLOCK
 * @author held03
 */
public class Multi extends NodeBlock {

	public static final int STATIC_COST = 5;
	public static final int ADDITIONAL_COST = 4;

	/**
	 * The sub blocks contained by this block.
	 */
	NodeBlock[] subBlocks = new NodeBlock[0];

	/**
	 * Creates an empty multi block.
	 */
	public Multi() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Creates a multi block with given sub blocks.
	 */
	public Multi(final NodeBlock... blocks) {
		subBlocks = blocks;
	}

	/**
	 * The sub blocks contained by this block.
	 */
	public NodeBlock[] getSubBlocks() {
		return subBlocks;
	}

	/*
	 * (non-Javadoc)
	 * @see com.github.held03.jasityProtocol.base.util.blocks.NodeBlock#encode()
	 */
	@Override
	public ByteBuffer encode() {
		ByteBuffer bb = ByteBuffer.allocate(getSize());

		bb.put(getNativeType());

		/*
		 * Get count and create an appropriated array.
		 */
		bb.putInt(subBlocks.length);

		ByteBuffer buf;

		/*
		 * Decode all entries.
		 */
		for (int i = 0; i < subBlocks.length; i++) {
			/*
			 * Get encoding of the entry
			 */
			buf = subBlocks[i].encode();

			/*
			 * Write length and data out.
			 */
			bb.putInt(buf.remaining());
			bb.put(buf);
		}

		bb.rewind();
		return bb;
	}

	/*
	 * (non-Javadoc)
	 */
	@Override
	public Multi decode(final ByteBuffer data) {

		/*
		 * Get count and create an appropriated array.
		 */
		int count = data.getInt();
		subBlocks = new NodeBlock[count];

		int len;
		int limit = data.limit();
		int nextPos;

		/*
		 * Decode all entries.
		 */
		for (int i = 0; i < count; i++) {
			/*
			 * Get length and content of the entry
			 */
			len = data.getInt();
			nextPos = data.position() + len;
			data.limit(nextPos);

			/*
			 * Add it to array.
			 */
			subBlocks[i] = NodeBlock.decodeBlock(data);

			data.position(nextPos);
			data.limit(limit);
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
		 * Own size 5 bytes plus 4 bytes for every entry
		 * and finally the sum of the sizes of the entries.
		 */
		int size = STATIC_COST; // 5

		for (NodeBlock block : subBlocks) {
			size += ADDITIONAL_COST; // 4
			size += block.getSize();
		}

		return size;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.github.held03.jasityProtocol.base.util.blocks.NodeBlock#getNativeType
	 * ()
	 */
	@Override
	public byte getNativeType() {
		return BLOCK_MULTIBLOCK;
	}

}
