package org.example.service;

import org.example.database.DatabaseManager;
import java.sql.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class TradingService {
    private PortfolioObserver observer;

    public void setObserver(PortfolioObserver observer) {
        this.observer = observer;
    }

    public void eseguiOrdine(String email, String simbolo, String tipo, double prezzo, double quantitaInput) {
        Connection conn = null;
        try {
            conn = DatabaseManager.getConnection();
            conn.setAutoCommit(false); // Transazione atomica

            String sqlPort = "SELECT p.id_portafoglio, p.saldo_disponibile FROM portafoglio p " +
                    "JOIN utenti u ON p.id_utente = u.id_utente WHERE u.email = ?";
            PreparedStatement psPort = conn.prepareStatement(sqlPort);
            psPort.setString(1, email);
            ResultSet rsPort = psPort.executeQuery();

            if (!rsPort.next()) return;

            int idPort = rsPort.getInt("id_portafoglio");
            BigDecimal saldoAttuale = rsPort.getBigDecimal("saldo_disponibile");
            BigDecimal prezzoEseguito = BigDecimal.valueOf(prezzo);
            BigDecimal quantita = BigDecimal.valueOf(quantitaInput); // Usa la quantità inserita dall'utente

            // Costo totale = Prezzo * Quantità
            BigDecimal costoTotale = prezzoEseguito.multiply(quantita);

            if (tipo.equalsIgnoreCase("BUY") && saldoAttuale.compareTo(costoTotale) < 0) {
                System.err.println("ERRORE: Saldo insufficiente per l'acquisto.");
                return; // Esce senza fare nulla
            }

            // Inserimento Transazione
            String sqlTrans = "INSERT INTO transazioni (id_portafoglio, id_asset, tipo_operazione, quantita, prezzo_eseguito) " +
                    "VALUES (?, (SELECT id_asset FROM stock_info WHERE symbol = ?), ?, ?, ?)";
            PreparedStatement psTrans = conn.prepareStatement(sqlTrans);
            psTrans.setInt(1, idPort);
            psTrans.setString(2, simbolo);
            psTrans.setString(3, tipo.toUpperCase());
            psTrans.setBigDecimal(4, quantita);
            psTrans.setBigDecimal(5, prezzoEseguito);
            psTrans.executeUpdate();

            // Aggiornamento Saldo Portafoglio
            String sqlUpdSaldo = "UPDATE portafoglio SET saldo_disponibile = saldo_disponibile " +
                    (tipo.equalsIgnoreCase("BUY") ? "- ?" : "+ ?") + " WHERE id_portafoglio = ?";
            PreparedStatement psUpdSaldo = conn.prepareStatement(sqlUpdSaldo);
            psUpdSaldo.setBigDecimal(1, costoTotale);
            psUpdSaldo.setInt(2, idPort);
            psUpdSaldo.executeUpdate();

            // Gestione Posizioni (PMC e Quantità)
            gestisciPosizione(conn, idPort, simbolo, tipo, quantita, prezzoEseguito);

            conn.commit();
            System.out.println("ORDINE ESEGUITO: " + tipo + " " + quantitaInput + " " + simbolo + " a $" + prezzo);

            if (observer != null) observer.onPortfolioChanged();

        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
        } finally {
            if (conn != null) try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    private void gestisciPosizione(Connection conn, int idPort, String simbolo, String tipo, BigDecimal quantita, BigDecimal prezzo) throws SQLException {
        String sqlCheck = "SELECT p.id_posizione, p.quantita, p.prezzo_medio_carico FROM posizioni p " +
                "JOIN stock_info s ON p.id_asset = s.id_asset WHERE p.id_portafoglio = ? AND s.symbol = ?";
        PreparedStatement psCheck = conn.prepareStatement(sqlCheck);
        psCheck.setInt(1, idPort);
        psCheck.setString(2, simbolo);
        ResultSet rs = psCheck.executeQuery();

        if (rs.next()) {
            BigDecimal qtaAttuale = rs.getBigDecimal("quantita");
            BigDecimal pmcAttuale = rs.getBigDecimal("prezzo_medio_carico");
            BigDecimal nuovaQta;
            BigDecimal nuovoPmc = pmcAttuale;

            if (tipo.equalsIgnoreCase("BUY")) {
                nuovaQta = qtaAttuale.add(quantita);
                BigDecimal valoreTotale = (qtaAttuale.multiply(pmcAttuale)).add(quantita.multiply(prezzo));
                nuovoPmc = valoreTotale.divide(nuovaQta, 4, RoundingMode.HALF_UP);
            } else {
                nuovaQta = qtaAttuale.subtract(quantita);
                if (nuovaQta.compareTo(BigDecimal.ZERO) < 0) throw new SQLException("Quantità insufficiente.");
            }

            String sqlUpdate = "UPDATE posizioni SET quantita = ?, prezzo_medio_carico = ? WHERE id_posizione = ?";
            PreparedStatement psUpd = conn.prepareStatement(sqlUpdate);
            psUpd.setBigDecimal(1, nuovaQta);
            psUpd.setBigDecimal(2, nuovoPmc);
            psUpd.setInt(3, rs.getInt("id_posizione"));
            psUpd.executeUpdate();
        } else {
            if (tipo.equalsIgnoreCase("BUY")) {
                String sqlInsPos = "INSERT INTO posizioni (id_portafoglio, id_asset, quantita, prezzo_medio_carico) " +
                        "VALUES (?, (SELECT id_asset FROM stock_info WHERE symbol = ?), ?, ?)";
                PreparedStatement psIns = conn.prepareStatement(sqlInsPos);
                psIns.setInt(1, idPort);
                psIns.setString(2, simbolo);
                psIns.setBigDecimal(3, quantita);
                psIns.setBigDecimal(4, prezzo);
                psIns.executeUpdate();
            }
        }
    }
}