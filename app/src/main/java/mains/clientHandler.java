package mains;

import java.io.BufferedReader;
import java.io.ObjectOutputStream;

import Communication.ResponseInputStream;
import client.StringToCommand;
import models.Request;
import models.Response;

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
                Response res = in.readObject();
                System.out.println(res);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
