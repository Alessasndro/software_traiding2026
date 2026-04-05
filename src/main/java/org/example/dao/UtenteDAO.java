package org.example.dao;
import org.example.model.Utente;
import java.sql.SQLException;

public interface UtenteDAO {
    int insertUtente(Utente utente) throws SQLException;
    Utente getByEmail(String email) throws SQLException;
}