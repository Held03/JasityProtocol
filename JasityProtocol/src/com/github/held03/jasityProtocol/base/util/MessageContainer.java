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

import com.github.held03.jasityProtocol.interfaces.MessageCoder;


/**
 * Contains the binary data of a message.
 * <p>
 * This class contains the binary data of a message used to send or receive it.
 * 
 * @author adam
 */
public class MessageContainer {

	final byte[] binaryData;
	final long messageID;

	long lastUpdate = System.currentTimeMillis();

	/**
	 * 
	 */
	public MessageContainer(final long messageID, final byte[] binaryData) {
		this.messageID = messageID;
		this.binaryData = binaryData;

	}

	/**
	 * Copying the given block into this container.
	 * 
	 * @param data the data to insert
	 * @param offset the beginning of the data
	 */
	public void putData(final byte[] data, final int offset) {
		System.arraycopy(data, 0, binaryData, offset, data.length);

		setUpdate();
	}

	/**
	 * Updates the last update field.
	 */
	public void setUpdate() {
		lastUpdate = System.currentTimeMillis();
	}

	/**
	 * Get the time stamp of the last update.
	 * <p>
	 * This can be used to check out of time messages.
	 * 
	 * @return the time of last changing.
	 */
	public long getLastUpdate() {
		return lastUpdate;
	}

	/**
	 * Gets the message id for this message container.
	 * 
	 * @return the id
	 */
	public long getId() {
		return messageID;
	}

	/**
	 * Gets the binary data of this message container.
	 * <p>
	 * If this container is complete it can be decoded by a {@link MessageCoder}.
	 * 
	 * @return the message data
	 */
	public byte[] getData() {
		return binaryData;
	}

	/**
	 * Returns the length of the internal data array.
	 * <p>
	 * During receiving, this means how many is generally to receive, and NOT
	 * how many was already received.
	 * 
	 * @return the data length
	 */
	public int getDataLength() {
		return binaryData.length;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (messageID ^ (messageID >>> 32));
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
		if (! (obj instanceof MessageContainer)) {
			return false;
		}
		MessageContainer other = (MessageContainer) obj;
		if (messageID != other.messageID) {
			return false;
		}
		return true;
	}



}
