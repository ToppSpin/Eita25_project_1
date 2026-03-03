package Communication;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

import models.Response;

public class ResponseInputStream {

    private ObjectInputStream stream;

    public ResponseInputStream(InputStream stream) throws IOException {
        this.stream = new ObjectInputStream(stream);
    }

    public Response readObject() throws IOException,ClassNotFoundException{
        return (Response)stream.readObject();
    }

    public void close() throws IOException {
        stream.close();
    }
}
