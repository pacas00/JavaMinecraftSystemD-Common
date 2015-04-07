package net.petercashel.nettyCore.common.exceptions;

import io.netty.channel.Channel;

public class ConnectionShuttingDown extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2981804382862618238L;

	public ConnectionShuttingDown() {
		super("Connection is shutting down.");
	}

	public ConnectionShuttingDown(String paramString) {
		super(paramString);
	}

	public ConnectionShuttingDown(String paramString, Throwable paramThrowable) {
		super(paramString, paramThrowable);
	}

	public ConnectionShuttingDown(Throwable paramThrowable) {
		super(paramThrowable);
	}
}
