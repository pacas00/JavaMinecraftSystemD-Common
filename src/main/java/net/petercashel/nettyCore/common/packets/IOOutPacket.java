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

import java.io.PrintStream;
import java.lang.reflect.Field;
import java.nio.charset.Charset;


//import net.petercashel.jmsDc.command.commandClient;
import net.petercashel.nettyCore.common.PacketRegistry;
import net.petercashel.nettyCore.common.packetCore.IPacketBase;
import net.petercashel.nettyCore.common.packetCore.Packet;
import net.petercashel.nettyCore.common.packetCore.PacketBase;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;

public class IOOutPacket extends PacketBase implements IPacketBase {
	public IOOutPacket(){
	}
	public IOOutPacket(int blen, byte[] b) {
		this.length = blen;
		this.b = b;
	}

	public static int packetID = 10;
	public  int length = 0;
	public  byte[] b;
	
	@Override
	public void pack() {
		this.setPacket(this.getBlankPacket());
		this.packet.writeInt(length);
		this.packet.writeBytes(b);
		
	}

	@Override
	public void unpack() {
		length = this.packet.readInt();
		b = this.packet.readBytes(length).array();
		
	}

	@Override
	public void execute(ChannelHandlerContext ctx) {
		try {
			String s = new String(b, Charset.forName("ASCII"));
			
			Class<?> clazz = Class.forName("net.petercashel.jmsDc.command.commandClient");
			Field f = clazz.getField("out");
			PrintStream ps = (PrintStream) null;
			ps = (PrintStream) f.get(ps);
			ps.println(s);
		} catch (Exception e) {
			// TODO Auto-generated catch block
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