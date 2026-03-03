package backend;

import java.io.PrintWriter;
import java.time.LocalDateTime;

public class Logger {

    private PrintWriter pw;

    public Logger(PrintWriter pw) {
        this.pw = pw;
    }

    public void Log(String text) {
        pw.append(LocalDateTime.now() +  " : " + text + "\n");
        pw.flush();
    }
}
