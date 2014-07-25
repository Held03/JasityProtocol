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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Message listener method.
 * <p>
 * This annotation is for marking methods which serves as message listener.
 * <p>
 * The message argument of the method defines the type of message which will be
 * handled by this listener. It is defined either as a class or a interface. All
 * messages which implements or extends this will be received, as well as the
 * class self. <br>
 * If a method uses {@link Message} as argument will receive every message. <br>
 * Anyway the class of this message argument must implement or extend the
 * {@link Message} interface.
 * <p>
 * The listener method needs specific arguments. There are two possible
 * configurations: * first one argument, the message argument. * second two
 * arguments, 1. the message argument, 2. a {@link NodeConnection}
 * <p>
 * Important such a method must return a boolean value. That indicate if the
 * message was consumed. That means if a early listener returns
 * <code>true</code> the message will be NOT future forward to the other
 * listeners. Therefore is also like a cancel value. <br>
 * The default return value of such a method is <code>false</code>.
 * <p>
 * It is allowed that a class/object has multiple and different such listeners.
 * They are registered at once if the object is registered. And separately
 * invoked as they are defined.
 * 
 * @author held03
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface JPListener {

	public enum Level {
		/**
		 * Receives the messages as first.
		 * <p>
		 * This is the highest priority.
		 */
		FISRT,

		/**
		 * Receives the messages earlier.
		 * <p>
		 * This is the higher priority.
		 */
		EARLIER,

		/**
		 * Receives the messages as usually.
		 * <p>
		 * This is the normal priority.
		 */
		NORMAL,

		/**
		 * Receives the messages later.
		 * <p>
		 * This is the lower priority.
		 */
		LATER,

		/**
		 * Receives the messages as last.
		 * <p>
		 * This is the lowest priority.
		 */
		LAST
	};

	/**
	 * The priority level for this listener.
	 * 
	 * @return the level of the priority
	 */
	Level level() default Level.NORMAL;
}
