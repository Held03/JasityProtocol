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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.nio.ByteBuffer;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.github.held03.jasityProtocol.base.util.blocks.Hello;
import com.github.held03.jasityProtocol.base.util.blocks.Ignore;
import com.github.held03.jasityProtocol.base.util.blocks.MessageB;
import com.github.held03.jasityProtocol.base.util.blocks.MessageBlock;
import com.github.held03.jasityProtocol.base.util.blocks.MessageBlockFeedback;
import com.github.held03.jasityProtocol.base.util.blocks.Multi;
import com.github.held03.jasityProtocol.base.util.blocks.NodeBlock;
import com.github.held03.jasityProtocol.base.util.blocks.Ping;


/**
 * @author held03
 */
public class TestNodeBlocks {

	Hello[] hellos;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		hellos = new Hello[6];
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for de-/encode the Hello class.
	 * 
	 * @throws InterruptedException
	 */
	@Test
	public void testHello() throws InterruptedException {
		Hello hello;
		NodeBlock nb;
		Hello res;
		ByteBuffer bb;
		int size;

		Random ran = new Random();

		for (int i = 0; i < 10; i++) {
			hello = new Hello((byte) ran.nextInt(), ran.nextLong());

			size = hello.getSize();

			bb = hello.encode();

			assertEquals("The encoded type has a different size than predicted.", size, bb.remaining());

			nb = NodeBlock.decodeBlock(bb);

			assertNotNull("Decoding failed.", nb);

			assertEquals("Wrong native type.", NodeBlock.BLOCK_HELLO, nb.getNativeType());

			res = (Hello) nb;

			assertEquals("The type wasn't right transmitted.", hello.getType(), res.getType());

			assertEquals("The version wasn't right transmitted.", hello.getVersion(), res.getVersion());

		}
	}

	/**
	 * Test method for de-/encode the Ignore class.
	 * 
	 * @throws InterruptedException
	 */
	@Test
	public void testIgnore() throws InterruptedException {
		Ignore ign;
		NodeBlock nb;
		Ignore res;
		ByteBuffer bb;
		int size;

		Random ran = new Random();

		for (int i = 0; i < 10; i++) {
			if (ran.nextBoolean()) {
				ign = new Ignore(ran.nextInt(7200));
			} else {
				byte[] buf = new byte[ran.nextInt(7200)];
				ran.nextBytes(buf);
				ign = new Ignore(buf);
			}

			size = ign.getSize();

			bb = ign.encode();

			assertEquals("The encoded type has a different size than predicted.", size, bb.remaining());

			nb = NodeBlock.decodeBlock(bb);

			assertNotNull("Decoding failed.", nb);

			assertEquals("Wrong native type.", NodeBlock.BLOCK_IGNORE, nb.getNativeType());

			res = (Ignore) nb;

			assertEquals("The data wasn't right transmitted.", ign.getData().length, res.getData().length);

			assertArrayEquals("The data wasn't right transmitted.", ign.getData(), res.getData());

		}
	}

	/**
	 * Test method for de-/encode the MessageB class.
	 * 
	 * @throws InterruptedException
	 */
	@Test
	public void testMessageB() throws InterruptedException {
		MessageB msgCtrl;
		NodeBlock nb;
		MessageB res;
		ByteBuffer bb;
		int size;

		Random ran = new Random();

		for (int i = 0; i < 15; i++) {
			if (ran.nextBoolean()) {
				msgCtrl = new MessageB((byte) ran.nextInt(), ran.nextLong());
			} else {
				msgCtrl = new MessageB(ran.nextLong(), ran.nextInt());
			}

			size = msgCtrl.getSize();

			bb = msgCtrl.encode();

			assertEquals("The encoded type has a different size than predicted.", size, bb.remaining());

			nb = NodeBlock.decodeBlock(bb);

			assertNotNull("Decoding failed.", nb);

			assertEquals("Wrong native type.", NodeBlock.BLOCK_MESSAGE, nb.getNativeType());

			res = (MessageB) nb;

			assertEquals("The type wasn't right transmitted.", msgCtrl.getType(), res.getType());

			assertEquals("The id wasn't right transmitted.", msgCtrl.getId(), res.getId());

			assertEquals("The size wasn't right transmitted.", msgCtrl.getMsgSize(), res.getMsgSize());

		}
	}

	/**
	 * Test method for de-/encode the MessageBlock class.
	 * 
	 * @throws InterruptedException
	 */
	@Test
	public void testMessageBlock() throws InterruptedException {
		MessageBlock mBl;
		NodeBlock nb;
		MessageBlock res;
		ByteBuffer bb;
		int size;

		Random ran = new Random();

		for (int i = 0; i < 30; i++) {
			byte[] buf = new byte[ran.nextInt(7200)];
			ran.nextBytes(buf);

			mBl = new MessageBlock(ran.nextLong(), ran.nextInt(), buf);


			size = mBl.getSize();

			bb = mBl.encode();

			assertEquals("The encoded type has a different size than predicted.", size, bb.remaining());

			nb = NodeBlock.decodeBlock(bb);

			assertNotNull("Decoding failed.", nb);

			assertEquals("Wrong native type.", NodeBlock.BLOCK_MESSAGE_BLOCK, nb.getNativeType());

			res = (MessageBlock) nb;

			assertEquals("The id wasn't right transmitted.", mBl.getId(), res.getId());

			assertEquals("The offset wasn't right transmitted.", mBl.getOffset(), res.getOffset());

			assertEquals("The data wasn't right transmitted.", mBl.getData().length, res.getData().length);

			assertArrayEquals("The data wasn't right transmitted.", mBl.getData(), res.getData());

		}
	}

	/**
	 * Test method for de-/encode the MessageBlockFeedback class.
	 * 
	 * @throws InterruptedException
	 */
	@Test
	public void testMessageBlockFeedback() throws InterruptedException {
		MessageBlockFeedback hello;
		NodeBlock nb;
		MessageBlockFeedback res;
		ByteBuffer bb;
		int size;

		Random ran = new Random();

		for (int i = 0; i < 10; i++) {
			hello = new MessageBlockFeedback((byte) ran.nextInt(), ran.nextLong(), ran.nextInt(), ran.nextInt());

			size = hello.getSize();

			bb = hello.encode();

			assertEquals("The encoded type has a different size than predicted.", size, bb.remaining());

			nb = NodeBlock.decodeBlock(bb);

			assertNotNull("Decoding failed.", nb);

			assertEquals("Wrong native type.", NodeBlock.BLOCK_MESSAGE_BLOCK_FEEDBACK, nb.getNativeType());

			res = (MessageBlockFeedback) nb;

			assertEquals("The type wasn't right transmitted.", hello.getType(), res.getType());

			assertEquals("The id wasn't right transmitted.", hello.getId(), res.getId());

			assertEquals("The length wasn't right transmitted.", hello.getLength(), res.getLength());

			assertEquals("The offset wasn't right transmitted.", hello.getOffset(), res.getOffset());

		}
	}

	/**
	 * Test method for de-/encode the Ping class.
	 * 
	 * @throws InterruptedException
	 */
	@Test
	public void testPing() throws InterruptedException {
		Ping hello;
		NodeBlock nb;
		Ping res;
		ByteBuffer bb;
		int size;

		Random ran = new Random();

		for (int i = 0; i < 10; i++) {
			hello = new Ping((byte) ran.nextInt(), ran.nextLong());

			size = hello.getSize();

			bb = hello.encode();

			assertEquals("The encoded type has a different size than predicted.", size, bb.remaining());

			nb = NodeBlock.decodeBlock(bb);

			assertNotNull("Decoding failed.", nb);

			assertEquals("Wrong native type.", NodeBlock.BLOCK_PING, nb.getNativeType());

			res = (Ping) nb;

			assertEquals("The type wasn't right transmitted.", hello.getType(), res.getType());

			assertEquals("The id wasn't right transmitted.", hello.getId(), res.getId());

		}
	}

	/**
	 * Test method for de-/encode the Multi class.
	 * 
	 * @throws InterruptedException
	 */
	@Test
	public void testMulti() throws InterruptedException {
		Multi hello;
		NodeBlock nb;
		Multi res;
		ByteBuffer bb;
		int size;

		Random ran = new Random();

		for (int i = 0; i < 20; i++) {
			byte[] buf = new byte[ran.nextInt(7200)];
			ran.nextBytes(buf);

			hello = new Multi(new Hello((byte) ran.nextInt(), ran.nextLong()), new MessageBlock(ran.nextLong(),
					ran.nextInt(), buf), new Ping((byte) ran.nextInt(), ran.nextLong()));

			testAMulti(hello);

		}

		for (int i = 0; i < 10; i++) {

			hello = new Multi(new Ignore(ran.nextInt(3000)), new Ignore(ran.nextInt(7000)), new Ignore(0));

			testAMulti(hello);

		}

		for (int i = 0; i < 10; i++) {

			hello = new Multi(new Multi(), new Ignore(ran.nextInt(3000)), new Multi(new Ignore(ran.nextInt(700)),
					new Ignore(ran.nextInt(700))), new Ignore(0));

			testAMulti(hello);

		}

		testAMulti(new Multi());
	}

	private void testAMulti(final Multi m) {
		NodeBlock nb;
		Multi res;
		ByteBuffer bb;
		int size;

		size = m.getSize();

		bb = m.encode();

		assertEquals("The encoded type has a different size than predicted.", size, bb.remaining());

		nb = NodeBlock.decodeBlock(bb);

		assertNotNull("Decoding failed.", nb);

		assertEquals("Wrong native type.", NodeBlock.BLOCK_MULTIBLOCK, nb.getNativeType());

		res = (Multi) nb;

		NodeBlock[] orig = m.getSubBlocks();

		assertEquals("Wrong count of sub blocks.", orig.length, res.getSubBlocks().length);

		for (NodeBlock sub : res.getSubBlocks()) {
			assertNotNull("A null block was transmitted.", sub);

			boolean found = false;

			for (int i = 0; i < orig.length; i++) {
				if (orig[i] == null)
					continue;

				if (orig[i].getNativeType() == sub.getNativeType() && orig[i].getSize() == sub.getSize()) {
					found = true;
					orig[i] = null;
					break;
				}
			}

			if (!found) {
				fail("SubBlock has no counter part.(1)");
			}
		}

		for (int i = 0; i < orig.length; i++) {
			if (orig[i] != null) {
				fail("SubBlock has no counter part.(2)");
			}
		}
	}

}
