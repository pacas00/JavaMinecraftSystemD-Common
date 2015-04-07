/*******************************************************************************
 * Copyright (c) 2015 Peter Cashel (pacas00@petercashel.net). All rights reserved.
 * This program and the accompanying materials are made available under the terms
 * of the Creative Commons Attribution-NoDerivatives 4.0 International License
 * which accompanies this distribution, and is available at
 * http://creativecommons.org/licenses/by-nd/4.0/.
 *
 * Contributors:
 *     Peter Cashel - initial implementation
 *******************************************************************************/
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
