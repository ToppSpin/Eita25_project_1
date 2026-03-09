package client;

import models.Request;
import models.Request.RequestType;

public class StringToCommand {

    public static Request stringToCommand(String input) throws IllegalArgumentException {
        try {
            input = input.trim();
            String[] args = input.split("\\|");
            Request req;
            switch (args[0].toLowerCase()) {
                case "create":
                    req = new Request(RequestType.Create);
                    req.data.put("name", args[1]);
                    req.data.put("nurse", args[2]);
                    return req;
                case "delete":
                    req = new Request(RequestType.Delete);
                    req.data.put("name", args[1]);
                    req.data.put("division", args[2]);
                    return req;
                case "read":
                    req = new Request(RequestType.Read);
                    req.data.put("name", args[1]);
                    req.data.put("division", args[2]);
                    return req;
                case "write":
                    req = new Request(RequestType.Write);
                    req.data.put("name", args[1]);
                    req.data.put("division", args[2]);
                    req.data.put("text", args[3]);
                    return req;
                case "list":
                    req = new Request(RequestType.List);
                    return req;
                case "help":
                    throw new IllegalArgumentException(helpMessage());
                default:
                    throw new IllegalArgumentException("Invalid input: type 'help' for info on how to use commands");
            }
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid input: type 'help' for info on how to use commands");
        }
    }

    private static String helpMessage() {
        return "possible commands are:\n create|patient name|nursename\n delete|patient name|division\n read|patient name|division\n write|patient name|division|'text to write'\n list\n";
    }
}
