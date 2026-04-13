package org.example.controller;

import org.example.dto.PosizioneDTO;
import org.example.repository.PortfolioRepository;
import org.example.service.MarketDataCache;
import org.example.ui.WalletView;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

class WalletControllerTest {

    private PortfolioRepository mockRepository;
    private WalletView mockView;
    private MarketDataCache mockCache;
    private WalletController controller;

    // L'utente di test
    private final String emailTest = "test@unimi.it";

    @BeforeEach
    void setUp() {
        // 1. Prepariamo le controfigure per ogni test
        mockRepository = mock(PortfolioRepository.class);
        mockView = mock(WalletView.class);
        mockCache = mock(MarketDataCache.class);

        // 2. Creiamo il controller passandogli le controfigure
        controller = new WalletController(emailTest, mockRepository, mockView, mockCache);
    }

    @Test
    @DisplayName("Test 1: Calcolo corretto della percentuale (+50%)")
    void testCalcoloPercentualeCorretta() throws Exception {
        // --- PREPARAZIONE ---
        double saldoFinto = 1500.00;

        // Creiamo un finto DTO (invece di usare new PosizioneDTO, lo mockiamo così non dipende dalla tua implementazione esatta!)
        PosizioneDTO mockPosizione = mock(PosizioneDTO.class);
        when(mockPosizione.getSymbol()).thenReturn("BTC");
        when(mockPosizione.getPrezzoMedioCarico()).thenReturn(100.0); // Comprato a 100$

        List<PosizioneDTO> fintaListaPosizioni = Collections.singletonList(mockPosizione);

        // Addestriamo i mock
        when(mockRepository.getSaldoDisponibile(emailTest)).thenReturn(saldoFinto);
        when(mockRepository.getPosizioniAttive(emailTest)).thenReturn(fintaListaPosizioni);

        // Il prezzo live ora è 150$ (Guadagno del 50%)
        when(mockCache.getUltimoPrezzo("BTC")).thenReturn(150.0);

        // --- AZIONE ---
        controller.avvia(); // Questo chiama internamente aggiornaDati()

        // --- VERIFICA ---
        // 1. Ha calcolato e settato esattamente il 50.0%?
        verify(mockPosizione, times(1)).setVariazionePercentuale(50.0);

        // 2. Ha passato i dati giusti alla vista?
        verify(mockView, times(1)).mostraSaldo(saldoFinto);
        verify(mockView, times(1)).mostraPosizioni(fintaListaPosizioni);
    }

    @Test
    @DisplayName("Test 2: Prezzo mancante dall'API setta variazione a 0%")
    void testGestioneEccezionePrezzoMancante() throws Exception {
        PosizioneDTO mockPosizione = mock(PosizioneDTO.class);
        when(mockPosizione.getSymbol()).thenReturn("ETH");
        when(mockPosizione.getPrezzoMedioCarico()).thenReturn(2000.0);

        when(mockRepository.getSaldoDisponibile(emailTest)).thenReturn(0.0);
        when(mockRepository.getPosizioniAttive(emailTest)).thenReturn(Collections.singletonList(mockPosizione));

        // Simuliamo che la cache lanci un'eccezione (es. API offline)
        when(mockCache.getUltimoPrezzo("ETH")).thenThrow(new RuntimeException("API Offline"));

        controller.avvia();

        // Deve aver intercettato l'errore e settato la variazione a 0.0
        verify(mockPosizione, times(1)).setVariazionePercentuale(0.0);
    }

    @Test
    @DisplayName("Test 3: Divisione per zero evitata se PMC è 0")
    void testGestionePmcZero() throws Exception {
        PosizioneDTO mockPosizione = mock(PosizioneDTO.class);
        when(mockPosizione.getSymbol()).thenReturn("DOGE");

        // Il caso critico: Prezzo Medio di Carico = 0
        when(mockPosizione.getPrezzoMedioCarico()).thenReturn(0.0);

        when(mockRepository.getSaldoDisponibile(emailTest)).thenReturn(100.0);
        when(mockRepository.getPosizioniAttive(emailTest)).thenReturn(Collections.singletonList(mockPosizione));
        when(mockCache.getUltimoPrezzo("DOGE")).thenReturn(0.15);

        controller.avvia();

        // Verifichiamo che NON abbia provato a fare il calcolo (evitando Infinity/NaN)
        // La funzione setVariazionePercentuale non deve MAI essere chiamata in questo caso,
        // oppure è chiamata a 0 nel catch se ha lanciato eccezione. Nel tuo codice viene ignorata.
        verify(mockPosizione, never()).setVariazionePercentuale(anyDouble());

        // Ma la vista deve comunque aggiornarsi senza crashare!
        verify(mockView, times(1)).mostraSaldo(100.0);
    }
}