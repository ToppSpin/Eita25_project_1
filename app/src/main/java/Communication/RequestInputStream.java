package Communication;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

import models.Request;

public class RequestInputStream {

    private ObjectInputStream stream;

    public RequestInputStream(InputStream stream) throws IOException {
        this.stream = new ObjectInputStream(stream);
    }

    public Request readObject() {
        try {
        return (Request) stream.readObject();
        } catch (Exception e)
        {
            return null;
        }
    }

    public void close() throws IOException {
        stream.close();
    }
}
