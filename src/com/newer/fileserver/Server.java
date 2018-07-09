package com.newer.fileserver;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 服务器
 * 
 * @author YYY
 *
 */
public class Server {

	// 端口号
	int port = 9000;

	// 套接字
	ServerSocket serverSocket;

	// 线程池
	ExecutorService pool;

	String filePath = "E:\\新建文件夹";

	HashMap<String, String> map = new HashMap<>();

	public void start() {
		try {
			serverSocket = new ServerSocket(port);

			// 建立一个可扩大的线程池
			pool = Executors.newCachedThreadPool();

			while (true) {
				Socket socket = serverSocket.accept();
				pool.execute(new Runnable() {

					public void run() {

						ByteArrayOutputStream data = new ByteArrayOutputStream();

						// 通过输出流获得文件的数据
						try (InputStream in = socket.getInputStream()) {
							byte[] buf = new byte[32];
							in.read(buf);
							String hash = new BigInteger(1, buf).toString(16);
							OutputStream out = socket.getOutputStream();
							byte[] bff = new byte[1];

							// 如果hashmap里包含这个hash值，就发送1，否则发送0
							if (map.containsKey(hash)) {
								bff[0] = 1;
								out.write(bff);
							} else {
								byte[] bff2 = new byte[1024 * 4];
								bff[0] = 0;
								out.write(bff);

								// 将文件下载到本地
								FileOutputStream out2 = new FileOutputStream(new File(filePath, hash));
								while (-1 != in.read(bff2)) {
									out2.write(bff2);
								}
							}
							map.put(hash, hash);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				});
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Server server = new Server();
		server.start();
	}
}
