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
package net.petercashel.nettyCore.common.packetCore;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public interface IPacketBase {

	public int getPacketID();
	public ByteBuf getPacket();
	public void setPacket(ByteBuf buf);
	public void pack();
	public void unpack();
	public void execute(ChannelHandlerContext ctx);

}
