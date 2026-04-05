package org.example.exception;

/**
 *  utente prova a comprare un asset
 * ma non ha abbastanza credito nel portafoglio.
 */
public class SaldoInsufficienteException extends Exception {
    public SaldoInsufficienteException(String messaggio) {
        super(messaggio);
    }
}