package org.example.repository;

import org.example.database.DatabaseManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JdbcUserRepository implements UserRepository {
    private static final Logger logger = LoggerFactory.getLogger(JdbcUserRepository.class);

    @Override
    public boolean login(String email, String pwd) {
        String query = "SELECT * FROM utenti WHERE email = ? AND password_hash = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, email);
            ps.setString(2, pwd);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            logger.error("Errore durante il login per l'utente {}: ", email, e);
            return false;
        }
    }

    @Override
    public boolean signIn(String nome, String cognome, String email, String pwd) {
        String query = "INSERT INTO utenti (nome, cognome, email, password_hash) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, nome);
            ps.setString(2, cognome);
            ps.setString(3, email);
            ps.setString(4, pwd);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Errore durante la registrazione dell'utente {}: ", email, e);
            return false;
        }
    }
}