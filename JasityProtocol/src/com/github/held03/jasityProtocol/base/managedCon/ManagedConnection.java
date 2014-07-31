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

package com.github.held03.jasityProtocol.base.managedCon;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.held03.jasityProtocol.base.AbstractConnection;
import com.github.held03.jasityProtocol.interfaces.Connection;
import com.github.held03.jasityProtocol.interfaces.ConnectionManager;
import com.github.held03.jasityProtocol.interfaces.NodeConnection;


/**
 * Helper class for connection implementation.
 * <p>
 * This class provides a framework for a implementation. This reduces the needed
 * work of implementations of only a few things.
 * 
 * @author held03
 */
public class ManagedConnection extends AbstractConnection implements Connection {

	/**
	 * Collection of the last pings.
	 * <p>
	 * List all last pings in seconds.
	 */
	protected HashSet<TimedValue<Float>> pings = new HashSet<>();

	/**
	 * Time to keep the pings in milliseconds.
	 * <p>
	 * default value: 5 minutes = 300_000
	 */
	protected long holdPings = 5 * 60 * 1000;

	/**
	 * Collection of all load values.
	 */
	protected HashSet<TimedValue<Float>> load = new HashSet<>();

	/**
	 * Creates a new ManagedConnection.
	 */
	public ManagedConnection(final ConnectionManager conMan) {
		super(conMan);
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	/**
	 * Removes old values.
	 * <p>
	 * This removes all values which are older the the given time stamp.
	 * 
	 * @param set the list to check
	 * @param timeMili the oldest time to keep
	 */
	public static <T> void removeBefore(final Set<TimedValue<T>> set, final long timeMili) {
		if (set == null) {
			throw new NullPointerException();
		}

		HashSet<TimedValue<T>> remove = new HashSet<TimedValue<T>>();

		for (TimedValue<T> val : set) {
			if (val.miliTime < timeMili) {
				remove.add(val);
			}
		}

		set.removeAll(remove);
	}

	@Override
	public float getPingTime() {

		synchronized (pings) {
			// remove all entries older than hold time
			removeBefore(pings, System.currentTimeMillis() - holdPings);

			if (pings.size() == 0)
				return 10;

			float summe = 0;

			for (TimedValue<Float> val : pings) {
				summe += val.value;
			}

			return summe / pings.size();
		}
	}

	/**
	 * Gets count of ping used to get ping time.
	 * <p>
	 * This method returns how many ping were sent during {@link #holdPings}
	 * time. This is also the count of ping times used to generate the
	 * {@link #getPingTime()}
	 * <p>
	 * {@link #setPingHold(long)} can change the the time range the pings are
	 * collected. If too less pings are use the {@link #holdPings} time can be
	 * increased. If too many pings are use the {@link #holdPings} time can be
	 * decreased.
	 * 
	 * @see #setPingHold(long)
	 * @see #getPingHold()
	 * @return
	 */
	public int getPingSignificans() {

		synchronized (pings) {
			// remove all entries older than hold time
			removeBefore(pings, System.currentTimeMillis() - holdPings);

			return pings.size();
		}
	}

	/**
	 * Adds a ping time to the list in seconds.
	 * <p>
	 * This pings are hold for 5 minutes. That means the ping time needs to be
	 * checked at least every 5 minutes. It is recommend to check this every
	 * minute.
	 * 
	 * @param time the last ping time to add
	 */
	public void addPingTime(final float time) {
		synchronized (pings) {
			removeBefore(pings, System.currentTimeMillis() - holdPings);

			pings.add(new TimedValue<Float>(time));
		}
	}

	/**
	 * Gets the time pings will be held in milliseconds.
	 * <p>
	 * All ping times added during this last time, will be kept and used to get
	 * the average ping time got by {@link #getPingTime()}.
	 * 
	 * @see #setPingHold(long)
	 * @see #getPingTime()
	 * @see #holdPings
	 * @return the ping keeping time
	 */
	public long getPingHold() {
		return holdPings;
	}

	/**
	 * Sets the time pings will be held in millisconds.
	 * <p>
	 * All ping times added during this last time, will be kept and used to get
	 * the average ping time got by {@link #getPingTime()}.
	 * 
	 * @see #setPingHold(long)
	 * @see #getPingTime()
	 * @see #holdPings
	 * @param time the millisecond to set
	 */
	public void setPingHold(final long time) {
		holdPings = time;
	}

	@Override
	public float getConectionOutputLoad1() {
		synchronized (load) {
			removeBefore(load, System.currentTimeMillis() - 5 * 60 * 1000);

			float summe = 0;
			int count = 0;

			long lastMin = System.currentTimeMillis() - 60_000;

			for (TimedValue<Float> val : load) {
				// use only values newer than 1 minute
				if (val.miliTime > lastMin) {
					summe += val.value;
					count++;
				}
			}

			if (count == 0)
				return 1;

			return summe / count;
		}
	}

	@Override
	public float getConectionOutputLoad5() {
		synchronized (load) {
			removeBefore(load, System.currentTimeMillis() - 5 * 60 * 1000);

			float summe = 0;

			if (load.size() == 0)
				return 1;

			for (TimedValue<Float> val : load) {
				summe += val.value;
			}


			return summe / load.size();
		}
	}

	/**
	 * Adds a load value.
	 * <p>
	 * This will be used to create the average connection load got by
	 * {@link #getConectionOutputLoad1()} and {@link #getConectionOutputLoad5()}.
	 * <p>
	 * This value ranges from 0.0 to 1.0 <br>
	 * 0.0 means that the connection is completely idle, and 1.0 means that the
	 * connection is completely busy.
	 * 
	 * @param loadVal
	 */
	public void addLoadValue(final float loadVal) {
		synchronized (load) {
			removeBefore(load, System.currentTimeMillis() - 5 * 60 * 1000);

			load.add(new TimedValue<Float>(loadVal));
		}
	}

	@Override
	public List<NodeConnection> getRelatedNodes() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * A value with a specific adding time.
	 * <p>
	 * This allows to track how old the values are.
	 * 
	 * @author held03
	 * @param <T> Value type to store
	 */
	public class TimedValue<T> {

		/**
		 * The time this was added in milliseconds.
		 * <p>
		 * This the time stamp when the value was added or was generated.
		 * <p>
		 * This is usually get by calling
		 * <code>System.currentTimeMillis()</code>.
		 */
		public long miliTime;

		/**
		 * The value which is actually stored.
		 */
		public T value;

		/**
		 * Empty constructor.
		 * <p>
		 * This gets a TimedValue with no time and no value.
		 */
		public TimedValue() {

		}

		/**
		 * Creates a TimedValue for the given value.
		 * <p>
		 * This gets a TimedValue with the given value and a time set to the
		 * moment when this method was called.
		 * 
		 * @param value the value to set
		 */
		public TimedValue(final T value) {
			this.value = value;
			miliTime = System.currentTimeMillis();
		}

		/**
		 * Create a TimedValue for the given values.
		 * <p>
		 * 
		 * @param value
		 * @param miliTime
		 */
		public TimedValue(final T value, final long miliTime) {
			this.value = value;
			this.miliTime = miliTime;

		}
	}
}
