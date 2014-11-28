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
 * Message meta data block.
 * <p>
 * A block for general messages control.
 * 
 * <pre>
 * Structure:
 * 
 * - byte: type:
 *          0: New       - start new message
 *          1: Unknown   - received block about a unknown message
 *          2: Sent      - completely sent a message
 *          3: Complete  - successfully received message
 *          4: ErrorSend - message sending fail
 *          5: WhatsUp   - too long no request
 *          6: Pending   - message is waiting in the queue
 *          7: ErrorRece - message receiving fail
 * - long: message ID
 * - int: message size or zero (only parsed on NEW)
 * </pre>
 * 
 * The sender first sends the <code>New</code> command with the related ID and a
 * CRC checksum. Afterwards the sender sends all the blocks by
 * {@link #BLOCK_MESSAGE_BLOCK}. If the receiver receives a
 * {@link #BLOCK_MESSAGE_BLOCK} without knowing the message ID, is sends an
 * <code>Unknown</code> command and the sender answers with the <code>New</code>
 * command again exactly like above. If the sender sent all block and received
 * all {@link #BLOCK_MESSAGE_BLOCK_FEEDBACK}s about them, it sends the
 * <code>Send</code> command and the receiver answers with the
 * <code>Complete</code> command.
 * <p>
 * If any when an deep error occurs or the transmission was canceled any how,
 * any one writes <code>ErrorSend</code> or <code>ErrorRece</code>. The other
 * node can answer even with <code>ErrorRece</code> or <code>ErrorSend</code>,
 * it is like the <code>Bye</code> command of the {@link #BLOCK_HELLO} block for
 * a message. If the receivers thinks the sending of message hangs up, can send
 * the <code>WhatsUp</code> command. The sender can answer differently, either
 * if the transmission was broken it answers <code>ErrorSend</code>, if there is
 * still some thing to send it answers <code>Pending</code> also if it is still
 * sending blocks. If the transmission was successful it answers
 * <code>Send</code> and the receiver has to answer like above.
 * <p>
 * <code>ErrorSend</code> is always send by the sender. <code>ErrorRece</code>
 * is always send by the receiver. This is important to distinguish if a local
 * or a remote message is canceled.
 * 
 * @see NodeBlock#BLOCK_MESSAGE
 * @author held03
 */
public class MessageB extends NodeBlock {

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
	 * message sending fail.
	 */
	public static final byte TYPE_ERROR_SEND = 4;

	/**
	 * Too long no request.
	 */
	public static final byte TYPE_WHATS_UP = 5;

	/**
	 * Message is waiting in the queue.
	 */
	public static final byte TYPE_PENDING = 6;

	/**
	 * message receiving fail.
	 */
	public static final byte TYPE_ERROR_RECIEVE = 7;

	/**
	 * The message id about it is.
	 */
	long id = 0;

	/**
	 * The size of the message or zero (only parsed on NEW).
	 */
	int size = 0;

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
	public MessageB() {

	}

	/**
	 * Creates a block with the given data.
	 * 
	 * @param type the type of this block
	 * @param id the id of the message this is about
	 */
	public MessageB(final byte type, final long id) {
		this.type = type;
		this.id = id;
	}

	/**
	 * Creates a new message block with given id and size.
	 * 
	 * @param id the id of the message this is about
	 * @param size the size of the message
	 */
	public MessageB(final long id, final int size) {
		this.type = TYPE_NEW;
		this.id = id;
		this.size = size;

	}

	/**
	 * The message id about it is.
	 */
	public long getId() {
		return id;
	}

	/**
	 * The message size or zero (only parsed on NEW).
	 * <p>
	 * Don't get confused with {@link #getSize()} which returns the size of this
	 * block, instance of the length of the data of the message.
	 */
	public int getMsgSize() {
		return size;
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
	public ByteBuffer encode() {
		ByteBuffer bb = ByteBuffer.allocate(getSize());

		/*
		 * Write the native type.
		 */
		bb.put(getNativeType());

		/*
		 * Write the actual data.
		 */
		bb.put(type);
		bb.putLong(id);
		bb.putInt(size);

		/*
		 * Flush and return data.
		 */
		bb.rewind();
		return bb;
	}

	/*
	 * (non-Javadoc)
	 */
	@Override
	public MessageB decode(final ByteBuffer data) {
		/*
		 * Get the data.
		 */
		type = data.get();
		id = data.getLong();
		size = data.getInt();

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
