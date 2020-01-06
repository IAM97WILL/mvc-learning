package com.bailiban.socket.cs;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class ServerTest {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8080);
        System.out.println("服务器已开启，等待客户端连接。");
        while (true)
        {
            Socket socket = serverSocket.accept();
            System.out.println("客户端已连接。");
            new HttpServer(socket).start();
        }
    }
}
