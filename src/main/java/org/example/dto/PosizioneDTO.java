//impacchettare i dati di una posizione per mandarli alla UI, senza che la UI sappia nulla del database.

package org.example.dto;

public class PosizioneDTO {
    private String symbol;
    private double quantita;
    private double prezzoMedioCarico;
    private double variazionePercentuale = 0.0;

    public PosizioneDTO(String symbol, double quantita, double prezzoMedioCarico) {
        this.symbol = symbol;
        this.quantita = quantita;
        this.prezzoMedioCarico = prezzoMedioCarico;
    }

    public String getSymbol() { return symbol; }
    public double getQuantita() { return quantita; }
    public double getPrezzoMedioCarico() { return prezzoMedioCarico; }

    public double getVariazionePercentuale() { return variazionePercentuale; }
    public void setVariazionePercentuale(double variazionePercentuale) { this.variazionePercentuale = variazionePercentuale; }
}