package org.example.model;

import java.math.BigDecimal;

public class Portafoglio {
    private int idPortafoglio;
    private int idUtente;
    private BigDecimal saldoDisponibile;

    public Portafoglio() {}

    public Portafoglio(int idPortafoglio, int idUtente, BigDecimal saldoDisponibile) {
        this.idPortafoglio = idPortafoglio;
        this.idUtente = idUtente;
        this.saldoDisponibile = saldoDisponibile;
    }

    // Getter e Setter
    public int getIdPortafoglio() { return idPortafoglio; }
    public void setIdPortafoglio(int idPortafoglio) { this.idPortafoglio = idPortafoglio; }
    public int getIdUtente() { return idUtente; }
    public void setIdUtente(int idUtente) { this.idUtente = idUtente; }
    public BigDecimal getSaldoDisponibile() { return saldoDisponibile; }
    public void setSaldoDisponibile(BigDecimal saldoDisponibile) { this.saldoDisponibile = saldoDisponibile; }
}