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
package net.petercashel.nettyCore.client;

import java.net.InetSocketAddress;
import java.nio.channels.Channels;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLEngine;

import net.petercashel.commonlib.threading.threadManager;
import net.petercashel.nettyCore.common.PacketRegistry;
import net.petercashel.nettyCore.common.exceptions.ConnectionShuttingDown;
import net.petercashel.nettyCore.common.packets.PingPacket;
import net.petercashel.nettyCore.ssl.SSLContextProvider;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.handler.timeout.ReadTimeoutHandler;

public class clientCore {
	public static boolean UseSSL = true;
	public static boolean DoAuth = true;
	public static String username = "";
	public static String token = "";
	static Channel connection;
	static final int side = 1;
	static String _host = "";
	static int _port = 0;
	public static boolean shuttingdown = false;
	public static boolean connClosed = true;
	private static NioEventLoopGroup group;

	public static void reinitaliseConnection() throws Exception {
		if (_port == 0)
			throw new Exception("Critical Error. Connection never initialized.");
		initializeConnection(_host, _port);
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
	public static void initializeConnection(final String addr, final int port)
			throws Exception {
		_host = addr;
		_port = port;
		PacketRegistry.setupRegistry();
		PacketRegistry.Side = side;
		if (UseSSL)
			SSLContextProvider.SetupSSL();

		group = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			b.group(group).channel(NioSocketChannel.class)
					.handler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel ch)
								throws Exception {
							ChannelPipeline p = ch.pipeline();
							p.addLast("readTimeoutHandler",
									new ReadTimeoutHandler(300));
							if (UseSSL && !SSLContextProvider.selfSigned)
								p.addLast("ssl",
										getClientSSLHandler(addr, port));
							if (UseSSL && SSLContextProvider.selfSigned)
								p.addLast(
										"ssl",
										SSLContextProvider.getSelfClient()
												.newHandler(ch.alloc(), addr,
														port));
							p.addLast("InboundOutboundClientHandler",
									new ClientConnectionHander());
						}
					});

			// Make the connection attempt.
			ChannelFuture f = b.connect(addr, port).sync();
			f.awaitUninterruptibly(2000, TimeUnit.MILLISECONDS);

			if (!f.isSuccess())
				throw new RuntimeException("Failed to connect");
			// if a wait option was selected and the connect did not fail,
			// the Date can now be sent.
			System.out.println("Client Core Connected!");
			connection = f.channel();
			connClosed = false;
			// Initiate the Ping->Pong->PingPong Packet test.
			PacketRegistry.pack(new PingPacket()).sendPacket(
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

	public static SslHandler getClientSSLHandler(final String addr,
			final int port) {
		final SSLEngine sslEngine = SSLContextProvider.get().createSSLEngine(
				addr, port);
		sslEngine.setUseClientMode(true);
		final SslHandler sslHandler = new SslHandler(sslEngine);
		return sslHandler;
	}

	public static String GeneratedSaltedToken(String TOKEN, String TokenSalt) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-512");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		String text = TOKEN + TokenSalt;

		md.update(text.getBytes(StandardCharsets.UTF_8));
		byte[] hash = md.digest();
		StringBuffer hexString = new StringBuffer();

		for (int i = 0; i < hash.length; i++) {
			String hex = Integer.toHexString(0xff & hash[i]);
			if (hex.length() == 1)
				hexString.append('0');
			hexString.append(hex);
		}

		return hexString.toString();
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
