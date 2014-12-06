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


/**
 * This is the main package of the JasityProtocol library.
 * <p/>
 * The library provides a general interface to communicate over different
 * network structures in a server-client meaner.
 * <p>
 * This library can be implemented to integrate a new back end or to use a back
 * end for an application. The actual connections are realized as {@link Node}s
 * which represents the end points of any connection. These <code>Node</code>s
 * handles the message encoding, sending, receiving, reassemble and decoding.
 * For all this tasks exists a standard implementation
 * {@link com.github.held03.jasityProtocol.base.DefaultNode} which should meet
 * the most requires.
 * <p>
 * The actual binary sending and routing is handled by the
 * {@link com.github.held03.jasityProtocol.interfaces.Connection} which is
 * specified by the back end. As further tools, it exist
 * {@link com.github.held03.jasityProtocol.interfaces.MessageCoder} which are
 * able to encode a <code>Message</code> object into a byte array.
 * <p>
 * Message are written by implements the <code>Message</code> interface. The
 * would be automatically coded into a binary from by an
 * <code>MessageCoder</code>, it a
 * {@link com.github.held03.jasityProtocol.interfaces.BinaryMessage} was
 * implemented the message can encode and decode it self.
 * <h2>Implementation</h2>
 * <h3>For Applications</h3> The to communicating sides are categorized as
 * server side and client side. The server side needs to open an
 * {@link com.github.held03.jasityProtocol.interfaces.Server} The server
 * instance can be retrieved from the
 * {@link com.github.held03.jasityProtocol.interfaces.BackEnd} you want to use.
 * To allow to connect to, a
 * {@link com.github.held03.jasityProtocol.interfaces.ServerListener} has to be
 * implemented by the application and applied on the server. Through it the
 * application can receive the new connections.
 * <p>
 * The client side has to create or get a
 * {@link com.github.held03.jasityProtocol.interfaces.Address} instance for the
 * specific back end. Through this specification, the back ends can be very
 * individual and are not only bounded the IP. With the <code>Address</code>
 * instance a connection to the target server can be opened.
 * <p>
 * Both sides will finally receive a <code>Node</code> instance, with it
 * {@link com.github.held03.jasityProtocol.interfaces.Message}s can be send and
 * listeners for messages can be registered. Listeners are any objects, where at
 * least on method has the
 * {@link com.github.held03.jasityProtocol.interfaces.JPListener} annotation.
 * <h3>For Back ends</h3> To add new back end a BackEnd implementation should be
 * written. Also an <code>Address</code> and <code>Connection</code>
 * implementation is needed.
 * 
 * @author held03
 */

package com.github.held03.jasityProtocol;

