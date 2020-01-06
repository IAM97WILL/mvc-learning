package com.bailiban.socket.chat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8080);
        System.out.println("服务端开启，等待客户端连接。。");
        Socket socket = serverSocket.accept();
        System.out.println("客户端已连接。");

        new RecieveThread("client",socket).start();

        new SendThread("server",socket).start();
    }
}
