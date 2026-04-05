package org.example.dao;
import java.math.BigDecimal;
import java.sql.SQLException;

public interface OperazioniDAO {
    void eseguiAcquisto(int idPortafoglio, int idAsset, BigDecimal quantita, BigDecimal prezzo) throws SQLException;
}