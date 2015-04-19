package net.petercashel.commonlib.event.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;

public class EventDispatcher {

	public LinkedBlockingQueue<ASMHandlerObject> handlersToProcess = new LinkedBlockingQueue<ASMHandlerObject>();

	private Collection handlers = new ArrayList();

	/**
	 * Adds an event handler.
	 */
	public void addHandler(Object object) {
		this.handlers.add(object);
	}

	/**
	 * Dispatch an event to the registered handlers.
	 */
	public void dispatchEvent(Event event) {
		for (Object handler : handlers) {
			dispatchEventTo(event, handler);

		}
	}

	protected void dispatchEventTo(Event event, Object handler) {
		System.out.println(event.getClass().getClassLoader().toString());
		System.out.println(handler.getClass().getClassLoader().toString());
		Collection<Method> methods = findMatchingEventHandlerMethods(handler, event.getEventName());
		for (Method method : methods) {
			try {
				// Make sure the method is accessible (JDK bug ?)
				method.setAccessible(true);
				System.out.println(event.getClass().getClassLoader().toString());
				System.out.println(handler.getClass().getClassLoader().toString());
				
				if (method.getParameterTypes().length == 1) {
					if ((event.getClass().isInstance(method.getParameterTypes()[0]))) {
						System.out.println("Dispatch:dispatchEventTo FIRE");
						method.invoke(handler, event);
					}

				}
			} catch (Exception e) {
				System.err.println("Could not invoke event handler!");
				e.printStackTrace(System.err);
			}
		}
	}

	/** 
	 * Find all methods from the <em>handler</em> object that must be called, based on the presence
	 * of the HandleEvent annotation. 
	 */
	private Collection<Method> findMatchingEventHandlerMethods(Object handler, String eventName) {
		Method[] methods = handler.getClass().getDeclaredMethods();
		Collection<Method> result = new ArrayList<Method>();
		for (Method method : methods) {
			if (canHandleEvent(method, eventName)) {
				result.add(method);
			}
		}
		return result;
	}

	/**
	 * Look for the annotation values.
	 */
	private boolean canHandleEvent(Method method, String eventName) {

		if (method.getParameterTypes()[0].getSimpleName().equals(eventName)) return true;
		return false;
	}

	public void processQueue() {
		while (handlersToProcess.size() > 0) {
			ASMHandlerObject a = null;
			try {
				a = handlersToProcess.take();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			Class<?> cls = null;
			try {
				cls = Class.forName(a.Class, false, ClassLoader.getSystemClassLoader());
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			try {
				addHandler(cls.newInstance());
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
}
