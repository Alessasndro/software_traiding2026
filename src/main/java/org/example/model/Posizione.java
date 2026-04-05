package org.example.model;

import java.math.BigDecimal;

public class Posizione {
    private int idPosizione;
    private int idPortafoglio;
    private int idAsset;
    private BigDecimal quantita;
    private BigDecimal prezzoMedioCarico;

    public Posizione() {}

    public int getIdPosizione() { return idPosizione; }
    public void setIdPosizione(int idPosizione) { this.idPosizione = idPosizione; }
    public int getIdPortafoglio() { return idPortafoglio; }
    public void setIdPortafoglio(int idPortafoglio) { this.idPortafoglio = idPortafoglio; }
    public int getIdAsset() { return idAsset; }
    public void setIdAsset(int idAsset) { this.idAsset = idAsset; }
    public BigDecimal getQuantita() { return quantita; }
    public void setQuantita(BigDecimal quantita) { this.quantita = quantita; }
    public BigDecimal getPrezzoMedioCarico() { return prezzoMedioCarico; }
    public void setPrezzoMedioCarico(BigDecimal prezzoMedioCarico) { this.prezzoMedioCarico = prezzoMedioCarico; }
}