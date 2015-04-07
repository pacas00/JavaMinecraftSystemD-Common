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
package net.petercashel.nettyCore.server;


import java.net.SocketAddress;
import java.util.HashMap;

import javax.net.ssl.SSLEngine;

import net.petercashel.nettyCore.common.PacketRegistry;
import net.petercashel.nettyCore.common.packets.PongPacket;
import net.petercashel.nettyCore.ssl.SSLContextProvider;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.handler.timeout.ReadTimeoutHandler;

public class serverCore {

	public static boolean UseSSL = true;
	static final int side = 0;
	public static HashMap<SocketAddress,Channel> clientConnectionMap;
	private static NioEventLoopGroup bossGroup;
	private static NioEventLoopGroup workerGroup;

	// http://netty.io/wiki/user-guide-for-4.x.html
	/**
	 * Initializes the Server listerning socket 
	 *
	 * @param port - Int port to bind to
	 * @throws Exception
	 */
	public static void initializeServer(int port) throws Exception {
		clientConnectionMap = new HashMap<SocketAddress,Channel>();
		PacketRegistry.setupRegistry();
		PacketRegistry.Side = side;
		if (UseSSL) SSLContextProvider.SetupSSL();

		bossGroup = new NioEventLoopGroup(); // (1)
		workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap(); // (2)
			b.group(bossGroup, workerGroup)
			.channel(NioServerSocketChannel.class) // (3)
			.childHandler(new ChannelInitializer<SocketChannel>() { // (4)
				@Override
				public void initChannel(SocketChannel ch) throws Exception {
					ChannelPipeline p = ch.pipeline();
					p.addLast("readTimeoutHandler", new ReadTimeoutHandler(300));
					if (UseSSL && !SSLContextProvider.selfSigned) p.addLast("ssl", getSSLHandler());
					if (UseSSL && SSLContextProvider.selfSigned) p.addLast("ssl", SSLContextProvider.getSelfServer().newHandler(ch.alloc()));
					p.addLast("InboundOutboundServerHandler", new ServerConnectionHandler());
				}
			})
			.option(ChannelOption.SO_BACKLOG, 128)          // (5)
			.childOption(ChannelOption.TCP_NODELAY, true); // (6)


			// Bind and start to accept incoming connections.
			ChannelFuture f = b.bind("0.0.0.0", port).sync(); // (7)
			System.out.println("Server Core Initalised!");
			// Wait until the server socket is closed.
			// In this example, this does not happen, but you can do that to gracefully
			// shut down your server.
			f.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}

	}

	public static SslHandler getSSLHandler() {
		final SSLEngine sslEngine = SSLContextProvider.get().createSSLEngine();
		sslEngine.setUseClientMode(false);
		sslEngine.setNeedClientAuth(false);
		final SslHandler sslHandler = new SslHandler(sslEngine);

		return sslHandler;
	}

	public static void shutdown() {
		for (Channel c : clientConnectionMap.values()) {
			try {
				c.close().sync();
			} catch (InterruptedException e) {
			}
			c = null;
		}
		workerGroup.shutdownGracefully();
		bossGroup.shutdownGracefully();
		clientConnectionMap.clear();
		clientConnectionMap = null;
		PacketRegistry.shutdown();
	}
}
