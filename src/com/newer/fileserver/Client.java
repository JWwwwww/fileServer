package com.newer.fileserver;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
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

			// 新建一个长度可变的字节数组
			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			// 定义一个字节数组
			byte[] buf = new byte[1024 * 4];
			int size;
			fileInputStream = new FileInputStream(file);
			while (-1 != (size = fileInputStream.read(buf))) {
				baos.write(buf, 0, size);
			}
			byte[] b = baos.toByteArray();
			
			// 计算文件的散列值
			try {
				byte[] hash = MessageDigest.getInstance("SHA-256").digest(b);
				out.write(hash);
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}

			// 用一个长度为1的字节数组来接收信息
			byte[] bt = new byte[1];
			InputStream in = socket.getInputStream();
			in.read(bt);
			if (bt[0] == 1) {
				System.err.println("不能重复上传!");
			} else if (bt[0] == 0) {
				out.write(b);
				System.out.println("上传成功！");
			}
			out.close();
			in.close();
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
