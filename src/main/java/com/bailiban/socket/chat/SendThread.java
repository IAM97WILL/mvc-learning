package com.bailiban.socket.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SendThread  extends Thread{

    private String fromName;
    private Socket socket;

    public SendThread(String fromName, Socket socket) {
        this.fromName = fromName;
        this.socket = socket;
    }

    @Override
    public void run() {
        try (BufferedReader sin=new BufferedReader(new InputStreamReader(System.in));
             PrintWriter out=new PrintWriter(socket.getOutputStream(),true)){
            while (true)
            {
                String line=sin.readLine();
                out.println(line);
                if (line==null||line.equals("bye"))
                {
                    break;
                }
            }

        } catch (IOException e) {
//            e.printStackTrace();
        }

    }
}
