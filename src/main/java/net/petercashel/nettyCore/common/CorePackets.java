package net.petercashel.nettyCore.common;

import net.petercashel.nettyCore.common.packets.CMDInPacket;
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
		PacketRegistry.registerPacketWithID(PingPongPacketID, PingPongPacket.class);
		
		PacketRegistry.registerPacketWithID(IOOutPacket.packetID, IOOutPacket.class);
		PacketRegistry.registerPacketWithID(IOInPacket.packetID, IOInPacket.class);
		PacketRegistry.registerPacketWithID(CMDInPacket.packetID, CMDInPacket.class);
		
	}
}
