package models;

import java.io.Serializable;

public class Response implements Serializable {

    public String text;

    public Response(String text) {
        this.text = ""+text;
    }

    @Override
    public String toString() {
        return text;
    }
}