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
import java.net.SocketAddress;
import java.nio.file.Path;
import java.util.HashMap;

import net.petercashel.nettyCore.common.PacketRegistry;
import io.netty.bootstrap.AbstractBootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.epoll.EpollDomainSocketChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerDomainSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.unix.DomainSocketAddress;
import io.netty.handler.timeout.ReadTimeoutHandler;

public class serverCoreUDS {

	static final int side = 0;
	private static EpollEventLoopGroup bossGroup;
	private static EpollEventLoopGroup workerGroup;
	public static HashMap<Channel, Channel> clientConnectionMap;
	public static boolean alive = false;
	public static Channel c = null;

	// http://netty.io/wiki/user-guide-for-4.x.html
	/**
	 * Initializes the Server listerning socket
	 *
	 * @param port
	 *            - Int port to bind to
	 * @throws Exception
	 */
	public static void initializeServer(Path socket) throws Exception {
		initializeServer(socket.toFile());
	}

	public static void initializeServer(File socket) throws Exception {
		clientConnectionMap = new HashMap<Channel, Channel>();
		PacketRegistry.setupRegistry();
		PacketRegistry.Side = side;
		alive = true;
		bossGroup = new EpollEventLoopGroup(); // (1)
		workerGroup = new EpollEventLoopGroup();
		try {
			ServerBootstrap b = new BootstrapFactory<ServerBootstrap>() {
				@Override
				public ServerBootstrap newInstance() {
					return new ServerBootstrap()
							.group(bossGroup, workerGroup)
							.channel(EpollServerDomainSocketChannel.class)
							.childHandler(
									new ChannelInitializer<EpollDomainSocketChannel>() {
										@Override
										public void initChannel(
												EpollDomainSocketChannel ch)
												throws Exception {
											ChannelPipeline p = ch.pipeline();
											p.addLast("readTimeoutHandler",
													new ReadTimeoutHandler(300));
											p.addLast(
													"InboundOutboundServerHandler",
													new ServerUDSConnectionHandler());
										}
									});
				}
			}.newInstance();

			// Bind and start to accept incoming connections.
			ChannelFuture f = b.bind(newSocketAddress(socket)).sync(); // (7)
			System.out.println("Server UDS Initalised!");
			// Wait until the server socket is closed.
			// In this example, this does not happen, but you can do that to
			// gracefully
			// shut down your server.
			c = f.channel();
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
		socket.delete();
		DomainSocketAddress sock = new DomainSocketAddress(socket);
		System.out.println(sock.path());
		return sock;
	}

	public static void shutdown() {
		try {
			for (Channel c : clientConnectionMap.values()) {
				try {
					c.close().sync();
				} catch (InterruptedException e) {
				}
				c = null;
			}
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
			PacketRegistry.shutdown();
		} catch (NullPointerException e) {
		}
	}
}
