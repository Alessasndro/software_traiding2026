package org.example.service;

import java.sql.*;

public class UserService {
    private final String url = "jdbc:mysql://localhost:3306/trading_wallet?useSSL=false&serverTimezone=UTC";
    private final String user = "root";
    private final String password = "1234";

    public boolean login(String email, String pwd) {
        // Query aggiornata: usiamo password_hash come da tuo schema
        String query = "SELECT * FROM utenti WHERE email = ? AND password_hash = ?";
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, email);
            ps.setString(2, pwd);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            System.err.println("Errore Login: " + e.getMessage());
            return false;
        }
    }

    public boolean signIn(String nome, String cognome, String email, String pwd) {
        // Query aggiornata con la colonna 'cognome'
        String query = "INSERT INTO utenti (nome, cognome, email, password_hash) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, nome);
            ps.setString(2, cognome);
            ps.setString(3, email);
            ps.setString(4, pwd);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Errore Sign In: " + e.getMessage());
            return false;
        }
    }
}