package org.example.exception;

/**
 * Lanciata quando si cerca un utente
 * che non esiste nel database.
 */
public class UtenteNonTrovatoException extends Exception {
    public UtenteNonTrovatoException(String messaggio) {
        super(messaggio);
    }
}