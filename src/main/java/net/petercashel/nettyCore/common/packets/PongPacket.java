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
package net.petercashel.nettyCore.common.packets;

import java.nio.charset.StandardCharsets;

import net.petercashel.nettyCore.client.clientCore;
import net.petercashel.nettyCore.common.PacketRegistry;
import net.petercashel.nettyCore.common.packetCore.IPacketBase;
import net.petercashel.nettyCore.common.packetCore.Packet;
import net.petercashel.nettyCore.common.packetCore.PacketBase;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;

public class PongPacket extends PacketBase implements IPacketBase {
	// SYN-ACK
	public PongPacket() { // Server -> Client
	}

	public PongPacket(String Tok) {
		tokenSalt = Tok;
	}

	public static int packetID = 1;
	public String tokenSalt = "";

	@Override
	public void pack() {
		this.setPacket(this.getBlankPacket());
		byte[] b = tokenSalt.getBytes(StandardCharsets.US_ASCII);
		this.packet.writeInt(tokenSalt.length());
		this.packet.writeBytes(b);
	}

	@Override
	public void unpack() {
		int i = this.packet.readInt();
		tokenSalt = new String(this.packet.readBytes(i).array(),
				StandardCharsets.US_ASCII);
	}

	@Override
	public void execute(ChannelHandlerContext ctx) {
		if (tokenSalt == null) {
			ctx.close();
			System.out.println("Client "
					+ ctx.channel().remoteAddress().toString()
					+ " failed to authenticate due to server error.");
			return;
		}
		if (tokenSalt.isEmpty()) {
			ctx.close();
			System.out.println("Client "
					+ ctx.channel().remoteAddress().toString()
					+ " failed to authenticate due to server error.");
			return;
		}
		if (tokenSalt.equalsIgnoreCase("")) {
			ctx.close();
			System.out.println("Client "
					+ ctx.channel().remoteAddress().toString()
					+ " failed to authenticate due to server error.");
			return;
		}
		System.out.println("Sending Salted Token");
		(PacketRegistry.pack(new PingPongPacket(clientCore
				.GeneratedSaltedToken(clientCore.token, tokenSalt))))
				.sendPacket(ctx);
	}

	@Override
	public int getPacketID() {
		// TODO Auto-generated method stub
		return packetID;
	}

	public ByteBuf getBlankPacket() {
		// TODO Auto-generated method stub
		ByteBuf b = Unpooled.buffer(Packet.packetBufSize).writeZero(
				Packet.packetBufSize);
		b.setIndex(0, 0);
		return b;
	}

	@Override
	public ByteBuf getPacket() {
		return this.packet;
	}

	@Override
	public void setPacket(ByteBuf buf) {
		this.packet = buf;

	}

}