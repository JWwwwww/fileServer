package com.newer.fileserver;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
							byte[] buf = new byte[1024 * 4];
							int size;
							while (-1 != (size = in.read(buf))) {
								data.write(buf, 0, size);
							}
						} catch (IOException e) {
							e.printStackTrace();
						}

						byte[] info = data.toByteArray();
						String file = "";

						// 计算文件的散列值
						try {
							byte[] hash = MessageDigest.getInstance("SHA-256").digest(info);
							file = new BigInteger(1, hash).toString(16);
						} catch (NoSuchAlgorithmException e) {
							e.printStackTrace();
						}

						// 新建文件，文件名为散列值
						try (FileOutputStream out = new FileOutputStream(new File(filePath, file))) {
							out.write(info);
						} catch (FileNotFoundException e) {
							e.printStackTrace();
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
