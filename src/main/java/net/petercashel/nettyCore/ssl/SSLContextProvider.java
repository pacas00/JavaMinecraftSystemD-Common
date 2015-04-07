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
package net.petercashel.nettyCore.ssl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.util.Random;
import java.util.logging.Logger;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManagerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.base64.Base64;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.util.internal.logging.Log4JLoggerFactory;


public class SSLContextProvider {

	public static boolean useExternalSSL = false;
	public static String pathToSSLCert = "";
	public static String SSLCertSecret = "secret";

	private static byte[] pkcs12Base64 = null; //
	private static SSLContext sslContext = null;
	public static boolean selfSigned = false;

	public SSLContextProvider() {
		SetupSSL();
	}

	public static void SetupSSL() {

		if (useExternalSSL) {
			try {
				try {
					pkcs12Base64 = Files.readAllBytes(FileSystems.getDefault().getPath(pathToSSLCert));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (NullPointerException e) {
				selfSigned = true;
			}
		} else {
			InputStream in = SSLContextProvider.class.getClass().getResourceAsStream("/net/petercashel/nettyCore/ssl/SSLCERT.p12");
			try {
				int buffersize = 0;
				try {
					buffersize = in.available();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					buffersize = 16384;
				}
				byte[] buffer = new byte[buffersize];
				try {
					if ( in.read(buffer) == -1 ) {
					}        
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally { 
					try {
						if ( in != null ) 
							in.close();
					} catch ( IOException e) {
					}

				}
				pkcs12Base64 = buffer;
			} catch (NullPointerException e) {
				selfSigned = true;
			}
		}
	}
	public static SslContext getSelfClient() {
		SslContext sslCtx = null;
		try {
			sslCtx = SslContext.newClientContext(InsecureTrustManagerFactory.INSTANCE);
		} catch (SSLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sslCtx;
	}


	public static SslContext getSelfServer() {
		SelfSignedCertificate ssc = null;
		try {
			ssc = new SelfSignedCertificate();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SslContext sslCtx = null;
		try {
			sslCtx = SslContext.newServerContext(ssc.certificate(), ssc.privateKey());
		} catch (SSLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sslCtx;
	}

	public static SSLContext get() {
		if(sslContext==null) {
			synchronized (SSLContextProvider.class) {
				if(sslContext==null) {
					try {
						sslContext = SSLContext.getInstance("TLSv1.2");
						KeyStore ks = KeyStore.getInstance("PKCS12");
						ks.load(new ByteArrayInputStream((pkcs12Base64)), SSLCertSecret.toCharArray());
						KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
						kmf.init(ks, SSLCertSecret.toCharArray());

						TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("PKIX");
						trustManagerFactory.init(ks);

						//Random r = new Random();
						//int iseed = r.nextInt();
						//while (iseed < 0) iseed = r.nextInt();
						//byte[] seed = r.generateSeed(iseed);
						SecureRandom sr = new SecureRandom();
						sslContext.init(kmf.getKeyManagers(), trustManagerFactory.getTrustManagers(), sr);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		return sslContext;
	}
}