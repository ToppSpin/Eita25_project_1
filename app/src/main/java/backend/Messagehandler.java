package backend;

import java.io.IOException;
import java.io.PrintWriter;

import models.Request;
import models.Response;
import models.Result;
import models.User;

public class Messagehandler {

    static Messagehandler singelton = null;

    DbService db;
    Logger log;

    private Messagehandler() {
        db = DbService.getInstance();
        try {
            log = new Logger(new PrintWriter("log.txt"));
        } catch (IOException e) {}
    }

    public static Messagehandler getInstance() {
        if (singelton == null) {
            singelton = new Messagehandler();
        } 
        return singelton;
    }

    public synchronized Response handle(Request msg, User user) {
        switch (msg.type) {
            case Create:
                return Create(user, msg);
            case Delete:
                return Delete(user, msg);
            case Write:
                return Write(user, msg);
            case List:
                return List(user);
            case Read:
                return Read(user, msg);
            default:
                return new Response("Some Error in the MessageHandler");
        }
    }

    private Response Create(User user, Request req) {
        Result result = db.Create(user, req.data.get("name"), req.data.get("nurse"));
        if (result.status() == true) {
            log.Log("User: " + user.name + " Created Record for: " + req.data.get("name") + " and with Nurse: " + req.data.get("nurse"));
        } else {
            log.Log("User: " + user.name + " tried to create record for patient: " + req.data.get("name") + " with nurse: " + req.data.get("nurse") + " Error was: " + result.text());
        }
        return new Response(result.text());
    }

    private Response Delete(User user, Request req) {
        Result result = db.Delete(user, req.data.get("name"), req.data.get("division"));
        if (result.status() == true) {
            log.Log("User: " + user.name + " Deleted record for patient: " + req.data.get("name") + " in division: " + req.data.get("division"));
        } else {
            log.Log("User: " + user.name + " tried to delete record for patient: " + req.data.get("name") + " in division " + req.data.get("division") + "Error was: " + result.text());
        }
        return new Response(result.text());
    }

    private Response Read(User user, Request req) {
        Result result = db.Read(user, req.data.get("name"), req.data.get("division"));
        if (result.status() == true) {
            log.Log("User: " + user.name + " Read record of patient: " + req.data.get("name") + " in division: " + req.data.get("division"));
        } else {
            log.Log("User: " + user.name + " tried to read record of patient: " + req.data.get("name") + " in division " + req.data.get("division") + "Error was: " + result.text());
        }
        return new Response(result.text());
    }

    private Response Write(User user, Request req) {
        Result result = db.Write(user, req.data.get("name"), req.data.get("division"), req.data.get("text"));
        if (result.status() == true) {
            log.Log("User: " + user.name + " Wrote to record for patient: " + req.data.get("name") + " in division: " + req.data.get("division"));
        } else {
            log.Log("User: " + user.name + " tried to write to record for patient: " + req.data.get("name") + " in division " + req.data.get("division") + "Error was: " + result.text());
        }
        return new Response(result.text());
    }

    private Response List(User user) {
        var records = 
            db
            .getAllRecords(user)
            .stream()
            .sorted()
            .map(rec -> rec.toString())
            .toList();

            return new Response(String.join("\n-- ", records));
    }
}
