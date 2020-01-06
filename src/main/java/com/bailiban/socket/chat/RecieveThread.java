package com.bailiban.socket.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class RecieveThread  extends Thread{
    private String fromname;
    private Socket socket;

    public RecieveThread(String fromname, Socket socket) {
        this.fromname = fromname;
        this.socket = socket;
    }

    @Override
    public void run() {
        try(BufferedReader in =new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            while (true)
            {
                String line=in.readLine();
                System.out.println(fromname+":"+line);
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
