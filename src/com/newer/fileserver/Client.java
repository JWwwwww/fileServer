package com.newer.fileserver;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

/**
 * 客户端
 * 
 * @author YYY
 *
 */
public class Client {

	// 文件输入流
	FileInputStream fileInputStream;

	// 套接字
	Socket socket;

	OutputStream out;

	// 服务器地址及端口号
	String serverAddress = "";
	int serverPort = 9000;

	public void start() {
		// 输入需要上传的文件
		Scanner sc = new Scanner(System.in);
		System.out.println("请输入文件：");
		String file = sc.next();
		sc.close();

		try {
			// 实例化套接字
			socket = new Socket(serverAddress, serverPort);

			// 通过套接字建立客户端向服务器的输出
			out = socket.getOutputStream();

			// 定义一个字节数组
			byte[] buf = new byte[1024 * 4];
			int size;
			fileInputStream = new FileInputStream(file);
			while (-1 != (size = fileInputStream.read(buf))) {
				out.write(buf);
				out.flush();
			}
			System.out.println("上传成功");

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fileInputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		Client client = new Client();
		client.start();
	}
}
