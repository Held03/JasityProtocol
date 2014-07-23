package de.wag_web.jubp2.base;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.wag_web.jubp2.interfaces.Connection;
import de.wag_web.jubp2.interfaces.JUBP2Listener;
import de.wag_web.jubp2.interfaces.Message;
import de.wag_web.jubp2.interfaces.ConnectionManager;

/**
 * The general implementation.
 * <p>
 * This class implements some general purpose features of the connection.
 * But is still flexible.
 * <p>
 * @author held03
 *
 */
public abstract class AbstractConnection implements Connection {

	/**
	 * The set of all registered connection listeners.
	 */
	protected Set<ListenerContainer> messageListeners = new HashSet<>();
	
	/**
	 * The manager of this connection.
	 * <p>
	 * This is the connection callback.
	 */
	protected ConnectionManager manager;
	
	/**
	 * Normal constructor.
	 */
	public AbstractConnection(ConnectionManager manager) {
		this.manager = manager;
		
	}

	@Override
	public Future<Boolean> send(Message message) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addListener(Object listener) {
		HashSet<ListenerContainer> msgToAdd = new HashSet<>();
		
		Class<?> clazz = listener.getClass();
		
		for (Method method : clazz.getMethods()) {
			JUBP2Listener list = method.getAnnotation(JUBP2Listener.class);
			
			if (list == null)
				continue;
			
			Class<?> returning = method.getReturnType();
			
			if (!returning.equals(Boolean.class))
				continue;
			
			Class<?>[] parameter = method.getParameterTypes();
			
			if (parameter.length != 1 || !Message.class.isAssignableFrom(parameter[0]))
				continue;
			
			msgToAdd.add(new ListenerContainer(listener, parameter[0].asSubclass(Message.class), method, list.level()));
		}
		
		if (msgToAdd.isEmpty()) {
			Logger.getLogger(AbstractConnection.class.getName()).log(Level.WARNING, 
					"A listener was added but it could NOT find a valid listener method! For object {1} into {2}",
					new Object[]{listener, this});
			
			return;
		}
		
		synchronized (messageListeners) {
			messageListeners.addAll(msgToAdd);
		}
	}

	@Override
	public void removeListener(Object listener) {
		synchronized (messageListeners) {
			HashSet<ListenerContainer> msgToRm = new HashSet<>();
			
			for (ListenerContainer container : messageListeners) {
				if (container.object.equals(listener)) {
					msgToRm.add(container);
				}
			}
			
			messageListeners.removeAll(msgToRm);
		}
	}
	
	/**
	 * Delivers a message to the specific listeners.
	 * <p>
	 * First this method will check the message with the manager {@link ConnectionManager#checkMessage(Message)}.
	 * If it returns <code>true</code> it will be delivered to all fitting and registered listeners.
	 * Until one listener returned <code>true</code> or the end of the list is reached.
	 * <p>
	 * If a listener of a specific level returns <code>true</code> all other listener of the same level
	 * gets the message still before this method returns.
	 * At least all listeners of lower levels don't get the message.
	 * <p>
	 * @param msg
	 */
	protected void deliverMessage(Message msg) {
		if (manager == null) {
			Logger.getLogger(AbstractConnection.class.getName()).log(Level.WARNING, 
					"A message was received but no manager was set for the connection!");
			throw new IllegalStateException("Message couldn't be delivered, because no manager was set.");
		}
		
		if (manager.checkMessage(msg)) {
			//manager accept message

			HashMap<JUBP2Listener.Level, HashSet<ListenerContainer>> msgToSend = new HashMap<>();
			
			synchronized (messageListeners) {
				for (ListenerContainer container : messageListeners) {
					if (container.message.isAssignableFrom(msg.getClass())) {
						if (msgToSend.get(container.priority) == null)
							msgToSend.put(container.priority, new HashSet<ListenerContainer>());
						msgToSend.get(container.priority).add(container);
					}
				}
			}
			
			boolean consumed = false;
			
			for(JUBP2Listener.Level level : JUBP2Listener.Level.values()) {
				for(ListenerContainer container : msgToSend.get(level)) {
					try {
						if (container.callback.invoke(container.object, msg).equals(Boolean.TRUE)) {
							consumed = true;
						}
					} catch (IllegalAccessException e) {
						e.printStackTrace();
						Logger.getLogger(AbstractConnection.class.getName()).log(Level.WARNING, 
								"Listener invokation fails!", e);
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
						Logger.getLogger(AbstractConnection.class.getName()).log(Level.WARNING, 
								"Listener invokation fails!", e);
					} catch (InvocationTargetException e) {
						e.printStackTrace();
						Logger.getLogger(AbstractConnection.class.getName()).log(Level.WARNING, 
								"Listener invokation fails!", e);
					}
				}
				
				if (consumed)
					break;
			}
			
		} else {
			//manager reject message
		}
	}
	
	/**
	 * Contains a listener object.
	 * <p>
	 * This class is for holding the listeners related objects together.
	 * They are the Listener instance, the message to listening for and the method signature to invoke.
	 * <p>
	 * Since it is possible that a single listener object can have multiple listener methods,
	 * each method gets its own entry of this class.
	 * <p>
	 * @see AbstractConnection#messageListeners
	 * @author held03
	 *
	 */
	protected class ListenerContainer {
		
		/**
		 * The instance of the listener.
		 */
		public Object object;
		
		/**
		 * The message type the listener is for.
		 */
		public Class<? extends Message> message;
		
		/**
		 * The method of the listener to invoke.
		 * <p>
		 * This method must have the {@link JUBP2Listener} annotation,
		 * must return a boolean value and must have exactly one argument
		 * which takes a {@link Message} or a sub class/interface.
		 */
		public Method callback;
		
		/**
		 * The priority of the listener.
		 */
		public JUBP2Listener.Level priority;
		
		/**
		 * Creates a new container with given content.
		 */
		public ListenerContainer(Object object, Class<? extends Message> message, Method callback,
				JUBP2Listener.Level priority) {
			this.object = object;
			this.message = message;
			this.callback = callback;
			this.priority = priority;
			
		}
		
		/**
		 * Creates a empty instance.
		 */
		public ListenerContainer() {
			
		}
	}
}
