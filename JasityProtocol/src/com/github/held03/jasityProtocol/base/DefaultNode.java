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

package com.github.held03.jasityProtocol.base;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.held03.jasityProtocol.interfaces.Address;
import com.github.held03.jasityProtocol.interfaces.Connection;
import com.github.held03.jasityProtocol.interfaces.JPListener;
import com.github.held03.jasityProtocol.interfaces.Message;
import com.github.held03.jasityProtocol.interfaces.Message.Priority;
import com.github.held03.jasityProtocol.interfaces.Node;


/**
 * Implements a default node.
 * <p>
 * This should be able to use as a general node, so that a back end do not need
 * to implement it.
 * 
 * @author held03
 */
public class DefaultNode implements Node {

	/**
	 * The set of all registered connection listeners.
	 * <p>
	 * This field should be synchronized if accessed. Like:
	 * 
	 * <pre>
	 * synchronized (messageListeners) {
	 * 	// access or edit list ...
	 * }
	 * </pre>
	 */
	protected HashSet<ListenerContainer> messageListeners = new HashSet<>();

	private long messageIdCouter = 0;

	public synchronized long getNextId() {
		return messageIdCouter++;
	}

	/**
	 * A block containing other blocks.
	 * 
	 * <pre>
	 * Structure:
	 * 
	 * - int: count of sub blocks
	 *  { for every block
	 *   - block data
	 *  }
	 * </pre>
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
	 */
	public static final byte BLOCK_IGNORE = 0;

	/**
	 * Handshake block.
	 * <p>
	 * Block about establishing and breaking connections. This is comparable
	 * with the handshake in TCP.
	 * 
	 * <pre>
	 * Structure:
	 * 
	 * - byte: type:
	 *          0: Knock - request connection
	 *          1: Hello - connection accepted
	 *          2: Busy  - connection refused
	 *          3: Bye   - connection closed
	 * </pre>
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
	 *          2: Send     - completely send a message
	 *          3: Complete - successfully received message
	 *          4: Error    - message fail [TODO]
	 * - long: message ID
	 * - long: CRC checksum or zero (only parsed on NEW)
	 * </pre>
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
	 * -byte: answer:
	 *         0: Acknowledge - get successfully block
	 *         1: Repeat      - resent block
	 * </pre>
	 */
	public static final byte BLOCK_MESSAGE_BLOCK_FEEDBACK = 5;

	/**
	 * The address of the remote node.
	 */
	protected final Address remoteAddress;

	/**
	 * The address of the local node.
	 */
	protected final Address localAddress;

	/**
	 * The connection to the Internet.
	 * <p>
	 * This is the access point, which the node uses to communicate with its
	 * remote node.
	 */
	protected final Connection connection;

	/**
	 * Connected flag.
	 * <p>
	 * It is <code>true</code> if the the connection to the remote node was
	 * successfully established.
	 */
	protected boolean isOnline;

	/**
	 * Create a new Node.
	 * 
	 * @param address the address the node is connected to
	 */
	public DefaultNode(final Address local, final Address remote, final Connection connection) {
		this.localAddress = local;
		this.remoteAddress = remote;
		this.connection = connection;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.github.held03.jasityProtocol.interfaces.Node#receivedBlock(byte[])
	 */
	@Override
	public void receivedBlock(final byte[] block) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * @see com.github.held03.jasityProtocol.interfaces.Node#getNextBlock()
	 */
	@Override
	public byte[] getNextBlock() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.github.held03.jasityProtocol.interfaces.Node#getNextBlockDirectly()
	 */
	@Override
	public byte[] getNextBlockDirectly() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.github.held03.jasityProtocol.interfaces.Node#sendMessage(com.github
	 * .held03.jasityProtocol.interfaces.Message)
	 */
	@Override
	public Future<Boolean> sendMessage(final Message msg) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.github.held03.jasityProtocol.interfaces.Node#sendMessage(com.github
	 * .held03.jasityProtocol.interfaces.Message,
	 * com.github.held03.jasityProtocol.interfaces.Message.Priority)
	 */
	@Override
	public Future<Boolean> sendMessage(final Message msg, final Priority priority) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.github.held03.jasityProtocol.interfaces.Node#close()
	 */
	@Override
	public void close() {
		connection.close();
	}

	/*
	 * (non-Javadoc)
	 * @see com.github.held03.jasityProtocol.interfaces.Node#isConnected()
	 */
	@Override
	public boolean isConnected() {
		return connection.isConnected() && isOnline;
	}

	/*
	 * (non-Javadoc)
	 * @see com.github.held03.jasityProtocol.interfaces.Node#getLocalAddress()
	 */
	@Override
	public Address getLocalAddress() {
		return localAddress;
	}

	/*
	 * (non-Javadoc)
	 * @see com.github.held03.jasityProtocol.interfaces.Node#getRemoteAddress()
	 */
	@Override
	public Address getRemoteAddress() {
		return remoteAddress;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.github.held03.jasityProtocol.interfaces.Node#addListener(java.lang
	 * .Object)
	 */
	@Override
	public void addListener(final Object listener) {
		// list for adding the new listeners
		Set<ListenerContainer> msgToAdd = ListenerContainer.getListeners(listener);

		// checks if at least one valid listener was found 
		if (msgToAdd.isEmpty()) {
			Logger.getLogger(AbstractConnection.class.getName()).log(Level.WARNING,
					"A listener was added but it could NOT find a valid listener method! For object {1} into {2}",
					new Object[] { listener, this });

			return;
		}

		// synchronize and add the listeners
		synchronized (messageListeners) {
			messageListeners.addAll(msgToAdd);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.github.held03.jasityProtocol.interfaces.Node#removeListener(java.
	 * lang.Object)
	 */
	@Override
	public void removeListener(final Object listener) {
		synchronized (messageListeners) {
			// creates a list for removing the proper entries later
			HashSet<ListenerContainer> msgToRm = new HashSet<>();

			// search for all listeners related to the given object
			for (ListenerContainer container : messageListeners) {

				if (container.object.equals(listener)) {
					// if one found add it to the list
					msgToRm.add(container);
				}
			}

			// finally removes all selected listeners
			messageListeners.removeAll(msgToRm);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.github.held03.jasityProtocol.interfaces.Node#getListeners()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Set<ListenerContainer> getListeners() {
		synchronized (messageListeners) {
			return (Set<ListenerContainer>) messageListeners.clone();
		}
	}

	/**
	 * Delivers a message to the specific listeners.
	 * <p>
	 * First this method will check the message with the manager
	 * {@link ConnectionManager#checkMessage(Message, Connection)}. If it
	 * returns <code>true</code> it will be delivered to all fitting and
	 * registered listeners. Until one listener returned <code>true</code> or
	 * the end of the list is reached.
	 * <p>
	 * If a listener of a specific level returns <code>true</code> all other
	 * listener of the same level gets the message still before this method
	 * returns. At least all listeners of lower levels don't get the message.
	 * 
	 * @param msg the message to send
	 * @param node the node from which it was received
	 */
	protected void deliverMessage(final Message msg) {

		// map listeners to their appropriate level
		HashMap<JPListener.Level, HashSet<ListenerContainer>> msgToSend = new HashMap<>();

		// adds a list/set for every level
		for (JPListener.Level level : JPListener.Level.values()) {
			msgToSend.put(level, new HashSet<ListenerContainer>());
		}

		// gets the listener from connection to send to, and map them by level
		synchronized (messageListeners) {
			for (ListenerContainer container : messageListeners) {

				// check if the listener accepts the give message type
				if (container.message.isAssignableFrom(msg.getClass())) {
					msgToSend.get(container.priority).add(container);
				}
			}
		}

		// gets the listener from the node to send to, and map them by level
		for (ListenerContainer container : getListeners()) {

			// check if the listener accepts the give message type
			if (container.message.isAssignableFrom(msg.getClass())) {
				msgToSend.get(container.priority).add(container);
			}
		}

		// indicates if a listener returned true
		boolean consumed = false;

		// sends the message
		for (JPListener.Level level : JPListener.Level.values()) {
			for (ListenerContainer container : msgToSend.get(level)) {
				try {
					// check the argument configuration 
					if (container.callback.getParameterTypes().length == 1) {
						// call the listener with one argument
						// and check the return value
						if (container.callback.invoke(container.object, msg).equals(Boolean.TRUE)) {
							consumed = true;
						}
					} else if (container.callback.getParameterTypes().length == 2) {
						// call the listener with the additional nodeConnection arg
						// and check the return value
						if (container.callback.invoke(container.object, msg, this).equals(Boolean.TRUE)) {
							consumed = true;
						}
					} else {
						// illegal argument configurations
					}

				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					e.printStackTrace();
					Logger.getLogger(AbstractConnection.class.getName()).log(Level.WARNING,
							"Listener invokation fails! object {1}; method {2}",
							new Object[] { container.object, container.callback.getName() });
					Logger.getLogger(AbstractConnection.class.getName()).log(Level.WARNING, "", e);
				}
			}

			// if one listener returns true, don't call lower listeners 
			if (consumed)
				break;
		}

	}

	/*
	 * (non-Javadoc)
	 * @see com.github.held03.jasityProtocol.interfaces.Node#getPingTime()
	 */
	@Override
	public float getPingTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * @see com.github.held03.jasityProtocol.interfaces.Node#getState()
	 */
	@Override
	public State getState() {
		// TODO Auto-generated method stub
		return null;
	}

}
