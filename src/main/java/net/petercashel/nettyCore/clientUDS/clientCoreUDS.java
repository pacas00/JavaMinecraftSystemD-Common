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
package net.petercashel.nettyCore.clientUDS;

import java.io.File;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import net.petercashel.nettyCore.common.PacketRegistry;
import net.petercashel.nettyCore.common.exceptions.ConnectionShuttingDown;
import net.petercashel.nettyCore.common.packets.GetHistoryPacket;
import net.petercashel.nettyCore.common.packets.PingPacket;
import io.netty.bootstrap.AbstractBootstrap;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollDomainSocketChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.unix.DomainSocketAddress;

public class clientCoreUDS {
	static Channel connection;
	static final int side = 1;
	public static boolean shuttingdown = false;
	public static boolean connClosed = true;
	private static EpollEventLoopGroup group;

	public static void reinitaliseConnection() throws Exception {
	}

	/**
	 * Initializes a Client Connection
	 * 
	 * @param addr
	 *            - String address to connect to
	 * @param port
	 *            - int Port number to connect to
	 * @throws Exception
	 */

	public static void initializeConnection(Path socket) throws Exception {
		initializeConnection(socket.toFile());
	}

	public static void initializeConnection(File socket) throws Exception {
		PacketRegistry.setupRegistry();
		PacketRegistry.Side = side;

		group = new EpollEventLoopGroup();
		try {
			Bootstrap b = new BootstrapFactory<Bootstrap>() {
				@Override
				public Bootstrap newInstance() {
					return new Bootstrap()
							.group(group)
							.channel(EpollDomainSocketChannel.class)
							.handler(
									new ChannelInitializer<EpollDomainSocketChannel>() {
										@Override
										protected void initChannel(
												EpollDomainSocketChannel ch)
												throws Exception {
											ChannelPipeline p = ch.pipeline();
											p.addLast(
													"InboundOutboundClientHandler",
													new ClientUDSConnectionHander());
										}
									});
				}
			}.newInstance();

			// Make the connection attempt.
			ChannelFuture f = b.connect(newSocketAddress(socket)).sync();
			f.awaitUninterruptibly(2000, TimeUnit.MILLISECONDS);

			if (!f.isSuccess())
				throw new RuntimeException("Failed to connect");
			// if a wait option was selected and the connect did not fail,
			// the Date can now be sent.
			System.out.println("Client UDS Connected!");
			connection = f.channel();
			connClosed = false;

			// Send GetHistoryPacket
			PacketRegistry.pack(new GetHistoryPacket()).sendPacket(
					connection.pipeline().context(
							"InboundOutboundClientHandler"));

			// Wait until the connection is closed.
			f.channel().closeFuture().sync();
		} finally {
			group.shutdownGracefully();
			System.out.println("Connection Closed");
			connClosed = true;
		}
	}

	public static Channel getChannel() throws ConnectionShuttingDown,
			NullPointerException {
		if (shuttingdown)
			throw new ConnectionShuttingDown();
		return connection;
	}

	public interface BootstrapFactory<CB extends AbstractBootstrap<?, ?>> {
		CB newInstance();
	}

	public static DomainSocketAddress newSocketAddress(File socket) {
		return new DomainSocketAddress(socket);
	}

	public static void shutdown() throws InterruptedException,
			ConnectionShuttingDown {
		try {
			getChannel().close().await(20, TimeUnit.SECONDS);
			group.shutdownGracefully().await(30, TimeUnit.SECONDS);
			PacketRegistry.shutdown();
		} catch (NullPointerException e) {
		}
	}
}
