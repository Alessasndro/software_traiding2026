package org.example;

import org.example.database.DatabaseManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Punto di ingresso principale dell'applicazione.
 * Utilizzato per il bootstrap dei servizi e la verifica dell'infrastruttura database.
 */
public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("Avvio del sistema Trading 2026 in corso...");

        // Apertura della connessione tramite il blocco try-with-resources
        // per garantire il rilascio automatico della risorsa SQL
        try (Connection conn = DatabaseManager.getConnection()) {

            if (conn != null && !conn.isClosed()) {
                logger.info("Stato connessione: ATTIVA (Gestita da HikariCP)");

                // Query di diagnostica per confermare l'identità dello schema database
                try (Statement stmt = conn.createStatement()) {
                    ResultSet rs = stmt.executeQuery("SELECT DATABASE()");
                    if (rs.next()) {
                        String dbName = rs.getString(1);
                        logger.info("Connessione stabilita con successo allo schema: {}", dbName);
                    }
                }
            }

        } catch (Exception e) {
            logger.error("Inizializzazione fallita: errore critico durante la verifica del Database.", e);
            System.exit(1); // Codice d'uscita 1 indica un termine per errore
        } finally {
            // Chiusura del pool solo al termine del ciclo di vita dell'applicazione
            DatabaseManager.closePool();
            logger.info("Processo terminato correttamente.");
        }
    }
}