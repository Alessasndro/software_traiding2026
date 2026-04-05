package org.example.ui;

import org.example.service.UserService;
import javax.swing.*;

public class AuthUI {
    private final UserService userService = new UserService();

    public String avviaMenuIniziale() {
        while (true) {
            String[] opzioni = {"Login", "Sign In", "Esci"};
            int scelta = JOptionPane.showOptionDialog(null,
                    "TRADING WALLET 2026\nScegli un'opzione:", "Benvenuto",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, opzioni, opzioni[0]);

            if (scelta == 2 || scelta == -1) System.exit(0);

            if (scelta == 1) {
                gestisciRegistrazione();
            } else if (scelta == 0) {
                String email = gestisciLogin();
                if (email != null) return email;
            }
        }
    }

    private void gestisciRegistrazione() {
        JTextField nomeF = new JTextField();
        JTextField cognomeF = new JTextField();
        JTextField emailF = new JTextField();
        JPasswordField pwdF = new JPasswordField();
        Object[] msg = {"Nome:", nomeF, "Cognome:", cognomeF, "Email:", emailF, "Password:", pwdF};

        if (JOptionPane.showConfirmDialog(null, msg, "Registrazione", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            if (userService.signIn(nomeF.getText(), cognomeF.getText(), emailF.getText(), new String(pwdF.getPassword()))) {
                JOptionPane.showMessageDialog(null, "Account creato!");
            } else {
                JOptionPane.showMessageDialog(null, "Errore registrazione.");
            }
        }
    }

    private String gestisciLogin() {
        JTextField emailF = new JTextField();
        JPasswordField pwdF = new JPasswordField();
        Object[] msg = {"Email:", emailF, "Password:", pwdF};

        if (JOptionPane.showConfirmDialog(null, msg, "Login", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            String email = emailF.getText();
            if (userService.login(email, new String(pwdF.getPassword()))) {
                return email;
            } else {
                JOptionPane.showMessageDialog(null, "Credenziali errate.");
            }
        }
        return null;
    }
}