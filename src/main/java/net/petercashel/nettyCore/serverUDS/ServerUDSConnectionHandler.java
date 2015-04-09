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
package net.petercashel.nettyCore.serverUDS;

import net.petercashel.nettyCore.common.PacketRegistry;
import net.petercashel.nettyCore.common.packetCore.Packet;
import net.petercashel.nettyCore.server.serverCore;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.handler.timeout.ReadTimeoutException;


public class ServerUDSConnectionHandler extends ChannelHandlerAdapter {
	
	private ByteBuf buf;
	
	@Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        buf = ctx.alloc().buffer(Packet.packetBufSize + Packet.packetHeaderSize); // (1)
    }

	@Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
    	try {
    	if (!(serverCoreUDS.clientConnectionMap.containsKey(ctx.channel()))) {
    		serverCoreUDS.clientConnectionMap.put(ctx.channel(), ctx.channel());
    	}
    	} catch (NullPointerException e) {
    		//Delibrately null on shutdown.
    		ctx.close();
    		ctx.executor().shutdownGracefully();
    	}
    }
    
	@Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        buf.release(); // (1)
        buf = null;
    }
    
 	@Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		ByteBuf m = (ByteBuf) msg;
        buf.writeBytes(m); // (2)
        m.release();

        if (buf.readableBytes() >= (Packet.packetBufSize + Packet.packetHeaderSize)) { // (3)
        	int side = buf.readInt();
        	Packet p = new Packet(buf.readInt(), buf.readBytes(Packet.packetBufSize));
            PacketRegistry.unpackExecute(p, ctx); 
        }
    }

	@Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof ReadTimeoutException) {
            serverCoreUDS.clientConnectionMap.remove(ctx.channel());
        } else {
            super.exceptionCaught(ctx, cause);
        }
    }

}
