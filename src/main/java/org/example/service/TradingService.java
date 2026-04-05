package org.example.service;

import java.sql.*;

public class TradingService {
    private final String url = "jdbc:mysql://localhost:3306/trading_wallet";
    private final String user = "root";
    private final String password = "";

    public void eseguiOrdine(String email, String simbolo, String tipo, double prezzo) {
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            // 1. Troviamo l'id_portafoglio associato all'email dell'utente
            String queryPortafoglio = "SELECT p.id_portafoglio FROM portafoglio p JOIN utenti u ON p.id_utente = u.id_utente WHERE u.email = ?";
            PreparedStatement ps1 = conn.prepareStatement(queryPortafoglio);
            ps1.setString(1, email);
            ResultSet rs1 = ps1.executeQuery();

            // 2. Troviamo l'id_asset associato al simbolo (BTC, ETH, etc.)
            String queryAsset = "SELECT id_asset FROM stock_info WHERE symbol = ?";
            PreparedStatement ps2 = conn.prepareStatement(queryAsset);
            ps2.setString(1, simbolo);
            ResultSet rs2 = ps2.executeQuery();

            if (rs1.next() && rs2.next()) {
                int idPortafoglio = rs1.getInt("id_portafoglio");
                int idAsset = rs2.getInt("id_asset");

                // 3. Inseriamo la transazione nella tabella 'transazioni'
                String queryTransazione = "INSERT INTO transazioni (id_portafoglio, id_asset, tipo_operazione, quantita, prezzo_eseguito) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement ps3 = conn.prepareStatement(queryTransazione);
                ps3.setInt(1, idPortafoglio);
                ps3.setInt(2, idAsset);
                ps3.setString(3, tipo); // 'BUY' o 'SELL'
                ps3.setDouble(4, 1.0);   // Quantità fissa di 1 unità
                ps3.setDouble(5, prezzo);

                ps3.executeUpdate();
                System.out.println("ORDINE REALE REGISTRATO: " + tipo + " " + simbolo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}