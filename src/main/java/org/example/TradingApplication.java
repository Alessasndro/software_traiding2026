package org.example;

import org.example.exception.MarketDataException;
import org.example.repository.JdbcPortfolioRepository;
import org.example.repository.PortfolioRepository;
import org.example.service.MarketDataCache;
import org.example.service.MarketDataService;
import org.example.service.TradingService;
import org.example.service.UserService;
import org.example.ui.AuthUI;
import org.example.ui.TradingTerminalUI;
import org.example.ui.WalletPanel;

import java.util.Arrays;
import java.util.List;

public class TradingApplication {
    private final List<String> cripto = Arrays.asList("BTC", "ETH", "SOL");

    public void start() {
        // Assemblaggio modulo Autenticazione
        org.example.repository.UserRepository userRepository = new org.example.repository.JdbcUserRepository();
        UserService userService = new UserService(userRepository);
        AuthUI authUI = new AuthUI(userService);

        String emailUtente = authUI.avviaMenuIniziale();

        if (emailUtente != null) {
            assemblaModuli(emailUtente);
        }
    }
    private void assemblaModuli(String emailUtente) {
        MarketDataCache dataCache = new MarketDataCache();
        PortfolioRepository portfolioRepository = new JdbcPortfolioRepository();
        TradingService tradingService = new TradingService();

        WalletPanel walletPanel = new WalletPanel();
        org.example.controller.WalletController walletController = new org.example.controller.WalletController(
                emailUtente, portfolioRepository, walletPanel
        );

        tradingService.setObserver(walletController);
        walletController.avvia();

        TradingTerminalUI terminalUI = new TradingTerminalUI(
                emailUtente, tradingService, dataCache, walletPanel
        );

        MarketDataService dataService = new MarketDataService(dataCache);
        dataService.setUi(terminalUI);

        terminalUI.inizializzaInterfaccia(cripto);
        avviaAggiornamentoDati(dataService);
    }

    //Gestione del thread per scaricare i dati senza bloccare la UI
    private void avviaAggiornamentoDati(MarketDataService dataService) {
        new Thread(() -> {
            // Storico iniziale
            for (String s : cripto) {
                try {
                    dataService.caricaStorico(s);
                    Thread.sleep(300);
                } catch (MarketDataException | InterruptedException e) {
                    System.err.println("⚠ [ERRORE STORICO] " + s + ": " + e.getMessage());
                }
            }

            // Loop infinito per il Live
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
                    System.err.println("Thread dati interrotto.");
                    break;
                }
            }
        }).start();
    }
}