package mains;

import java.io.BufferedReader;
import java.io.ObjectOutputStream;

import Communication.ResponseInputStream;
import client.StringToCommand;
import models.Request;

public class clientHandler {

    BufferedReader read;
    ObjectOutputStream out;
    ResponseInputStream in;

    public clientHandler(BufferedReader read, ObjectOutputStream out, ResponseInputStream in) {
        this.read = read;
        this.out = out;
        this.in = in;
    }

    public void run() {
        try {
            String msg;
            Request req;
            while (true) {

                System.out.print(">");
                msg = read.readLine();
                if (msg.equalsIgnoreCase("quit")) {
                    break;
                }
                try {
                    req = StringToCommand.stringToCommand(msg);
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                    continue;
                }
                out.writeObject(req);
                out.flush();
                System.out.println(in.readObject().toString());   
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
