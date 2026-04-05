package org.example.dao.impl;

import org.example.dao.UtenteDAO;
import org.example.database.DatabaseManager;
import org.example.model.Utente;
import java.sql.*;

public class UtenteDAOImpl implements UtenteDAO {

    @Override
    public int insertUtente(Utente utente) throws SQLException {
        String sql = "INSERT INTO utenti (nome, cognome, email, password_hash) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, utente.getNome());
            pstmt.setString(2, utente.getCognome());
            pstmt.setString(3, utente.getEmail());
            pstmt.setString(4, utente.getPasswordHash());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int idGenerato = generatedKeys.getInt(1);
                    creaPortafoglioIniziale(idGenerato);
                    return idGenerato;
                }
            }
        }
        return -1;
    }

    private void creaPortafoglioIniziale(int idUtente) throws SQLException {
        String sql = "INSERT INTO portafoglio (id_utente, saldo_disponibile) VALUES (?, 0.00)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idUtente);
            pstmt.executeUpdate();
        }
    }

    @Override
    public Utente getByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM utenti WHERE email = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Utente(
                            rs.getInt("id_utente"),
                            rs.getString("nome"),
                            rs.getString("cognome"),
                            rs.getString("email"),
                            rs.getString("password_hash")
                    );
                }
            }
        }
        return null;
    }
}