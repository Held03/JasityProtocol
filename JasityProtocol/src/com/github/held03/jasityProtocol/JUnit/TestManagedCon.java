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

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.github.held03.jasityProtocol.base.managedCon.ManagedConnection;


/**
 * @author held03
 */
public class TestManagedCon {

	ManagedConnection con;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		con = new ManagedConnection(null);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for
	 * {@link com.github.held03.jasityProtocol.base.managedCon.ManagedConnection#getPingTime()}
	 * .
	 * 
	 * @throws InterruptedException
	 */
	@Test
	public void testGetPingTime() throws InterruptedException {

		con.setPingHold(3000);

		// 1. test: simple accumulation
		con.addPingTime(0.6f);
		con.addPingTime(0.8f);
		con.addPingTime(1.2f);

		Assert.assertEquals("1. test: simple accumulation", 0.866f, con.getPingTime(), 0.01f);

		// 2. test: remove feature

		// set max ping time
		con.setPingHold(1000);

		// skip most of the time and add one value
		Thread.sleep(800);
		con.addPingTime(0.2f);

		// skip rest time of forget fist values
		Thread.sleep(210);

		Assert.assertEquals("2. test: remove feature", 0.2f, con.getPingTime(), 0.01f);

	}

	/**
	 * Test method for
	 * {@link com.github.held03.jasityProtocol.base.managedCon.ManagedConnection#getPingHold()}
	 * .
	 */
	@Test
	public void testGetPingHold() {
		con.setPingHold(1000);

		Assert.assertEquals("1. test: set to 1000ms", 1000, con.getPingHold());

		con.setPingHold(2);

		Assert.assertEquals("1. test: set to 2ms", 2, con.getPingHold());

		con.setPingHold(686542119);

		Assert.assertEquals("1. test: set to 686542119ms", 686542119, con.getPingHold());
	}

	/**
	 * Test method for
	 * {@link com.github.held03.jasityProtocol.base.managedCon.ManagedConnection#getConectionOutputLoad1()}
	 * .
	 * <p>
	 * This takes up to 6 minutes to proceed.
	 * 
	 * @throws InterruptedException
	 */
	@Test
	public void testGetConectionOutputLoad() throws InterruptedException {
		con.addLoadValue(0.7f);
		con.addLoadValue(0.2f);
		con.addLoadValue(0.6f);

		System.out.println("0s1-5: " + con.getConectionOutputLoad1() + " " + con.getConectionOutputLoad5());
		Assert.assertEquals("simple test 1min", 0.5f, con.getConectionOutputLoad1(), 0.01f);
		Assert.assertEquals("simple test 5min", 0.5f, con.getConectionOutputLoad5(), 0.01f);

		// wait 10s
		Thread.sleep(10010);

		con.addLoadValue(0.0f);

		System.out.println("10s1-5: " + con.getConectionOutputLoad1() + " " + con.getConectionOutputLoad5());
		Assert.assertEquals("10s test 1min", 0.375f, con.getConectionOutputLoad1(), 0.01f);
		Assert.assertEquals("10s test 5min", 0.375f, con.getConectionOutputLoad5(), 0.01f);

		// wait 20s
		Thread.sleep(20010);

		con.addLoadValue(0.4f);

		System.out.println("30s1-5: " + con.getConectionOutputLoad1() + " " + con.getConectionOutputLoad5());
		Assert.assertEquals("30s test 1min", 0.38f, con.getConectionOutputLoad1(), 0.01f);
		Assert.assertEquals("30s test 5min", 0.38f, con.getConectionOutputLoad5(), 0.01f);

		// wait 30s
		Thread.sleep(30010);

		con.addLoadValue(0.5f);

		System.out.println("1m1-5: " + con.getConectionOutputLoad1() + " " + con.getConectionOutputLoad5());
		Assert.assertEquals("1:00 test 1min", 0.3f, con.getConectionOutputLoad1(), 0.01f);
		Assert.assertEquals("1:00 test 5min", 0.4f, con.getConectionOutputLoad5(), 0.01f);

		// wait 60s
		Thread.sleep(60010);

		con.addLoadValue(0.4f);

		System.out.println("2m1-5: " + con.getConectionOutputLoad1() + " " + con.getConectionOutputLoad5());
		Assert.assertEquals("2:00 test 1min", 0.4f, con.getConectionOutputLoad1(), 0.01f);
		Assert.assertEquals("2:00 test 5min", 0.4f, con.getConectionOutputLoad5(), 0.01f);

		// wait 180s
		Thread.sleep(180010);

		con.addLoadValue(0.2f);
		con.addLoadValue(0.0f);

		System.out.println("5m1-5: " + con.getConectionOutputLoad1() + " " + con.getConectionOutputLoad5());
		Assert.assertEquals("5:00 test 1min", 0.1f, con.getConectionOutputLoad1(), 0.01f);
		Assert.assertEquals("5:00 test 5min", 0.25f, con.getConectionOutputLoad5(), 0.01f);

		// wait 60s
		Thread.sleep(60010);

		con.addLoadValue(1f);

		System.out.println("6m1-5: " + con.getConectionOutputLoad1() + " " + con.getConectionOutputLoad5());
		Assert.assertEquals("6:00 test 1min", 1f, con.getConectionOutputLoad1(), 0.01f);
		Assert.assertEquals("6:00 test 5min", 0.4f, con.getConectionOutputLoad5(), 0.01f);

	}

}
