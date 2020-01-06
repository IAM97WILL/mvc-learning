package com.bailiban.socket.hello;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientTest {
    public static void main(String[] args) throws IOException {
        //建立客户端连接
        Socket socket = new Socket("localhost", 8080);
        System.out.println("客户端已连接服务器");
        try(BufferedReader sin=new BufferedReader(new InputStreamReader(System.in));
            BufferedReader in =new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out=new PrintWriter(socket.getOutputStream())
        ) {
            //输入向服务端发送的信息
            String line = sin.readLine();
            //向服务器端发送输入信息
            out.println(line);
            out.flush();
            //获取服务端信息
            System.out.println("服务器端："+in.readLine());
        }
    }
}
