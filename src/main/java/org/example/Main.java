package org.example;

import org.example.exception.MarketDataException;
import org.example.service.CandelaService;
import org.example.service.MarketDataService;
import org.example.ui.AuthUI;

import java.util.Arrays;
import java.util.List;

public class Main {
    private static final List<String> cripto = Arrays.asList("BTC", "ETH", "SOL");

    public static void main(String[] args) {
        AuthUI authUI = new AuthUI();
        String emailUtente = authUI.avviaMenuIniziale();

        if (emailUtente != null) {
            avviaSistemaTrading(emailUtente);
        }
    }

    private static void avviaSistemaTrading(String emailUtente) {
        CandelaService candelaService = new CandelaService();
        MarketDataService dataService = new MarketDataService(candelaService);

        candelaService.inizializzaGrafici(cripto, emailUtente);

        new Thread(() -> {
            for (String s : cripto) {
                try {
                    dataService.caricaStorico(s);
                    Thread.sleep(300);
                } catch (MarketDataException | InterruptedException e) {
                    System.err.println("⚠ [ERRORE STORICO] " + s + ": " + e.getMessage());
                }
            }

            while (true) {
                for (String s : cripto) {
                    try {
                        dataService.aggiornaLive(s);
                    } catch (MarketDataException e) {
                        System.err.println("⚠ [ERRORE LIVE] " + s + " -> " + e.getMessage());
                    }
                }
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }).start();
    }
}