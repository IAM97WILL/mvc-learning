package com.bailiban.socket.cs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class HttpClient extends Thread{
    private Socket socket;

    public HttpClient(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try(
                BufferedReader in=new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedReader sin=new BufferedReader(new InputStreamReader(System.in));
                PrintWriter out=new PrintWriter(socket.getOutputStream(),true)
                ) {
            while (true)
            {
                String line=sin.readLine();
                if (line.equals("bye")) {
                    break;
                }
                out.println(line);
                String rec=in.readLine();
                System.out.println(rec);
            }



        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
