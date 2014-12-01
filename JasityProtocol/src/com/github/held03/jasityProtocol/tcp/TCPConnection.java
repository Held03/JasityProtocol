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

package com.github.held03.jasityProtocol.tcp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import com.github.held03.jasityProtocol.base.AbstractConnection;
import com.github.held03.jasityProtocol.base.DefaultNode;
import com.github.held03.jasityProtocol.interfaces.Address;
import com.github.held03.jasityProtocol.interfaces.Node;
import com.github.held03.jasityProtocol.interfaces.NodeClosedException;


/**
 * TCP implementation for the JasityProtocol.
 * 
 * @author held03
 */
public class TCPConnection extends AbstractConnection {

	/**
	 * The standard block size for the node blocks.
	 */
	protected static final int STANDART_BLOCK_SIZE = 0xFFFF; // 64kB 

	/**
	 * The maximum block size for the node blocks.
	 */
	protected static final int MAX_BLOCK_SIZE = 0xFFFFF; // 1MB 

	/**
	 * The back end socket over which the communication will be done.
	 */
	protected Socket socket;

	/**
	 * The reader thread which handles the reading.
	 */
	private Thread reader;

	/**
	 * The writer thread which handles the writing.
	 */
	private Thread writer;

	/**
	 * @param localAddress
	 */
	protected TCPConnection(final Address local) {
		super(local, true);
	}

	/**
	 * Creates a node connected to the given address.
	 * 
	 * @param connectTo the address to connect to
	 * @return the node representing this connection
	 * @throws IOException
	 */
	public static Node newConnection(final TCPAddress connectTo) throws IOException {
		@SuppressWarnings("resource")
		Socket s = new Socket(connectTo.inetAddress, connectTo.port);

		Address local = new TCPAddress(s.getLocalAddress(), s.getLocalPort());

		TCPConnection con = new TCPConnection(local);

		Node n = new DefaultNode(connectTo, con);

		con.addNode(n);

		con.socket = s;

		con.reader = new Thread(con.new Reader());
		con.reader.setDaemon(false);
		con.reader.setName("Read from " + connectTo);
		con.reader.start();

		con.writer = new Thread(con.new Writer());
		con.writer.setDaemon(true);
		con.writer.setName("Write to " + connectTo);
		con.writer.start();

		return n;
	}

	static Node newConnection(final Socket connection) throws IOException {
		Address local = new TCPAddress(connection.getLocalAddress(), connection.getLocalPort());
		Address remote = new TCPAddress(connection.getInetAddress(), connection.getPort());

		TCPConnection con = new TCPConnection(local);

		Node n = new DefaultNode(remote, con);

		System.out.println("creat node to: " + n.getRemoteAddress());

		con.addNode(n);

		con.socket = connection;

		con.reader = new Thread(con.new Reader());
		con.reader.setDaemon(false);
		con.reader.setName("Read from " + remote);
		con.reader.start();

		con.writer = new Thread(con.new Writer());
		con.writer.setDaemon(true);
		con.writer.setName("Write to " + remote);
		con.writer.start();

		return n;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.github.held03.jasityProtocol.interfaces.Connection#getBlockSize()
	 */
	@Override
	public int getBlockSize() {
		return STANDART_BLOCK_SIZE;
	}

	@Override
	public void close() {
		if (reader != null)
			reader.interrupt();

		if (reader != null)
			writer.interrupt();

		try {
			if (socket != null) {
				socket.close();
			}
		} catch (IOException e) {
			/*
			 * Ignore exception.
			 */
		}

		socket = null;

		super.close();

	}

	class Reader implements Runnable {

		/*
		 * (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			try {
				DataInputStream in = new DataInputStream(TCPConnection.this.socket.getInputStream());

				Address from = new TCPAddress(socket.getInetAddress(), socket.getPort());

				//System.out.println("[" + Thread.currentThread().getName() + "] from: " + from);

				int len;
				byte[] buf = new byte[MAX_BLOCK_SIZE];

				while (in.readBoolean() && !Thread.currentThread().isInterrupted()) {
					len = in.readInt();

					in.readFully(buf, 0, len);

					deliverBlock(buf, from);

				}

			} catch (IOException e) {
				e.printStackTrace();
			}

			close();
		}

	}

	class Writer implements Runnable {

		/*
		 * (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			try {
				DataOutputStream out = new DataOutputStream(TCPConnection.this.socket.getOutputStream());

				Address to = new TCPAddress(socket.getInetAddress(), socket.getPort());

				byte[] buf;

				//System.out.println("[" + Thread.currentThread().getName() + "] to: " + to);

				while ( (buf = getNextBlock(to)) != null && !Thread.currentThread().isInterrupted()) {
					out.writeBoolean(true);

					out.writeInt(buf.length);

					out.write(buf);

					out.flush();
				}

			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				//e.printStackTrace();
			} catch (NodeClosedException e) {
				//e.printStackTrace();
			}

			close();
		}

	}
}
