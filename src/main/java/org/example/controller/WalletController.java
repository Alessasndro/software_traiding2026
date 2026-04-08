package org.example.controller;

import org.example.dto.PosizioneDTO;
import org.example.repository.PortfolioRepository;
import org.example.service.MarketDataCache;
import org.example.service.PortfolioObserver;
import org.example.ui.WalletView;

import java.util.List;

public class WalletController implements PortfolioObserver {
    private final String emailUtente;
    private final PortfolioRepository repository;
    private final WalletView view;
    private final MarketDataCache dataCache; // ORA IL CONTROLLER CONOSCE I PREZZI!

    public WalletController(String emailUtente, PortfolioRepository repository, WalletView view, MarketDataCache dataCache) {
        this.emailUtente = emailUtente;
        this.repository = repository;
        this.view = view;
        this.dataCache = dataCache;
    }

    public void avvia() {
        aggiornaDati();
    }

    @Override
    public void onPortfolioChanged() {
        aggiornaDati();
    }

    // Metodo pubblico per rinfrescare le percentuali ogni 10 secondi
    public void aggiornaPrezziLive() {
        aggiornaDati();
    }

    private void aggiornaDati() {
        try {
            double saldo = repository.getSaldoDisponibile(emailUtente);
            List<PosizioneDTO> posizioni = repository.getPosizioniAttive(emailUtente);

            // CALCOLO DELLA PERCENTUALE DI GUADAGNO/PERDITA
            for (PosizioneDTO pos : posizioni) {
                try {
                    double prezzoAttuale = dataCache.getUltimoPrezzo(pos.getSymbol());
                    double pmc = pos.getPrezzoMedioCarico();
                    if (pmc > 0) {
                        double variazione = ((prezzoAttuale - pmc) / pmc) * 100;
                        pos.setVariazionePercentuale(variazione);
                    }
                } catch (Exception e) {
                    // Se l'API non ha ancora scaricato il prezzo, lasciamo a 0%
                    pos.setVariazionePercentuale(0.0);
                }
            }

            view.mostraSaldo(saldo);
            view.mostraPosizioni(posizioni);
        } catch (Exception e) {
            view.mostraErrore("Errore nel caricamento dei dati.");
        }
    }
}