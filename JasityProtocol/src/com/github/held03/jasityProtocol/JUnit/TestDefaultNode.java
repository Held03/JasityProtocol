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

package com.github.held03.jasityProtocol.JUnit;

import static org.junit.Assert.fail;

import java.util.LinkedList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.github.held03.jasityProtocol.base.DefaultNode;
import com.github.held03.jasityProtocol.base.util.StringMessage;
import com.github.held03.jasityProtocol.base.util.blocks.NodeBlock;
import com.github.held03.jasityProtocol.interfaces.Connection;
import com.github.held03.jasityProtocol.interfaces.JPListener;
import com.github.held03.jasityProtocol.interfaces.Node;
import com.github.held03.jasityProtocol.interfaces.NodeClosedException;


/**
 * @author held03
 */
public class TestDefaultNode {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for
	 * {@link com.github.held03.jasityProtocol.base.DefaultNode#receivedBlock(byte[])}
	 * .
	 */
	@Test
	public void testReceivedBlockByteArray() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link com.github.held03.jasityProtocol.base.DefaultNode#getNextBlock()}.
	 * 
	 * @throws NodeClosedException
	 */
	@Test(timeout = 10000)
	public void testGetNextBlock() throws NodeClosedException {

		final List<Node> firstList = new LinkedList<Node>();

		Connection cFirst = new Connection() {

			@Override
			public boolean isConnected() {
				return true;
			}

			@Override
			public List<Node> getRelatedNodes() {
				return firstList;
			}

			@Override
			public float getConectionOutputLoad5() {
				return 0;
			}

			@Override
			public float getConectionOutputLoad1() {
				return 0;
			}

			@Override
			public int getBlockSize() {
				return 32;
			}

			@Override
			public void close() {

			}
		};

		DefaultNode first = new DefaultNode(null, null, cFirst);

		firstList.add(first);

		final List<Node> secondList = new LinkedList<Node>();

		Connection cSecond = new Connection() {

			@Override
			public boolean isConnected() {
				return true;
			}

			@Override
			public List<Node> getRelatedNodes() {
				return secondList;
			}

			@Override
			public float getConectionOutputLoad5() {
				return 0;
			}

			@Override
			public float getConectionOutputLoad1() {
				return 0;
			}

			@Override
			public int getBlockSize() {
				return 32;
			}

			@Override
			public void close() {

			}
		};

		DefaultNode second = new DefaultNode(null, null, cSecond);

		secondList.add(first);

		//final List<StringMessage> res = new LinkedList<StringMessage>();

		res.clear();

		Object listy = new Object() {

			@JPListener
			public Boolean readString(final StringMessage sm) {
				System.out.println("Received: '" + sm.getText() + "'");
				res.add(sm);

				return false;
			}
		};

//		WriteOutListener listy = new WriteOutListener();

		/*
		 * Start test.
		 */
		byte[] bufA = null, bufB = null;

		final List<StringMessage> start = new LinkedList<StringMessage>();

		start.add(new StringMessage("Next Stäp. ☯☿♜♬♮⚊⚂♦♖"));
		start.add(new StringMessage("Hello World."));
		start.add(new StringMessage("Next Stäp. ☯☿♜♮⚊⚂ ♦♖"));

		for (StringMessage sm : start)
			first.sendMessage(sm);

		second.addListener(listy);

		int noCount = 0;
		boolean lastA = false;
		boolean lastNote = false;

		while ( (bufA = first.getNextBlockDirectly()) != null | (bufB = second.getNextBlockDirectly()) != null
				| noCount++ < 5) {
			if (bufA != null && bufA.length != 0) {
				System.out.println("Send Block A. len=" + bufA.length + "  " + NodeBlock.decodeBlock(bufA).toString());
				second.receivedBlock(bufA);
				noCount = 0;
			}

			if (bufA != null && bufB != null) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			if (bufB != null && bufB.length != 0) {
				System.out.println("Send Block B. len=" + bufB.length + "  " + NodeBlock.decodeBlock(bufB).toString());
				first.receivedBlock(bufB);
				noCount = 0;
			}

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			bufA = null;
			bufB = null;
		}

		StringMessage sm;
		for (StringMessage find : start) {
			sm = null;
			for (StringMessage test : res) {
				if (test.equals(find)) {
					sm = test;
				}
			}

			if (sm == null) {
				fail("Did not received: " + find.getText());
			} else {
				res.remove(sm);
			}
		}

	}

	/**
	 * Test method for
	 * {@link com.github.held03.jasityProtocol.base.DefaultNode#getNextBlockDirectly()}
	 * .
	 */
	@Test
	public void testGetNextBlockDirectly() {
		fail("Not yet implemented");
	}

	List<StringMessage> res = new LinkedList<StringMessage>();

	class WriteOutListener {

		@JPListener
		public Boolean readString(final StringMessage sm) {
			System.out.println("Received: '" + sm.getText() + "'");
			res.add(sm);

			return false;
		}
	}
}
