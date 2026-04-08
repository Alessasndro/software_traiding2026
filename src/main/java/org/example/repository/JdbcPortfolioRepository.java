package org.example.repository;

import org.example.database.DatabaseManager;
import org.example.dto.PosizioneDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcPortfolioRepository implements PortfolioRepository {

    private static final Logger logger = LoggerFactory.getLogger(JdbcPortfolioRepository.class);

    @Override
    public double getSaldoDisponibile(String email) {
        String sql = "SELECT saldo_disponibile FROM portafoglio p JOIN utenti u ON p.id_utente = u.id_utente WHERE u.email = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);

            // Nested try-with-resources for ResultSet
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("saldo_disponibile");
                }
            }
        } catch (SQLException e) {
            logger.error("Errore durante il recupero del saldo per l'utente: {}", email, e);
            // Throw a custom runtime exception so the application knows the query failed
            throw new RuntimeException("Database error retrieving balance", e);
        }

        return 0.0; // Return 0.0 only if the query succeeds but no user is found
    }

    @Override
    public List<PosizioneDTO> getPosizioniAttive(String email) {
        List<PosizioneDTO> posizioni = new ArrayList<>();
        String sql = "SELECT s.symbol, p.quantita, p.prezzo_medio_carico FROM posizioni p " +
                "JOIN stock_info s ON p.id_asset = s.id_asset " +
                "JOIN portafoglio port ON p.id_portafoglio = port.id_portafoglio " +
                "JOIN utenti u ON port.id_utente = u.id_utente " +
                "WHERE u.email = ? AND p.quantita > 0";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    posizioni.add(new PosizioneDTO(
                            rs.getString("symbol"),
                            rs.getDouble("quantita"),
                            rs.getDouble("prezzo_medio_carico")
                    ));
                }
            }
        } catch (SQLException e) {
            logger.error("Errore durante il recupero delle posizioni attive per l'utente: {}", email, e);
            throw new RuntimeException("Database error retrieving active positions", e);
        }

        return posizioni;
    }
}