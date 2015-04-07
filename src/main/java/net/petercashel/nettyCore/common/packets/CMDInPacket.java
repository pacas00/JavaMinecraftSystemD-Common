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
package net.petercashel.nettyCore.common.packets;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;





//import net.petercashel.jmsDd.command.commandServer;
import net.petercashel.nettyCore.common.PacketRegistry;
import net.petercashel.nettyCore.common.packetCore.IPacketBase;
import net.petercashel.nettyCore.common.packetCore.Packet;
import net.petercashel.nettyCore.common.packetCore.PacketBase;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;

public class CMDInPacket extends PacketBase implements IPacketBase {
	public CMDInPacket() {
	}
	
	public CMDInPacket(String s) {
		this.s = s;
	}

	public static int packetID = 20;
	public String s;
	
	@Override
	public void pack() {
		this.setPacket(this.getBlankPacket());
		this.packet.writeInt(s.length());
		try {
			this.packet.writeInt(s.getBytes("ASCII").length);
		} catch (UnsupportedEncodingException e) {
			this.packet.writeInt(s.getBytes().length);
		}
		try {
			this.packet.writeBytes(s.getBytes("ASCII"));
		} catch (UnsupportedEncodingException e) {
			this.packet.writeBytes(s.getBytes());
		}
		
	}

	@Override
	public void unpack() {
		int strlength = this.packet.readInt();
		int length = this.packet.readInt();
		s = new String(this.packet.readBytes(length).array());		
	}

	@Override
	public void execute(ChannelHandlerContext ctx) {
		try {
			Class<?> clazz = Class.forName("net.petercashel.jmsDd.command.commandServer");
			Method m = clazz.getMethod("processCommand", String.class);
			m.invoke(null, s);
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//commandServer.processCommand(s);		
	}

	@Override
	public int getPacketID() {
		// TODO Auto-generated method stub
		return packetID;
	}

	public ByteBuf getBlankPacket() {
		// TODO Auto-generated method stub
		ByteBuf b = Unpooled.buffer(Packet.packetBufSize).writeZero(Packet.packetBufSize);
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