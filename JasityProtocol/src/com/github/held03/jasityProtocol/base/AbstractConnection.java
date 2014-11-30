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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.github.held03.jasityProtocol.interfaces.Address;
import com.github.held03.jasityProtocol.interfaces.Connection;
import com.github.held03.jasityProtocol.interfaces.Node;
import com.github.held03.jasityProtocol.interfaces.NodeClosedException;


/**
 * A general connection implementation.
 * <p>
 * This class implements some general methods.
 * <p>
 * 
 * @author held03
 */
public abstract class AbstractConnection implements Connection {

	/**
	 * The nodes which receives from this connection.
	 * <p>
	 * Usually this nodes have to send as well over this connection, but that is
	 * not necessary.
	 */
	protected HashMap<Address, Node> nodes = new HashMap<Address, Node>();

	/**
	 * List of all remote addresses of the nodes.
	 */
	protected ArrayList<Address> addresses = new ArrayList<Address>();

	/**
	 * Indicates if the user requested closing
	 */
	protected boolean isClosed = false;

	/**
	 * If this flag is set, the connection will be closed if the last node
	 * closes.
	 * <p>
	 * This may be useful for single node connections.
	 */
	protected boolean closeIfEmpty;

	/**
	 * The local address which this connection uses to communicate.
	 */
	protected final Address localAddress;

	protected int lastSenderIndex = 0;

	/**
	 * Normal constructor.
	 */
	public AbstractConnection(final Address localAddress) {
		this(localAddress, false);

	}

	/**
	 * Normal constructor.
	 */
	public AbstractConnection(final Address localAddress, final boolean closeIfEmpty) {
		this.localAddress = localAddress;
		this.closeIfEmpty = closeIfEmpty;

	}

	/**
	 * Normal constructor.
	 */
	public AbstractConnection(final Address localAddress, final Node n) {
		this(localAddress, n, true);

	}

	/**
	 * Normal constructor.
	 */
	public AbstractConnection(final Address localAddress, final Node n, final boolean closeIfEmpty) {
		this.localAddress = localAddress;
		this.closeIfEmpty = closeIfEmpty;

		addNode(n);

	}

	/*
	 * (non-Javadoc)
	 * @see com.github.held03.jasityProtocol.interfaces.Connection#close()
	 */
	@Override
	public void close() {
		isClosed = true;

		synchronized (nodes) {
			for (Node n : nodes.values()) {
				n.close();
			}

			nodes.clear();
			addresses.clear();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.github.held03.jasityProtocol.interfaces.Connection#isConnected()
	 */
	@Override
	public boolean isConnected() {
		return !isClosed;
	}

	/*
	 * (non-Javadoc)
	 * @see com.github.held03.jasityProtocol.interfaces.Connection#
	 * getConectionOutputLoad1()
	 */
	@Override
	public float getConectionOutputLoad1() {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * @see com.github.held03.jasityProtocol.interfaces.Connection#
	 * getConectionOutputLoad5()
	 */
	@Override
	public float getConectionOutputLoad5() {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.github.held03.jasityProtocol.interfaces.Connection#getRelatedNodes()
	 */
	@Override
	public Set<Node> getRelatedNodes() {
		synchronized (nodes) {
			return new HashSet<Node>(nodes.values());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.github.held03.jasityProtocol.interfaces.Connection#getLocalAddress()
	 */
	@Override
	public Address getLocalAddress() {
		return localAddress;
	}

	/**
	 * Adds a node to the internal list.
	 * 
	 * @param n the node to add
	 * @return <code>true</code> if the node could be added, <code>false</code>
	 *         if already present
	 */
	public boolean addNode(final Node n) {
		synchronized (nodes) {

			if (nodes.containsKey(n.getRemoteAddress())) {
				return false;
			} else {
				nodes.put(n.getRemoteAddress(), n);
				addresses.add(n.getRemoteAddress());
				return true;
			}
		}
	}

	/**
	 * Removes a node from the internal list.
	 * 
	 * @param n the node to remove
	 * @return <code>true</code> if the node was removed, <code>false</code> if
	 *         not found
	 */
	public boolean rmNode(final Node n) {
		synchronized (nodes) {

			if (nodes.containsKey(n.getRemoteAddress())) {
				nodes.remove(n.getRemoteAddress());
				addresses.remove(n.getRemoteAddress());

				if (nodes.size() == 0 && closeIfEmpty) {
					close();
				}

				return true;
			} else {
				return false;
			}
		}
	}

	/**
	 * Forwards a data block to the specific node.
	 * <p>
	 * If there is no node to deliver this block i.g. because to the remote is
	 * no node listening, <code>false</code> is returned. If the block was
	 * delivered to the node <code>true</code> is returned.
	 * 
	 * @param data the binary data to deliver, it must contain an block for the
	 *            nodes
	 * @param from the address from which the block was received
	 * @return <code>true</code> if successfully delivered, otherwise
	 *         <code>false</code>
	 */
	public boolean deliverBlock(final byte[] data, final Address from) {
		synchronized (nodes) {
			if (nodes.containsKey(from)) {
				Node n = nodes.get(from);

				n.receivedBlock(data);

				return true;
			} else {
				return false;
			}
		}
	}

	/**
	 * Gets the next block of data from any node.
	 * <p>
	 * If currently no new block available any how, <code>null</code> will be
	 * returned.
	 * <p>
	 * The block contains the binary data and the address to send to.
	 * 
	 * @return the next block to send
	 */
	public Block getNextBlockDirectly() {
		if (nodes.size() == 0 || isClosed)
			return null;

		Block block;

		synchronized (nodes) {
			if (lastSenderIndex >= addresses.size())
				lastSenderIndex = 0;

			byte[] data;
			block = null;

			/*
			 * Post loop (after the last index)
			 */
			for (int i = lastSenderIndex + 1; i < addresses.size(); i++) {
				Node currentNode = nodes.get(addresses.get(i));

				try {
					data = currentNode.getNextBlockDirectly(getBlockSize());

					if (data != null) {
						block = new Block(data, currentNode.getRemoteAddress());
						lastSenderIndex = i;
						break;
					}
				} catch (NodeClosedException e) {
					rmNode(currentNode);
				}
			}

			/*
			 * Pre loop (before the last index and the last index)
			 */
			if (block == null) {
				for (int i = 0; i <= lastSenderIndex && i < addresses.size(); i++) {
					Node currentNode = nodes.get(addresses.get(i));

					try {
						data = currentNode.getNextBlockDirectly(getBlockSize());

						if (data != null) {
							block = new Block(data, currentNode.getRemoteAddress());
							lastSenderIndex = i;
							break;
						}
					} catch (NodeClosedException e) {
						rmNode(currentNode);
					}
				}
			}

		}

		return block;
	}

	/**
	 * A block of data to send to a target.
	 * 
	 * @author held03
	 */
	protected class Block {

		public final byte[] data;
		public final Address target;

		public Block(final byte[] data, final Address target) {
			this.data = data;
			this.target = target;

		}
	}

}
