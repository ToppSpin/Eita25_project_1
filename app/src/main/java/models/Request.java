package models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Request implements Serializable {

    public RequestType type;
    public Map<String,String> data;
    
    public Request(RequestType type) {
        this.type = type;
        data = new HashMap<>();
    }

    @Override
    public String toString() {
        return "this is a request saying {" + type + "}";
    }

    public enum RequestType {
    Create,
    Delete,
    Read,
    Write,
    List
}
}
