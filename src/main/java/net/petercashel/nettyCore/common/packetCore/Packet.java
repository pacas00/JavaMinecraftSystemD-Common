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
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

public class Packet extends PacketBase implements IPacketBase{
	
	// packetID is 2 bytes, so total packet size is packetBufSize + 4 bytes.
	public static final int packetBufSize = 65536;
	public static final int packetHeaderSize = 8;
	
	public Packet(int i, ByteBuf buf) {
		this.packetID = i;
		this.packet = buf;
	}

	@Override
	public int getPacketID() {
		// TODO Auto-generated method stub
		return packetID;
	}

	@Override
	public ByteBuf getPacket() {
		// TODO Auto-generated method stub
		return packet;
	}

	@Override
	public void pack() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unpack() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void execute(ChannelHandlerContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPacket(ByteBuf buf) {
		this.packet = buf;
		
	}
}
