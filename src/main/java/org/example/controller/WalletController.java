package org.example.controller;

import org.example.dto.PosizioneDTO;
import org.example.repository.PortfolioRepository;
import org.example.service.PortfolioObserver;
import org.example.ui.WalletView;

import java.util.List;

public class WalletController implements PortfolioObserver {
    private final String emailUtente;
    private final PortfolioRepository repository;
    private final WalletView view;

    public WalletController(String emailUtente, PortfolioRepository repository, WalletView view) {
        this.emailUtente = emailUtente;
        this.repository = repository;
        this.view = view;
    }

    public void avvia() {
        aggiornaDati();
    }

    @Override
    public void onPortfolioChanged() {
        aggiornaDati();
    }

    private void aggiornaDati() {
        try {
            double saldo = repository.getSaldoDisponibile(emailUtente);
            List<PosizioneDTO> posizioni = repository.getPosizioniAttive(emailUtente);

            view.mostraSaldo(saldo);
            view.mostraPosizioni(posizioni);
        } catch (Exception e) {
            view.mostraErrore("Errore nel caricamento dei dati.");
        }
    }
}