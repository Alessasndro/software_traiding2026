package org.example.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Transazione {
    private int idTransazione;
    private int idPortafoglio;
    private Integer idAsset; // Integer (oggetto) perché può essere NULL per depositi/prelievi
    private String tipoOperazione; // BUY, SELL, DEPOSIT, WITHDRAW
    private BigDecimal quantita;
    private BigDecimal prezzoEseguito;
    private Timestamp dataOperazione;

    public Transazione() {}

    // Getter e Setter
    public int getIdTransazione() { return idTransazione; }
    public void setIdTransazione(int idTransazione) { this.idTransazione = idTransazione; }
    public int getIdPortafoglio() { return idPortafoglio; }
    public void setIdPortafoglio(int idPortafoglio) { this.idPortafoglio = idPortafoglio; }
    public Integer getIdAsset() { return idAsset; }
    public void setIdAsset(Integer idAsset) { this.idAsset = idAsset; }
    public String getTipoOperazione() { return tipoOperazione; }
    public void setTipoOperazione(String tipoOperazione) { this.tipoOperazione = tipoOperazione; }
    public BigDecimal getQuantita() { return quantita; }
    public void setQuantita(BigDecimal quantita) { this.quantita = quantita; }
    public BigDecimal getPrezzoEseguito() { return prezzoEseguito; }
    public void setPrezzoEseguito(BigDecimal prezzoEseguito) { this.prezzoEseguito = prezzoEseguito; }
    public Timestamp getDataOperazione() { return dataOperazione; }
    public void setDataOperazione(Timestamp dataOperazione) { this.dataOperazione = dataOperazione; }
}