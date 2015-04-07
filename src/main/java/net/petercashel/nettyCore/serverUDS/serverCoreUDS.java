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


import java.io.File;
import java.nio.file.Path;
import net.petercashel.nettyCore.common.PacketRegistry;
import io.netty.bootstrap.AbstractBootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerDomainSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.unix.DomainSocketAddress;

public class serverCoreUDS {

	static final int side = 0;
	private static EpollEventLoopGroup bossGroup;
	private static EpollEventLoopGroup workerGroup;

	// http://netty.io/wiki/user-guide-for-4.x.html
	/**
	 * Initializes the Server listerning socket 
	 *
	 * @param port - Int port to bind to
	 * @throws Exception
	 */
	public static void initializeServer(Path socket) throws Exception {
		initializeServer(socket.toFile());
	}
	
	public static void initializeServer(File socket) throws Exception {
		PacketRegistry.setupRegistry();
		PacketRegistry.Side = side;

		bossGroup = new EpollEventLoopGroup(); // (1)
		workerGroup = new EpollEventLoopGroup();
		try {
			ServerBootstrap b = new BootstrapFactory<ServerBootstrap>() {
				@Override
				public ServerBootstrap newInstance() {
					return new ServerBootstrap().group(bossGroup, workerGroup)
							.channel(EpollServerDomainSocketChannel.class)
							.childHandler(new ChannelInitializer<SocketChannel>() { 
								@Override
								public void initChannel(SocketChannel ch) throws Exception {
									ChannelPipeline p = ch.pipeline();
									p.addLast("InboundOutboundServerHandler", new ServerUDSConnectionHandler());
								}
							});
				}
			}.newInstance();
			
			b.option(ChannelOption.SO_BACKLOG, 128); // (6)

			// Bind and start to accept incoming connections.
			ChannelFuture f = b.bind(newSocketAddress(socket)).sync(); // (7)
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

	public interface BootstrapFactory<CB extends AbstractBootstrap<?, ?>> {
		CB newInstance();
	}

	public static DomainSocketAddress newSocketAddress(File socket) {
			return new DomainSocketAddress(socket);
	}

	public static void shutdown() {
		workerGroup.shutdownGracefully();
		bossGroup.shutdownGracefully();
		PacketRegistry.shutdown();
	}
}
