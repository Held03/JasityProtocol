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
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.held03.jasityProtocol.base.util.MessageBlockFragment;
import com.github.held03.jasityProtocol.base.util.MessageContainer;
import com.github.held03.jasityProtocol.base.util.PingManager;
import com.github.held03.jasityProtocol.base.util.SendingMessage;
import com.github.held03.jasityProtocol.base.util.blocks.Hello;
import com.github.held03.jasityProtocol.base.util.blocks.Ignore;
import com.github.held03.jasityProtocol.base.util.blocks.MessageB;
import com.github.held03.jasityProtocol.base.util.blocks.MessageBlock;
import com.github.held03.jasityProtocol.base.util.blocks.MessageBlockFeedback;
import com.github.held03.jasityProtocol.base.util.blocks.Multi;
import com.github.held03.jasityProtocol.base.util.blocks.NodeBlock;
import com.github.held03.jasityProtocol.base.util.blocks.Ping;
import com.github.held03.jasityProtocol.interfaces.Address;
import com.github.held03.jasityProtocol.interfaces.Connection;
import com.github.held03.jasityProtocol.interfaces.JPListener;
import com.github.held03.jasityProtocol.interfaces.Message;
import com.github.held03.jasityProtocol.interfaces.Message.Priority;
import com.github.held03.jasityProtocol.interfaces.MessageCoder;
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
	 * The oldest version which the current one can handle due compatibility.
	 * <p>
	 * The version codes depends only on this implementation. Other nodes may
	 * have different version codes.
	 * <p>
	 * This value is always smaller or equals to {@link #CURRENT_VERSION}. All
	 * version between <code>MIN_VERSION</code> and <code>CURRENT_VERSION</code>
	 * has to be handled by this implementation.
	 */
	public static final long MIN_VERSION = 0;

	/**
	 * The version of the used implementation.
	 * <p>
	 * The version codes depends only on this implementation. Other nodes may
	 * have different version codes.
	 */
	public static final long CURRENT_VERSION = 0;

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

	/**
	 * Message id counter.
	 * <p>
	 * <b>IMPORTANT:</b> NEVER ACCESS THIS FIELD. Instant uses
	 * {@link #getNextId()} to get an ID.
	 */
	private long messageIdCouter = 0;

	/**
	 * Get a new unique message id for this node.
	 * <p>
	 * This id is only for messages send by this node.
	 * 
	 * @return new unique id
	 */
	public synchronized long getNextId() {
		return messageIdCouter++;
	}

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
	 * The coder this node uses to encode and decode messages.
	 * <p>
	 * <b>Note:</b> It is essential that the local and the remote node has the
	 * same coder or tow compatible ones.
	 */
	protected MessageCoder coder = new SerializerCoder(); // TODO use factory

	/**
	 * Connected flag.
	 * <p>
	 * It is <code>true</code> if the the connection to the remote node was
	 * successfully established.
	 */
	protected boolean isOnline;

	/**
	 * The version of the remote
	 */
	protected long remoteVersionCode = -1;

	/**
	 * The ping manager to manage pings.
	 */
	protected PingManager pingManager = new PingManager();

	/**
	 * System node blocks to send.
	 */
	protected LinkedList<NodeBlock> blocks = new LinkedList<NodeBlock>();

	/**
	 * Set of received blocks without knowing the message of it.
	 */
	protected HashSet<MessageBlockFragment> fragments = new HashSet<MessageBlockFragment>();

	/**
	 * Queue of messages waiting to send.
	 */
	protected PriorityQueue<SendingMessage> sendingQueue = new PriorityQueue<SendingMessage>();

	/**
	 * List of currently receiving messages.
	 */
	protected HashMap<Long, MessageContainer> receivingList = new HashMap<Long, MessageContainer>();

	/**
	 * The current state of the Node.
	 */
	protected State currentState = State.OPENING;

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
		NodeBlock nb = NodeBlock.decodeBlock(block);

		receivedBlock(nb);
	}

	/**
	 * Process given node block.
	 * 
	 * @param nb the block to process
	 */
	protected void receivedBlock(final NodeBlock nb) {
		switch (nb.getNativeType()) {
		case NodeBlock.BLOCK_MULTIBLOCK:
			Multi multi = (Multi) nb;

			/*
			 * Call receive recursively for every block
			 */
			for (NodeBlock sub : multi.getSubBlocks()) {
				receivedBlock(sub);
			}

			break;

		case NodeBlock.BLOCK_IGNORE:
			Ignore ign = (Ignore) nb;

			/*
			 * Easily ignore it ;)
			 */

			break;

		case NodeBlock.BLOCK_HELLO:
			Hello hello = (Hello) nb;

			switch (hello.getType()) {
			case Hello.TYPE_KNOCK:
				/*
				 * Answer with akc.
				 */
				sendBlock(new Hello(Hello.TYPE_HELLO, CURRENT_VERSION));

				break;

			case Hello.TYPE_HELLO:
				/*
				 * Sets node to available
				 */
				if (currentState.equals(State.OPENING)) {
					currentState = State.CONNECTED;
				}

				break;

			case Hello.TYPE_BUSY:
				/*
				 * Remote refused.
				 * Close node.
				 */
			case Hello.TYPE_BYE:
				/*
				 * Remote closed node.
				 * Close node.
				 */
				close();

				break;
			}

			break;

		case NodeBlock.BLOCK_PING:
			Ping ping = (Ping) nb;

			switch (ping.getType()) {
			case Ping.TYPE_PING:
				/*
				 * Answer the ping directly.
				 * The ping has a priority.
				 */
				sendBlock(new Ping(Ping.TYPE_PONG, ping.getId()), true);

				break;

			case Ping.TYPE_PONG:
				/*
				 * Forward it to the ping manager.
				 */
				pingManager.addPong(ping.getId());

			}

			break;

		case NodeBlock.BLOCK_MESSAGE:
			MessageB msg = (MessageB) nb;

			switch (msg.getType()) {
			/*
			 * Handle receiving operations.
			 */
			case MessageB.TYPE_NEW:
				if (receivingList.containsKey(msg.getId())) {
					/*
					 * Ignore if already known.
					 */
				} else {
					/*
					 * Creating new container to store the new message.
					 */
					MessageContainer mc = new MessageContainer(msg.getId(), new byte[msg.getMsgSize()]);

					receivingList.put(msg.getId(), mc);

					/*
					 * Search for already received blocks.
					 */
					HashSet<MessageBlockFragment> rm = new HashSet<MessageBlockFragment>();

					for (MessageBlockFragment mbf : fragments) {
						if (mbf.id == msg.getId()) {
							/*
							 * Mark for remove.
							 */
							rm.add(mbf);

							/*
							 * Add insert data.
							 */
							mc.putData(mbf.data, mbf.offset);

							/*
							 * Send acknowledge.
							 */
							sendBlock(new MessageBlockFeedback(MessageBlockFeedback.TYPE_ACKNOWLEDGE, mbf.id,
									mbf.offset, mbf.data.length));
						}
					}
				}


				break;

			case MessageB.TYPE_SENT:

				/*
				 * Remote verifies that a message have been sent.
				 * So let it deliver.
				 */

				if (receivingList.containsKey(msg.getId())) {
					/*
					 * Pull message from map and decode it.
					 */
					MessageContainer mc = receivingList.remove(msg.getId());

					Message m = coder.decodeMessage(ByteBuffer.wrap(mc.getData()));

					/*
					 * Send COMPLETE back.
					 */
					sendBlock(new MessageB(MessageB.TYPE_COMPLETE, msg.getId()));

					/*
					 * Deliver message to listeners.
					 * TODO: This should be done by an special thread.
					 */
					deliverMessage(m);

				} else {
					/*
					 * Ignore if no more present.
					 * But answer with COMPLETE.
					 */

					sendBlock(new MessageB(MessageB.TYPE_COMPLETE, msg.getId()));
				}

				break;

			case MessageB.TYPE_ERROR_SEND:

				if (receivingList.containsKey(msg.getId())) {
					/*
					 * Remove due cancellation.
					 */
					receivingList.remove(msg.getId());

				} else {
					/*
					 * If already removed, ignore it.
					 */
				}

				break;

			case MessageB.TYPE_PENDING:

				if (receivingList.containsKey(msg.getId())) {
					/*
					 * If exist the related message, update the time stamp.
					 */
					receivingList.get(msg.getId()).setUpdate();
				} else {
					/*
					 * Ignore it other wise.
					 */
				}

				break;
			/*
			 * Handle sending operations.
			 */
			case MessageB.TYPE_UNKNOWN:

				SendingMessage sm = getSendingById(msg.getId());

				if (sm != null) {
					/*
					 * Send the remote the NEW message.
					 */

					sendBlock(new MessageB(sm.getId(), sm.getDataLength()));
				} else {
					/*
					 * If there is no more message.
					 * Send ERROR back.
					 */
					sendBlock(new MessageB(MessageB.TYPE_ERROR_SEND, msg.getId()));
				}

				break;

			case MessageB.TYPE_COMPLETE:

				/*
				 * Message was successfully transmitted.
				 * Remove it if it still exist.
				 */

				sm = getSendingById(msg.getId());

				if (sm != null) {
					sendingQueue.remove(sm);
				}

				break;

			case MessageB.TYPE_WHATS_UP:

				sm = getSendingById(msg.getId());

				if (sm != null) {
					/*
					 * Send the remote the PENDING message.
					 */

					if (!sm.isDone()) {
						sendBlock(new MessageB(MessageB.TYPE_PENDING, sm.getId()));
					} else {
						if (sm.wasSuccessful()) {
							sendBlock(new MessageB(MessageB.TYPE_SENT, sm.getId()));
						} else {
							sendBlock(new MessageB(MessageB.TYPE_ERROR_SEND, sm.getId()));
						}
					}

				} else {
					/*
					 * If there is no more message.
					 * Send ERROR back.
					 */

					sendBlock(new MessageB(MessageB.TYPE_ERROR_SEND, msg.getId()));
				}

				break;

			case MessageB.TYPE_ERROR_RECIEVE:

				/*
				 * Message transmitting failed.
				 * Remove it if it still exist.
				 */

				sm = getSendingById(msg.getId());

				if (sm != null) {
					sendingQueue.remove(sm);
				}

				break;
			}

			break;

		case NodeBlock.BLOCK_MESSAGE_BLOCK:
			MessageBlock mBlock = (MessageBlock) nb;

			if (receivingList.containsKey(mBlock.getId())) {

				/*
				 * Add block to message if message exist.
				 */

				MessageContainer mc = receivingList.get(mBlock.getId());

				mc.putData(mBlock.getData(), mBlock.getOffset());

				/*
				 * Send feedback.
				 */

				sendBlock(new MessageBlockFeedback(MessageBlockFeedback.TYPE_ACKNOWLEDGE, mBlock.getId(),
						mBlock.getOffset(), mBlock.getData().length));
			} else {

				/*
				 * Add to fragment buffer.
				 */
				fragments.add(new MessageBlockFragment(mBlock.getId(), mBlock.getData(), mBlock.getOffset()));

				/*
				 * Send unknown message.
				 */
				sendBlock(new MessageB(MessageB.TYPE_UNKNOWN, mBlock.getId()));
			}

			break;

		case NodeBlock.BLOCK_MESSAGE_BLOCK_FEEDBACK:
			MessageBlockFeedback mbf = (MessageBlockFeedback) nb;

			switch (mbf.getType()) {
			case MessageBlockFeedback.TYPE_ACKNOWLEDGE:

				/*
				 * Save acknowledge.
				 */
				SendingMessage sm = getSendingById(mbf.getId());

				if (sm != null)
					sm.readBlockResponse(mbf.getOffset(), mbf.getLength());

				break;

			case MessageBlockFeedback.TYPE_REPEAT:

				/*
				 * Resent block if available.
				 */

				sm = getSendingById(mbf.getId());

				if (sm != null)
					;
				//TODO: Repeat the block.

				break;
			}

			break;
		}
	}

	protected SendingMessage getSendingById(final long msgId) {
		for (SendingMessage sm : sendingQueue) {
			return sm;
		}

		return null;
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

	/**
	 * Queue a node block for sending.
	 * 
	 * @param nb the node block to send
	 */
	protected void sendBlock(final NodeBlock nb) {
		sendBlock(nb, false);
	}

	/**
	 * Queue a node block for sending.
	 * 
	 * @param nb the node block to send
	 * @param first if <code>true</code>, add the block at head of the queue
	 */
	protected void sendBlock(final NodeBlock nb, final boolean first) {
		if (first) {
			blocks.addFirst(nb);
		} else {
			blocks.add(nb);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.github.held03.jasityProtocol.interfaces.Node#sendMessage(com.github
	 * .held03.jasityProtocol.interfaces.Message)
	 */
	@Override
	public Future<Boolean> sendMessage(final Message msg) {
		return sendMessage(msg, Priority.NORMAL);
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
		SendingMessage sm = new SendingMessage(getNextId(), coder.encodeMessage(msg).array(), priority);
		sendingQueue.add(sm);
		return sm;
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
		return pingManager.getAverageTime();
	}

	/*
	 * (non-Javadoc)
	 * @see com.github.held03.jasityProtocol.interfaces.Node#getState()
	 */
	@Override
	public State getState() {
		return currentState;
	}

}
