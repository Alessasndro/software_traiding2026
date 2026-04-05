package org.example.model;

public class StockInfo {
    private int idAsset;
    private String symbol;
    private String nomeAzienda;
    private String settore;

    public StockInfo() {}

    public StockInfo(int idAsset, String symbol, String nomeAzienda, String settore) {
        this.idAsset = idAsset;
        this.symbol = symbol;
        this.nomeAzienda = nomeAzienda;
        this.settore = settore;
    }

    // Getter e Setter
    public int getIdAsset() { return idAsset; }
    public void setIdAsset(int idAsset) { this.idAsset = idAsset; }
    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }
    public String getNomeAzienda() { return nomeAzienda; }
    public void setNomeAzienda(String nomeAzienda) { this.nomeAzienda = nomeAzienda; }
    public String getSettore() { return settore; }
    public void setSettore(String settore) { this.settore = settore; }
}