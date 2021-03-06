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

package com.github.held03.jasityProtocol.base.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.github.held03.jasityProtocol.base.util.blocks.MessageBlock;
import com.github.held03.jasityProtocol.interfaces.Message;


/**
 * @author adam
 */
public class SendingMessage extends MessageContainer implements Future<Boolean>, Comparable<SendingMessage> {

	/**
	 * Canceled flag.
	 * <p>
	 * If set, it indicates that the user requested the abort of the
	 * transmitting of the message.
	 */
	public boolean isCanceled = false;

	/**
	 * The current position of sent data.
	 * <p>
	 * It points as an array point into the data array and point to the first
	 * byte which hasn't yet been send.
	 * <p>
	 * <b>Notice:</b> It points out of the array if all bytes where sent.
	 */
	protected int currentOffset = 0;

	/**
	 * Points out if the whole data was transmitted and are confirmed by the
	 * remote.
	 * <p>
	 * This field shows if the data was transmitted, successfully, not
	 * successfully or still in progress. If the transmitting succeeded, it is
	 * <code>true</code>. If an error occurs or the transmitting was aborted, it
	 * is <code>false</code>. If the data wasn't yet completely sent or waiting
	 * for confirmation, it is <code>null</code>.
	 * <p>
	 * <b>Notice:</b> This field can be <code>null</code>.
	 */
	public Boolean finished = null;

	/**
	 * The priority of this message.
	 * <p>
	 * This is the major sorting field.
	 */
	public final Message.Priority priority;

	/**
	 * The time this message was originally created.
	 * <p>
	 * This is the minor sorting field.
	 */
	public final long creation = System.currentTimeMillis();

	/**
	 * List of sent, but not confirmed blocks.
	 * <p>
	 * Every time a block was sent it will be listed in that list, until it gets
	 * confirmed by the remote.
	 */
	protected Set<MessageBlockDef> sentBlocks = Collections
			.synchronizedSet(new HashSet<SendingMessage.MessageBlockDef>());

	/**
	 * List of all blocks which should be resent.
	 */
	protected Set<MessageBlockDef> repeatBlocks = Collections
			.synchronizedSet(new HashSet<SendingMessage.MessageBlockDef>());

	/**
	 * A abstract description of a block of data.
	 * <p>
	 * This class describes a block independently from the actual data, only by
	 * offset and length. Additional, the time of sent is saved too. This allows
	 * searching for old unconfirmed blocks for automatic resenting.
	 * 
	 * @author adam
	 */
	class MessageBlockDef {

		/**
		 * The start position of the block in the data array.
		 */
		public final int offset;
		/**
		 * The length of the block in bytes.
		 */
		public final int length;
		/**
		 * The time as the block was sent in milliseconds.
		 * <p>
		 * The time stamp is equally to those returned by
		 * {@link System#currentTimeMillis()}.
		 */
		public final long timeSent;

		/**
		 * Create a specific block.
		 * <p>
		 * The time will be set to the current time.
		 * 
		 * @param offset the start point of the block
		 * @param length the length of the block
		 */
		public MessageBlockDef(final int offset, final int length) {
			this(offset, length, System.currentTimeMillis());
		}

		/**
		 * Create a fully specified block.
		 * 
		 * @param offset the start point of the block
		 * @param length the length of the block
		 * @param timeSent the sending time of the block.
		 */
		public MessageBlockDef(final int offset, final int length, final long timeSent) {
			this.offset = offset;
			this.length = length;
			this.timeSent = timeSent;

		}

		/*
		 * (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + length;
			result = prime * result + offset;
			return result;
		}

		/*
		 * (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(final Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			MessageBlockDef other = (MessageBlockDef) obj;
			if (!getOuterType().equals(other.getOuterType())) {
				return false;
			}
			if (length != other.length) {
				return false;
			}
			if (offset != other.offset) {
				return false;
			}
			return true;
		}

		private SendingMessage getOuterType() {
			return SendingMessage.this;
		}
	}

	/**
	 * Create a message with given binary data.
	 * <p>
	 * To generate such data use a message coder. TODO: specify message coder
	 */
	public SendingMessage(final long messageID, final byte[] binaryData) {
		this(messageID, binaryData, Message.Priority.NORMAL);
	}

	/**
	 * Create a message with given binary data.
	 * <p>
	 * To generate such data use a message coder. TODO: specify message coder
	 */
	public SendingMessage(final long messageID, final byte[] binaryData, final Message.Priority priority) {
		super(messageID, binaryData);

		this.priority = priority;
	}

	/**
	 * Gets next block to send of this message.
	 * <p>
	 * If the data reaches the end, <code>null</code> will be returned.
	 * <p>
	 * This method would also return <code>null</code> if all block sent, but
	 * still not confirmed.
	 * 
	 * @param size size of block
	 * @return the block data to send, or <code>null</code> if EOF
	 */
	public synchronized MessageBlock getNextBlock(final int size, final long time) {
		int length = size;
		int offset = currentOffset;

		/*
		 * Search first for block to resent.
		 */
		MessageBlock mb = checkForMissingBlocks(size, time);
		if (mb != null)
			return mb;

		/*
		 * Then send the next block if available.
		 */
		if (currentOffset + length > binaryData.length) {
			length = binaryData.length - currentOffset;

			if (length <= 0)
				return null;

			currentOffset = binaryData.length;
		} else {
			currentOffset += length;
		}

		sentBlocks.add(new MessageBlockDef(offset, length));

		return sendBlock(offset, length);

	}

	/**
	 * Creates a block code for the given data range.
	 * <p>
	 * This method threads a block size of <code>16 + length</code>.
	 * 
	 * @param offset start position of data
	 * @param length length of data
	 * @return the coded block
	 */
	protected MessageBlock sendBlock(final int offset, final int length) {
		return new MessageBlock(messageID, binaryData, offset, length);
	}

	/**
	 * Checks if a sent block wasn't answered for <code>time</code>
	 * milliseconds.
	 * <p>
	 * If at least one block exceeded the given time, that block will be
	 * returned. If no block is out of time, <code>null</code> will be returned.
	 * 
	 * @param size the block size
	 * @param time the maximum time after a block must be answered.
	 * @return
	 */
	public synchronized MessageBlock checkForMissingBlocks(final int size, final long time) {
		for (MessageBlockDef mbd : repeatBlocks) {
			if (size < mbd.length) {
				repeatBlocks.remove(mbd);
				sentBlocks.add(new MessageBlockDef(mbd.offset, size));
				repeatBlocks.add(new MessageBlockDef(mbd.offset + size, mbd.length - size, mbd.timeSent));
				return sendBlock(mbd.offset, size);

			} else {
				sentBlocks.add(new MessageBlockDef(mbd.offset, mbd.length));
				repeatBlocks.remove(mbd);

				return sendBlock(mbd.offset, mbd.length);
			}
		}

		for (MessageBlockDef mbd : sentBlocks) {
			if (mbd.timeSent + time < System.currentTimeMillis()) {
				sentBlocks.remove(mbd);

				if (size < mbd.length) {
					sentBlocks.add(new MessageBlockDef(mbd.offset, size));
					sentBlocks.add(new MessageBlockDef(mbd.offset + size, mbd.length - size, mbd.timeSent));
					return sendBlock(mbd.offset, size);

				} else {
					sentBlocks.add(new MessageBlockDef(mbd.offset, mbd.length));

					return sendBlock(mbd.offset, mbd.length);
				}
			}
		}

		return null;
	}

	/**
	 * Called by the node if block response was received.
	 * 
	 * @param offset the block response offset
	 * @param length the block response length
	 */
	public synchronized void readBlockResponse(final int offset, final int length) {
		// removes old instance (all which are contained)
		HashSet<MessageBlockDef> rm = new HashSet<MessageBlockDef>();
		for (MessageBlockDef mbds : sentBlocks) {
			if (mbds.offset >= offset && mbds.offset + mbds.length <= offset + length) {
				rm.add(mbds);
			}
		}
		sentBlocks.removeAll(rm);

		if (sentBlocks.isEmpty() && repeatBlocks.isEmpty() && currentOffset == binaryData.length) {
			finished = true;

			this.notifyAll();
		}
	}

	/**
	 * Returns a block of data to resent it.
	 * 
	 * @param offset the start position of the data
	 * @param length the length of the data
	 * @return the block to send
	 */
	public synchronized MessageBlock resent(final int offset, final int length) {
		MessageBlockDef mbd = new MessageBlockDef(offset, length);
		// removes old instance (all which are contained)
		HashSet<MessageBlockDef> rm = new HashSet<MessageBlockDef>();
		for (MessageBlockDef mbds : sentBlocks) {
			if (mbds.offset >= offset && mbds.offset + mbds.length <= offset + length) {
				rm.add(mbds);
			}
		}
		sentBlocks.removeAll(rm);

		sentBlocks.add(mbd); // add new instance

		return sendBlock(offset, length);
	}

	public boolean wasSuccessful() {
		return (finished != null ? finished : false);
	}

	public synchronized void repeat(final int offset, final int length) {
		repeatBlocks.add(new MessageBlockDef(offset, length));
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.concurrent.Future#cancel(boolean)
	 */
	@Override
	public boolean cancel(final boolean b) {

		if (isCancelled())
			return false;

		synchronized (this) {
			isCanceled = true;
			finished = false;

			this.notifyAll();
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.concurrent.Future#get()
	 */
	@Override
	public synchronized Boolean get() throws InterruptedException, ExecutionException {

		if (isCancelled())
			return null;

		if (isDone())
			return finished;

		this.wait();

		return finished;

	}

	/*
	 * (non-Javadoc)
	 * @see java.util.concurrent.Future#get(long, java.util.concurrent.TimeUnit)
	 */
	@Override
	public Boolean get(final long arg0, final TimeUnit arg1) throws InterruptedException, ExecutionException,
			TimeoutException {
		synchronized (this) {
			if (isCancelled())
				return null;

			if (isDone())
				return finished;

			this.wait(arg1.toMillis(arg0));

			return finished;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.concurrent.Future#isCancelled()
	 */
	@Override
	public boolean isCancelled() {
		return isCanceled;
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.concurrent.Future#isDone()
	 */
	@Override
	public boolean isDone() {
		return finished != null;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(final SendingMessage o) {
		int prioSort = priority.compareTo(o.priority);

		if (prioSort == 0) {
			return (Long.compare(creation, o.creation));
		} else {
			return prioSort;
		}
	}

	public int currentOffset() {
		return currentOffset;
	}



//	@Override
//	public boolean equals(final Object o) {
//		if (o instanceof SendingMessage) {
//			SendingMessage sm = (SendingMessage) o;
//
//			return (sm.messageID == messageID);
//		}
//
//		return false;
//	}



}
