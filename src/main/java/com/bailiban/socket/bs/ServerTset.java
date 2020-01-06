package com.bailiban.socket.bs;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerTset {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(80);
        System.out.println("等待客户端连接");
        while (true)
        {
            Socket socket = serverSocket.accept();
            new Server(socket).start();
        }

    }
}
