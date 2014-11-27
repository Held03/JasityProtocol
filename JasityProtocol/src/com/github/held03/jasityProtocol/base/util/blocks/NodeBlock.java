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
import java.io.DataInputStream;
import java.io.IOException;

import com.github.held03.jasityProtocol.interfaces.Node;


/**
 * Represents a {@link Node} block.
 * <p>
 * These are basic blocks to control the base system of a node.
 * 
 * @author held03
 */
public abstract class NodeBlock {

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
	 * @see Multi
	 */
	public static final byte BLOCK_MULTIBLOCK = -1;

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
	 */
	public static final byte BLOCK_IGNORE = 0;

	/**
	 * Handshake block.
	 * <p>
	 * Block about establishing and breaking connections. This is comparable
	 * with the handshake in TCP. The sender send always its version code to let
	 * the receiver handle compatibility.
	 * 
	 * <pre>
	 * Structure:
	 * 
	 * - byte: type:
	 *          0: Knock - request connection
	 *          1: Hello - connection accepted
	 *          2: Busy  - connection refused
	 *          3: Bye   - connection closed
	 * - long: version code
	 * </pre>
	 * 
	 * Opening socket have first to send the <code>Knock</code> order. It's
	 * allowed that both send it. The receiver of such an order has to answer
	 * with either <code>Hello</code> to accept or <code>Busy</code> to deny.
	 * <p>
	 * If any one wants to break the connection it sends <code>Bye</code>.
	 * Usually, the receiver answer with <code>Bye</code> but that is not
	 * necessary. Due this lazy process it is possible that one node sends the
	 * <code>Bye</code> command but the other node never receives it. Therefore
	 * it is useful to check frequently the ping time by {@link #BLOCK_PING} and
	 * if the ping fails multiple times the connection should be threat as dead.
	 */
	public static final byte BLOCK_HELLO = 1;

	/**
	 * Ping-Pong block.
	 * <p>
	 * Block about sending ping pong signals to check a connection.
	 * <p>
	 * Every ping has an ID. A pong answer with the ID of the originally ping.
	 * So every pong can be assigned to a send ping. So it is possible to send
	 * multiple pings.
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
	 * <code>Pong</code> and the same ID. Both nodes can send Pings
	 * independently.
	 */
	public static final byte BLOCK_PING = 2;

	/**
	 * Message meta data block.
	 * <p>
	 * A block for general messages control.
	 * 
	 * <pre>
	 * Structure:
	 * 
	 * - byte: type:
	 *          0: New      - start new message
	 *          1: Unknown  - received block about a unknown message
	 *          2: Sent     - completely sent a message
	 *          3: Complete - successfully received message
	 *          4: Error    - message fail [TODO]
	 *          5: WhatsUp  - too long no request
	 *          6: Pending  - message is waiting in the queue
	 * - long: message ID
	 * - long: CRC checksum or zero (only parsed on NEW)
	 * </pre>
	 * 
	 * The sender first sends the <code>New</code> command with the related ID
	 * and a CRC checksum. Afterwards the sender sends all the blocks by
	 * {@link #BLOCK_MESSAGE_BLOCK}. If the receiver receives a
	 * {@link #BLOCK_MESSAGE_BLOCK} without knowing the message ID, is sends an
	 * <code>Unknown</code> command and the sender answers with the
	 * <code>New</code> command again exactly like above. If the sender sent all
	 * block and received all {@link #BLOCK_MESSAGE_BLOCK_FEEDBACK}s about them,
	 * it sends the <code>Send</code> command and the receiver answers with the
	 * <code>Complete</code> command.
	 * <p>
	 * If any when an deep error occurs or the transmission was canceled any
	 * how, any one writes <code>Error</code>. The other node can answer even
	 * with <code>Error</code>, it like the <code>Bye</code> command of the
	 * {@link #BLOCK_HELLO} block. If the receivers thinks the sending of
	 * message hangs up, can send the <code>WhatsUp</code> command. The sender
	 * can answer differently, either if the transmission was broken it answers
	 * <code>Error</code>, if there is still some thing to send it answers
	 * <code>Pending</code> also if it is still sending blocks. If the
	 * transmission was successful it answers <code>Send</code> and the receiver
	 * has to answer like above.
	 */
	public static final byte BLOCK_MESSAGE = 3;

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
	 */
	public static final byte BLOCK_MESSAGE_BLOCK = 4;

	/**
	 * Feedback block for a message.
	 * <p>
	 * This sends the receiver of an block back to the sender to indicate if a
	 * block was received or not, if no answer was send after a specific time
	 * the block will be send again.
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
	 * The receiver of a {@link #BLOCK_MESSAGE_BLOCK} answers with this block
	 * either by sending the <code>Acknowledge</code> command on success or the
	 * <code>Repeat</code> if something failed.
	 */
	public static final byte BLOCK_MESSAGE_BLOCK_FEEDBACK = 5;


	/**
	 * Empty constructor.
	 */
	public NodeBlock() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Encodes the block into a byte array.
	 * <p>
	 * These block are able to be sent over network. They can be parsed with
	 * {@link #decode(byte[])}.
	 * <p>
	 * Usually only one block is sent in one message, but with the multi block
	 * some can be joint to one again.
	 * <p>
	 * <b>Note:</b> This method will add a byte to the beginning of the data
	 * array which defines this block. This whole array can be parsed by
	 * {@link #decode(byte[])}
	 * 
	 * @return a byte array representing this block
	 */
	public abstract byte[] encode();

	/**
	 * Gets the size of bytes this block would takes encoded.
	 * <p>
	 * This size has to fit to the real size of an array generated by
	 * {@link #encode()}. It can depends on the content of fields.
	 * 
	 * @return the size of the block
	 */
	public abstract int getSize();

	/**
	 * Decodes the array into this block.
	 * <p>
	 * Note this data must fit the encoding of this block. If you don't know the
	 * type but it stands at the beginning use {@link #decodeBlock(byte[])}.
	 * <p>
	 * <b>Note:</b> This method can handle the leading type byte which is added
	 * to the front of a block by the {@link #encode()} method. you have to
	 * remove it or call {@link #decodeBlock(byte[])} which will handle it.
	 * 
	 * @param data the data to parse
	 * @return the block represented by the data
	 */
	public NodeBlock decode(final byte[] data) {
		return (decode(data, 0, data.length));
	}

	/**
	 * Decodes the array into this block.
	 * <p>
	 * Note this data must fit the encoding of this block. If you don't know the
	 * type but it stands at the beginning use {@link #decodeBlock(byte[])}.
	 * <p>
	 * <b>Note:</b> This method can handle the leading type byte which is added
	 * to the front of a block by the {@link #encode()} method. you have to
	 * remove it or call {@link #decodeBlock(byte[])} which will handle it.
	 * 
	 * @param data the data to parse
	 * @param offset the beginning of the data in the array
	 * @param length the length of the data in the array from the offset
	 * @return the block represented by the data
	 */
	public abstract NodeBlock decode(final byte[] data, final int offset, final int length);

	/**
	 * Returns the type byte of the block.
	 * <p>
	 * 
	 * @return the type
	 */
	public abstract byte getNativeType();

	/**
	 * Decodes a block.
	 * <p>
	 * This block has to start with the type of the block. This method will
	 * detect it and parse the right kind of block.
	 * <p>
	 * Note the array returned by {@link #encode()} can be perfectly parsed by
	 * this method.
	 * 
	 * @param data the data to parse
	 * @return the decoded block
	 */
	public static NodeBlock decodeBlock(final byte[] data) {
		return decodeBlock(data, 0, data.length);
	}

	/**
	 * Decodes a block.
	 * <p>
	 * This block has to start with the type of the block. This method will
	 * detect it and parse the right kind of block.
	 * <p>
	 * Note the array returned by {@link #encode()} can be perfectly parsed by
	 * this method.
	 * 
	 * @param data the data to parse
	 * @param offset the beginning of the data in the array
	 * @param length the length of the data in the array from the offset
	 * @return the decoded block
	 */
	public static NodeBlock decodeBlock(final byte[] data, final int offset, final int length) {
		try (ByteArrayInputStream in = new ByteArrayInputStream(data, offset, length);
				DataInputStream dis = new DataInputStream(in)) {

			switch (dis.readByte()) {
			case BLOCK_MULTIBLOCK:
				return new Multi().decode(data, 1, length - 1);

			case BLOCK_HELLO:
				return new Hello().decode(data, 1, length - 1);

			case BLOCK_PING:
				return new Ping().decode(data, 1, length - 1);

			case BLOCK_MESSAGE:
				return new Message().decode(data, 1, length - 1);

			case BLOCK_MESSAGE_BLOCK:
				return new MessageBlock().decode(data, 1, length - 1);

			case BLOCK_MESSAGE_BLOCK_FEEDBACK:
				return new MessageBlockFeedback().decode(data, 1, length - 1);

			case BLOCK_IGNORE:
				return new Ignore().decode(data, 1, length - 1);

			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}



}
