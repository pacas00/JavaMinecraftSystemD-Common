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

import net.petercashel.nettyCore.common.PacketRegistry;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

public class PacketBase {
	
	public static int packetID = -1;
	public ByteBuf packet;
	
	public void sendPacket(ChannelHandlerContext ctx) {
		ByteBuf b = ctx.alloc().buffer(Packet.packetBufSize + Packet.packetHeaderSize, Packet.packetBufSize + Packet.packetHeaderSize);
		b.writeInt(PacketRegistry.GetOtherSide());
		b.writeInt(packetID);
		b.writeBytes(packet);
		if (b.readableBytes() == (Packet.packetBufSize + Packet.packetHeaderSize)) {
			ctx.writeAndFlush(b);
		} else if (b.readableBytes() > (Packet.packetBufSize + Packet.packetHeaderSize)) {
			System.out.println("INVALID PACKET! DISCARDING!");
		} else {
			b.writeZero(b.writableBytes());
			
			if (b.readableBytes() == (Packet.packetBufSize + Packet.packetHeaderSize)) {
				ctx.writeAndFlush(b);
			} else if (b.readableBytes() > (Packet.packetBufSize + Packet.packetHeaderSize)) {
				System.out.println("INVALID PACKET! DISCARDING!");
			}
		}
		
	}
	
	public void sendPacket(Channel c) {
		ByteBuf b = c.alloc().buffer(Packet.packetBufSize + Packet.packetHeaderSize, Packet.packetBufSize + Packet.packetHeaderSize);
		b.writeInt(PacketRegistry.GetOtherSide());
		b.writeInt(packetID);
		b.writeBytes(packet);
		if (b.readableBytes() == (Packet.packetBufSize + Packet.packetHeaderSize)) {
			c.writeAndFlush(b);
		} else if (b.readableBytes() > (Packet.packetBufSize + Packet.packetHeaderSize)) {
			System.out.println("INVALID PACKET! DISCARDING!");
		} else {
			b.writeZero(b.writableBytes());
			
			if (b.readableBytes() == (Packet.packetBufSize + Packet.packetHeaderSize)) {
				c.writeAndFlush(b);
			} else if (b.readableBytes() > (Packet.packetBufSize + Packet.packetHeaderSize)) {
				System.out.println("INVALID PACKET! DISCARDING!");
			}
		}
		
	}
}
