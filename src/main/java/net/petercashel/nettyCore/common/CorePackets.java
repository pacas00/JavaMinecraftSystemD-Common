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
package net.petercashel.nettyCore.common;

import net.petercashel.nettyCore.common.packets.CMDInPacket;
import net.petercashel.nettyCore.common.packets.GetHistoryPacket;
import net.petercashel.nettyCore.common.packets.IOInPacket;
import net.petercashel.nettyCore.common.packets.IOOutPacket;
import net.petercashel.nettyCore.common.packets.PingPacket;
import net.petercashel.nettyCore.common.packets.PingPongPacket;
import net.petercashel.nettyCore.common.packets.PongPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class CorePackets {

	static short PingPacketID = 0;
	static short PongPacketID = 1;
	static short PingPongPacketID = 2;

	public static void registerCorePackets() {
		PacketRegistry.registerPacketWithID(PingPacketID, PingPacket.class);
		PacketRegistry.registerPacketWithID(PongPacketID, PongPacket.class);
		PacketRegistry.registerPacketWithID(PingPongPacketID,
				PingPongPacket.class);

		PacketRegistry.registerPacketWithID(IOOutPacket.packetID,
				IOOutPacket.class);
		PacketRegistry.registerPacketWithID(IOInPacket.packetID,
				IOInPacket.class);

		PacketRegistry.registerPacketWithID(CMDInPacket.packetID,
				CMDInPacket.class);

		PacketRegistry.registerPacketWithID(GetHistoryPacket.packetID,
				GetHistoryPacket.class);

	}
}
