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

package com.github.held03.jasityProtocol;

import java.util.HashSet;
import java.util.Set;

import com.github.held03.jasityProtocol.interfaces.BackEnd;


/**
 * Static class for general library stuff.
 * Contains stuff like the current version of the library and a back end
 * registration.
 * 
 * @author held03
 */
public class Jasity {

	/**
	 * The version code of this implementation.
	 * <p>
	 * This is the official library version of the JasityProtocol locally in
	 * use. It can be used as a feature reference of for printing version
	 * information or similar.
	 * 
	 * <pre>
	 * Previous released versions:
	 * 
	 * Code | Name |   Date   | Notes
	 * -----+------+----------+----------------
	 *    0 | v0.0 |    -     | Pre-release version
	 *    1 | v1.0 |2014-12-01| First release
	 *    2 |   -  |    -     | unreleased (CURRENT)
	 * </pre>
	 * 
	 * @see #CURRENT_VERSION_NAME
	 */
	public static final long CURRENT_VERSION = 2;

	/**
	 * String representing the current version in a human readable kind.
	 * 
	 * @see #CURRENT_VERSION
	 */
	public static final String CURRENT_VERSION_NAME = "v1.1";

	/**
	 * List of all registered back ends.
	 * <p>
	 * It is not necessary to register a back end.
	 */
	private static Set<BackEnd> backends = new HashSet<BackEnd>();

	/**
	 * Closed constructor. It is NOT for any use.
	 */
	private Jasity() {
		/*
		 * Should be never used.
		 */
	}

}
