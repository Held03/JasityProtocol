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

package com.github.held03.jasityProtocol.interfaces;

import java.nio.ByteBuffer;


/**
 * A Filter can modify all packages send by a NodeConnection.
 * <p>
 * This can be used for example to add a compression or a encryption to a
 * connection. It always relates to a single {@link NodeConnection}. So that it
 * exist one instance on server side an one on client side.
 * 
 * @author held03
 */
public interface Filter {

	/**
	 * Called if a connection is established.
	 * <p>
	 * This method is for setup the filter. And to link the filter to the given
	 * {@link NodeConnection}.
	 * <p>
	 * It has to call the {@link NodeConnection#filterInitiated(Filter)} method
	 * with it self as argument. Until that method was called by this filter it
	 * can only send {@link Messages} with
	 * {@link Message.Transport#PRE_FILLTERED} and all other messages also from
	 * other origin have to wait till it was called. This messages can be used
	 * to do some set ups.
	 * <p>
	 * If any other connection called this function in advance it must throw a
	 * {@link IllegalStateException}.
	 */
	public void startConnection(NodeConnection node);

	/**
	 * This is called if the connection ends.
	 * <p>
	 * It can be used to do some cleaning stuff. It will be no more possible to
	 * send any message.
	 */
	public void endConnection();

	/**
	 * Applies the filter to the given message bytes.
	 * <p>
	 * This will be invoked for every package transmitted over the related
	 * {@link NodeConnection}.
	 * <p>
	 * It is possible to change and return the given buffer.
	 * 
	 * @see #restore(ByteBuffer)
	 * @param buffer the package to edit
	 * @return the changed buffer
	 */
	public ByteBuffer apply(ByteBuffer buffer);

	/**
	 * Restores the changes of this filter on the given message bytes.
	 * <p>
	 * This method has to restore the changes of {@link #apply(ByteBuffer)}
	 * method. Following must be valid, if <code>buffer</code> is a ByteBuffer:
	 * 
	 * <pre>
	 * original = buffer.clone();
	 * apply(buffer);
	 * restore(buffer);
	 * assert buffer.equals(original) == true;
	 * </pre>
	 * 
	 * e.g. if {@link #apply(ByteBuffer)} compress the package, this method must
	 * decompress it.
	 * <p>
	 * Notice that the apply and the restore method are called on different
	 * instances of different systems, means one on server-side and one on
	 * client-side
	 * <p>
	 * It is possible to change and return the given buffer.
	 * 
	 * @see #apply(ByteBuffer)
	 * @param buffer the package to restore
	 * @return the changed buffer
	 */
	public ByteBuffer restore(ByteBuffer buffer);
}
