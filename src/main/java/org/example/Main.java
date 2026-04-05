package org.example;

import org.example.service.CandelaService;
import org.example.service.UserService;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javax.swing.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Main {
    private static final UserService userService = new UserService();
    private static final CandelaService candelaService = new CandelaService();
    private static final List<String> cripto = Arrays.asList("BTC", "ETH", "SOL");

    public static void main(String[] args) {
        String[] opzioni = {"Login", "Sign In", "Esci"};
        int scelta = JOptionPane.showOptionDialog(null,
                "TRADING WALLET 2026\nScegli un'opzione per iniziare:",
                "Benvenuto",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, opzioni, opzioni[0]);

        if (scelta == 2 || scelta == -1) System.exit(0);

        if (scelta == 1) { // REGISTRAZIONE (SIGN IN)
            JTextField nomeF = new JTextField();
            JTextField cognomeF = new JTextField();
            JTextField emailF = new JTextField();
            JPasswordField pwdF = new JPasswordField();
            Object[] msg = {
                    "Nome:", nomeF,
                    "Cognome:", cognomeF,
                    "Email:", emailF,
                    "Password:", pwdF
            };

            if (JOptionPane.showConfirmDialog(null, msg, "Registrazione Nuovo Utente", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                boolean ok = userService.signIn(nomeF.getText(), cognomeF.getText(), emailF.getText(), new String(pwdF.getPassword()));
                if (ok) {
                    JOptionPane.showMessageDialog(null, "Account creato con successo! Effettua il login.");
                } else {
                    JOptionPane.showMessageDialog(null, "Errore durante la registrazione. L'email potrebbe essere già presente.");
                }
            }
            main(args); // Torna al menu
            return;
        }

        if (scelta == 0) { // LOGIN
            JTextField emailF = new JTextField();
            JPasswordField pwdF = new JPasswordField();
            Object[] msg = {"Email:", emailF, "Password:", pwdF};

            if (JOptionPane.showConfirmDialog(null, msg, "Accesso Area Trading", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                String email = emailF.getText();
                if (userService.login(email, new String(pwdF.getPassword()))) {
                    avviaSistemaTrading(email);
                } else {
                    JOptionPane.showMessageDialog(null, "Credenziali errate o Database non raggiungibile.");
                    main(args);
                }
            }
        }
    }

    private static void avviaSistemaTrading(String emailUtente) {
        // Inizializza i 3 grafici separati con pulsanti BUY/SELL
        candelaService.inizializzaGrafici(cripto, emailUtente);

        // Thread per scaricare i dati reali
        new Thread(() -> {
            HttpClient client = HttpClient.newHttpClient();
            System.out.println("Caricamento dati di mercato in corso...");

            for (String s : cripto) {
                try {
                    String url = "https://min-api.cryptocompare.com/data/v2/histoday?fsym=" + s + "&tsym=USD&limit=50";
                    HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).build();
                    HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());

                    if (res.statusCode() == 200) {
                        JsonObject json = JsonParser.parseString(res.body()).getAsJsonObject();
                        JsonArray dataArray = json.getAsJsonObject("Data").getAsJsonArray("Data");

                        for (int i = 0; i < dataArray.size(); i++) {
                            JsonObject day = dataArray.get(i).getAsJsonObject();
                            Date date = new Date(day.get("time").getAsLong() * 1000);

                            candelaService.aggiungiCandela(
                                    s,
                                    date,
                                    day.get("open").getAsDouble(),
                                    day.get("high").getAsDouble(),
                                    day.get("low").getAsDouble(),
                                    day.get("close").getAsDouble()
                            );
                        }
                        // Piccola pausa tra il caricamento di una crypto e l'altra
                        Thread.sleep(300);
                    }
                } catch (Exception e) {
                    System.err.println("Errore caricamento " + s + ": " + e.getMessage());
                }
            }
            System.out.println("Sistema pronto. Operatività abilitata.");
        }).start();
    }
}