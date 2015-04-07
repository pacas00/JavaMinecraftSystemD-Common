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
