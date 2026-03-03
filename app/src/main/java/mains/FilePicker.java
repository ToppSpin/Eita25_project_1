package mains;

import javax.swing.*;
import java.io.File;

public class FilePicker {

    public static String pickFile() {
        JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));

        int result = fileChooser.showOpenDialog(null); // null = center on screen

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            return selectedFile.getAbsolutePath();
        }

        return null; // user cancelled
    }
}