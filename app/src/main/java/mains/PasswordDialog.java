package mains;

import javax.swing.*;
import java.awt.*;

public class PasswordDialog {
    public static char[] ShowDialog() throws Exception{
        JPasswordField passwordField = new JPasswordField(15);

        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.add(passwordField, BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(
                null,
                panel,
                "Enter Password",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            char[] password = passwordField.getPassword();
            System.out.println("Entered password: " + new String(password));
            return password;
        } else {
            System.out.println("Cancelled");
            throw new Exception();
        }
    }
}