package com.bailiban.socket.chat;

import java.io.IOException;
import java.net.Socket;

public class Client {
    public static void main(String[] args) throws IOException {

        Socket socket = new Socket("localhost", 8080);
        System.out.println("已连接上服务端。");
        new SendThread("client",socket).start();
        new RecieveThread("server",socket).start();
    }
}
