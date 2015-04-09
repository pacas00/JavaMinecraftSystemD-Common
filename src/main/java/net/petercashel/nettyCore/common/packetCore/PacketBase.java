/*******************************************************************************
 *    Copyright 2015 Peter Cashel (pacas00@petercashel.net)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
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
		ByteBuf b = ctx.alloc().buffer(
				Packet.packetBufSize + Packet.packetHeaderSize,
				Packet.packetBufSize + Packet.packetHeaderSize);
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
		ByteBuf b = c.alloc().buffer(
				Packet.packetBufSize + Packet.packetHeaderSize,
				Packet.packetBufSize + Packet.packetHeaderSize);
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
