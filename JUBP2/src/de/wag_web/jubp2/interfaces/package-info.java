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
 * For a working usage it needs to implement the {@link de.wag_web.jubp2.interfaces.ClientListener},
 * {@link de.wag_web.jubp2.interfaces.ServerListener} and some {@link de.wag_web.jubp2.interfaces.Message}s.
 * Additional it is possible to use the {@link de.wag_web.jubp2.interfaces.JUBP2Listener} annotation to
 * create message listeners.
 * <p>
 * The other interfaces represents the server client system which is implemented by this library,
 * but are still implementable for expansion.
 * <p>
 * @author held03
 */

package de.wag_web.jubp2.interfaces;