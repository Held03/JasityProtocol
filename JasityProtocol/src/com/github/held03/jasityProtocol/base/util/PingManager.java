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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Helping class to handle ping times.
 * 
 * @author held03
 */
public class PingManager {

	/**
	 * The time out for ping requests.
	 */
	public static long connectionTimeOut = 30_000;

	/**
	 * Count of ping to hold.
	 * <p>
	 * It counts only the pings which are answered.
	 */
	protected int threshold = 5;

	protected int failures = 0;

	/**
	 * List of sent but not answered pings.
	 */
	protected Set<DataSet> newPings = Collections.synchronizedSet(new HashSet<DataSet>());

	/**
	 * List of sent and answered pings.
	 */
	protected List<DataSet> holdPings = Collections.synchronizedList(new ArrayList<DataSet>());

	/**
	 * Returns the average ping time in seconds.
	 * 
	 * @return the time time
	 */
	public float getAverageTime() {
		long lastTime = Long.MAX_VALUE;
		float timeSum = 0f;
		int count = holdPings.size();

		for (DataSet ds : holdPings) {
			if (ds.timeStempSent < lastTime)
				lastTime = ds.timeStempSent;

			timeSum += ds.pingRunningTime / 1000f;
		}

		return timeSum / count;
	}

	/**
	 * Adds a newly sent ping to the list.
	 * 
	 * @param id the id of the ping
	 */
	public void addPing(final long id) {
		HashSet<DataSet> rm = new HashSet<DataSet>();

		for (DataSet ds : newPings) {
			if (System.currentTimeMillis() - ds.timeStempSent > connectionTimeOut) {
				rm.add(ds);
			}
		}

		newPings.removeAll(rm);

		newPings.add(new DataSet(id));
	}

	/**
	 * Adds the receiving of pong message.
	 * 
	 * @param id the id of the pong
	 */
	public void addPong(final long id) {
		HashSet<DataSet> rm = new HashSet<DataSet>();

		Collections.sort(holdPings);

		while (holdPings.size() >= threshold) {
			holdPings.remove(0);
		}

		for (DataSet ds : newPings) {
			if (ds.pingId == id) {
				ds.readPong();
				rm.add(ds);
				holdPings.add(ds);
				failures = 0;

			} else if (System.currentTimeMillis() - ds.timeStempSent > connectionTimeOut) {
				rm.add(ds);
				failures++;
			}
		}

		newPings.removeAll(rm);


	}

	/**
	 * Checks if the remote sends no more pings back.
	 * <p>
	 * Note: This would return <code>false</code> if no ping was ever received,
	 * even if the minMissingCount was exceeded, until the maxMissingCount was
	 * reached.
	 * <p>
	 * Note: this can be used to determinate when a connection hangs up.
	 * 
	 * @param minMissingCount the minimum of unanswered pings to return
	 *            <code>true</code>, if the last received answer is too long
	 *            ago.
	 * @param maxMissingCount the count of unanswered pings to return
	 *            <code>true</code>, any way
	 * @return <code>true</code> if no more pings were answered.
	 */
	public boolean checkPingsTimedOut(final int minMissingCount, final int maxMissingCount) {
		if (failures >= maxMissingCount)
			return true;

		if (holdPings.size() == 0)
			return false;

		DataSet newest = holdPings.get(holdPings.size() - 1);

		return (minMissingCount <= failures && (System.currentTimeMillis() - newest.timeStempSent) > connectionTimeOut);
	}

	/**
	 * Creates a new empty ping manager.
	 */
	public PingManager() {

	}

}


class DataSet implements Comparable<DataSet> {

	/**
	 * The time the time this ping was sent.
	 */
	public final long timeStempSent = System.currentTimeMillis();

	/**
	 * The id of the ping.
	 */
	public final long pingId;

	/**
	 * The time the ping has take to travel through the Internet.
	 * <p>
	 * As long as the pong wasn't received this is <code>-1</code>.
	 */
	public long pingRunningTime = -1;

	/**
	 * Creates a new representation of a newly sent ping.
	 * 
	 * @param pingId the id of the ping
	 */
	public DataSet(final long pingId) {
		this.pingId = pingId;
	}

	/**
	 * Sets up the running time.
	 * <p>
	 * If the running time was already set, nothing will changed.
	 */
	public void readPong() {
		if (pingRunningTime == -1) {
			pingRunningTime = System.currentTimeMillis() - timeStempSent;
		}
	}

	/**
	 * Returns <code>true</code> if a pong for this ping was received.
	 * 
	 * @return if the runtime was available
	 */
	public boolean hasDone() {
		return (pingRunningTime != -1);
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(final DataSet o) {
		return Long.compare(timeStempSent, o.timeStempSent);
	}

}
