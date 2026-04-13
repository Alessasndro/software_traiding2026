package org.example.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;

// ATTENZIONE: Questi sono gli import di JUnit per fare i controlli (Assert)
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MarketDataCacheTest {

    private MarketDataCache cache;

    @BeforeEach
    void setUp() {
        // Inizializziamo una cache pulita prima di ogni test
        cache = new MarketDataCache();
    }

    @Test
    @DisplayName("Test 1: Inserimento e recupero corretto dell'ultimo prezzo")
    void testAggiungiERecupera() {
        // Aggiungiamo due candele. La seconda è la più recente.
        cache.aggiungiCandela("BTC", new Date(), 50000.0, 51000.0, 49000.0, 50500.0);
        cache.aggiungiCandela("BTC", new Date(), 50500.0, 52000.0, 50000.0, 51800.0); // Chiusura a 51800

        // L'ultimo prezzo deve essere la chiusura (close) dell'ultima candela inserita
        assertEquals(51800.0, cache.getUltimoPrezzo("BTC"));
    }

    @Test
    @DisplayName("Test 2: Lancio eccezione se si chiede una moneta inesistente")
    void testEccezioneMonetaInesistente() {
        // Usiamo assertThrows per verificare che la tua IllegalStateException venga effettivamente lanciata
        Exception eccezione = assertThrows(IllegalStateException.class, () -> {
            cache.getUltimoPrezzo("FAKE_COIN");
        });

        // Verifichiamo anche che il messaggio di errore sia quello esatto che hai scritto tu
        assertEquals("Nessun prezzo disponibile per FAKE_COIN", eccezione.getMessage());
    }

    @Test
    @DisplayName("Test 3: Rispetto del limite massimo di 100 candele (Eviction)")
    void testLimite100Candele() {
        // Inseriamo 105 candele finte in un colpo solo tramite un ciclo
        for (int i = 1; i <= 105; i++) {
            // Usiamo 'i' come prezzo di chiusura per distinguerle (la prima chiude a 1, l'ultima a 105)
            cache.aggiungiCandela("ETH", new Date(), 10.0, 20.0, 5.0, (double) i);
        }

        // 1. La dimensione della lista non deve superare i 100!
        assertEquals(100, cache.getCloses("ETH").size());

        // 2. L'ultimo prezzo deve essere l'ultimo inserito (105)
        assertEquals(105.0, cache.getUltimoPrezzo("ETH"));

        // 3. Poiché le prime 5 candele (da 1 a 5) sono state cancellate,
        // la candela più vecchia ora in memoria deve essere la numero 6!
        assertEquals(6.0, cache.getCloses("ETH").get(0));
    }
}