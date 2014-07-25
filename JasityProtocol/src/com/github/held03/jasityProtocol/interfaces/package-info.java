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
 * The standard interfaces.
 * <p>
 * These interfaces represent the core of this library.
 * They are the thing to use.
 * <p>
 * This defines a interface to be used either to implement a
 * connection/transport system, and on the other side to implement a working
 * implementation.
 * <p>
 * The working implementation creates on the server-side a 
 * {@link com.github.held03.jasityProtocol.interfaces.Server} instance or
 * implement it. For the server it needs a
 * {@link com.github.held03.jasityProtocol.interfaces.ClientListener}
 * implementation.
 * <br>
 * On client-side the working implementation need a
 * {@link com.github.held03.jasityProtocol.interfaces.Client} instance or
 * implementation and for that in needs to implement a
 * {@link com.github.held03.jasityProtocol.interfaces.ServerListener}.
 * <br>
 * Both can have some listeners defined with
 * {@link com.github.held03.jasityProtocol.interfaces.JPListener} and
 * registered either on a
 * {@link com.github.held03.jasityProtocol.interfaces.Connection} or like
 * recommended on a
 * {@link com.github.held03.jasityProtocol.interfaces.NodeConnection}. The can
 * be retrieved from the server or client instances.
 * <br>
 * Additional both need to implement some
 * {@link com.github.held03.jasityProtocol.interfaces.Message}s or use some
 * predefined. All the used messages must be available on both sides client
 * and server. 
 * <p>
 * Such a implementation should be independent from the underlying transport
 * system. But it can be explicitly chosen by using a specific factory or by
 * changing the default factories to a specific.
 * <p>
 * To implement a transport system it needs to implement a
 * {@link com.github.held03.jasityProtocol.interfaces.Connection} and setup a
 * specific factory for it. The connection must handle at least all defined
 * message types within this package. If necessary also a
 * {@link com.github.held03.jasityProtocol.interfaces.Server} and
 * {@link com.github.held03.jasityProtocol.interfaces.Client} can be
 * implemented.
 * 
 * @author held03
 */

package com.github.held03.jasityProtocol.interfaces;