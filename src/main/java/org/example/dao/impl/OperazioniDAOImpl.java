package org.example.dao.impl;

import org.example.dao.OperazioniDAO;
import org.example.database.DatabaseManager;
import java.math.BigDecimal;
import java.sql.*;

public class OperazioniDAOImpl implements OperazioniDAO {

    @Override
    public void eseguiAcquisto(int idPortafoglio, int idAsset, BigDecimal quantita, BigDecimal prezzo) throws SQLException {
        Connection conn = DatabaseManager.getConnection();

        try {
            // Disabilita l'autocommit per gestire la transazione manualmente
            conn.setAutoCommit(false);

            // 1. Scalare il saldo dal portafoglio
            String sqlSaldo = "UPDATE portafoglio SET saldo_disponibile = saldo_disponibile - ? WHERE id_portafoglio = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlSaldo)) {
                ps.setBigDecimal(1, prezzo.multiply(quantita));
                ps.setInt(2, idPortafoglio);
                ps.executeUpdate();
            }

            // 2. Inserire il log della transazione
            String sqlTx = "INSERT INTO transazioni (id_portafoglio, id_asset, tipo_operazione, quantita, prezzo_eseguito) VALUES (?, ?, 'BUY', ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlTx)) {
                ps.setInt(1, idPortafoglio);
                ps.setInt(2, idAsset);
                ps.setBigDecimal(3, quantita);
                ps.setBigDecimal(4, prezzo);
                ps.executeUpdate();
            }

            // 3. Aggiornare la posizione (UPSERT: inserisce o aggiorna se già esiste)
            String sqlPos = "INSERT INTO posizioni (id_portafoglio, id_asset, quantita, prezzo_medio_carico) " +
                    "VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE " +
                    "quantita = quantita + VALUES(quantita), " +
                    "prezzo_medio_carico = (prezzo_medio_carico + VALUES(prezzo_medio_carico)) / 2";
            try (PreparedStatement ps = conn.prepareStatement(sqlPos)) {
                ps.setInt(1, idPortafoglio);
                ps.setInt(2, idAsset);
                ps.setBigDecimal(3, quantita);
                ps.setBigDecimal(4, prezzo);
                ps.executeUpdate();
            }

            // Se tutto è andato bene, conferma i cambiamenti
            conn.commit();

        } catch (SQLException e) {
            // In caso di errore, annulla ogni modifica fatta durante questa transazione
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
            conn.close();
        }
    }
}