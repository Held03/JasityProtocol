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

package com.github.held03.jasityProtocol.udp;

import java.util.List;

import com.github.held03.jasityProtocol.interfaces.Message;
import com.github.held03.jasityProtocol.interfaces.NodeConnection;
import com.github.held03.jasityProtocol.interfaces.Server;
import com.github.held03.jasityProtocol.interfaces.ServerListener;


public class UDPServer implements Server {

	public UDPServer() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void startConnection() {
		// TODO Auto-generated method stub

	}

	@Override
	public void stopConnection() {
		// TODO Auto-generated method stub

	}

	@Override
	public void broadcast(Message msg) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addListener(ServerListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeListener(ServerListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<NodeConnection> getNodes() {
		// TODO Auto-generated method stub
		return null;
	}

}
