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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;

import net.petercashel.jmsDd.auth.interfaces.IAuthDataSystem;
import net.petercashel.nettyCore.common.packetCore.IPacketBase;
import net.petercashel.nettyCore.common.packetCore.Packet;
import net.petercashel.nettyCore.common.packetCore.PacketBase;
import net.petercashel.nettyCore.server.ChannelUserHolder;
import net.petercashel.nettyCore.server.serverCore;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;

public class PingPongPacket extends PacketBase implements IPacketBase {
	// ACK
	public PingPongPacket() { // Client -> Server
	}

	public PingPongPacket(String TokSalt) { // Client -> Server
		tokenWithSalt = TokSalt;
	}

	public static int packetID = 2;
	public String tokenWithSalt = "";

	@Override
	public void pack() {
		this.setPacket(this.getBlankPacket());
		byte[] b = tokenWithSalt.getBytes(StandardCharsets.US_ASCII);
		this.packet.writeInt(tokenWithSalt.length());
		this.packet.writeBytes(b);
	}

	@Override
	public void unpack() {
		int i = this.packet.readInt();
		tokenWithSalt = new String(this.packet.readBytes(i).array(),
				StandardCharsets.US_ASCII);
	}

	@Override
	public void execute(ChannelHandlerContext ctx) {
		if (tokenWithSalt == null) {
			ctx.close();
			System.out.println("Client "
					+ ctx.channel().remoteAddress().toString()
					+ " failed to authenticate.");
			serverCore.clientConnectionMap.remove(ctx.channel()
					.remoteAddress());
			return;
		}
		if (tokenWithSalt.isEmpty()) {
			ctx.close();
			System.out.println("Client "
					+ ctx.channel().remoteAddress().toString()
					+ " failed to authenticate.");
			serverCore.clientConnectionMap.remove(ctx.channel()
					.remoteAddress());
			return;
		}
		if (tokenWithSalt.equalsIgnoreCase("")) {
			ctx.close();
			System.out.println("Client "
					+ ctx.channel().remoteAddress().toString()
					+ " failed to authenticate.");
			serverCore.clientConnectionMap.remove(ctx.channel()
					.remoteAddress());
			return;
		}
		IAuthDataSystem auth = null;
		try {
			Class cls = Class.forName("net.petercashel.jmsDd.auth.AuthSystem");
			Field f = cls.getField("backend");
			auth = (IAuthDataSystem) f.get(null);
		} catch (Exception e) {
			e.printStackTrace();
			ctx.close();
			System.out.println("Client "
					+ ctx.channel().remoteAddress().toString()
					+ " failed to authenticate.");
			serverCore.clientConnectionMap.remove(ctx.channel()
					.remoteAddress());
			return;
		}
		String Username = serverCore.AuthTmpUserMap.get(ctx.channel()
				.remoteAddress().toString());
		if (!(auth.GetSaltedToken(Username).equalsIgnoreCase(tokenWithSalt))) {
			ctx.close();
			System.out.println("Client "
					+ ctx.channel().remoteAddress().toString()
					+ " failed to authenticate.");
			serverCore.clientConnectionMap.remove(ctx.channel()
					.remoteAddress());
			return;
		}

		System.out.println("Client " + ctx.channel().remoteAddress().toString()
				+ " authenticated successfully.");
		//Quick cleanup and set username in mappings
		serverCore.AuthTmpUserMap.remove(ctx.channel()
				.remoteAddress().toString());
		ChannelUserHolder chUser = serverCore.clientConnectionMap.get(ctx.channel()
				.remoteAddress());
		chUser.user = Username;
		serverCore.clientConnectionMap.put(ctx.channel()
				.remoteAddress(), chUser);
		
		System.out.println("Sending history to "
				+ ctx.channel().remoteAddress().toString());
		try {
			Class cls = Class
					.forName("net.petercashel.jmsDd.command.commandServer");
			Method m = cls
					.getMethod("sendHistory", ChannelHandlerContext.class);
			m.invoke(null, ctx);
		} catch (Exception e) {
			e.printStackTrace();
		}

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