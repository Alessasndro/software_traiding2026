package org.example.service;

import org.example.database.DatabaseManager;

// IMPORT PER I TEST
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

// IMPORT PER SQL E MOCKITO
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class TradingServiceTest {

    @Test
    @DisplayName("Test 1: Esecuzione ordine con successo notifica l'Observer")
    void testEseguiOrdineNotificaObserver() throws Exception {
        PortfolioObserver mockObserver = mock(PortfolioObserver.class);
        Connection mockConn = mock(Connection.class);
        PreparedStatement mockStmt = mock(PreparedStatement.class);
        ResultSet mockRs = mock(ResultSet.class);

        when(mockConn.prepareStatement(anyString())).thenReturn(mockStmt);
        when(mockStmt.executeQuery()).thenReturn(mockRs);
        when(mockRs.next()).thenReturn(true, false);
        when(mockRs.getInt("id_portafoglio")).thenReturn(1);

        // Utente Ricco!
        when(mockRs.getBigDecimal("saldo_disponibile")).thenReturn(new BigDecimal("1000000.00"));

        try (MockedStatic<DatabaseManager> mockedDb = mockStatic(DatabaseManager.class)) {
            mockedDb.when(DatabaseManager::getConnection).thenReturn(mockConn);

            TradingService tradingService = new TradingService();
            tradingService.setObserver(mockObserver);

            tradingService.eseguiOrdine("elon.musk@test.com", "BTC", "BUY", 50000.00, 0.5);

            verify(mockConn, times(1)).commit();
            verify(mockObserver, times(1)).onPortfolioChanged();
        }
    }

    @Test
    @DisplayName("Test 2: Saldo insufficiente blocca l'ordine e NON chiama l'Observer")
    void testEseguiOrdineSaldoInsufficiente() throws Exception {
        // --- 1. PREPARAZIONE DEI MOCK ---
        PortfolioObserver mockObserver = mock(PortfolioObserver.class);
        Connection mockConn = mock(Connection.class);
        PreparedStatement mockStmt = mock(PreparedStatement.class);
        ResultSet mockRs = mock(ResultSet.class);

        when(mockConn.prepareStatement(anyString())).thenReturn(mockStmt);
        when(mockStmt.executeQuery()).thenReturn(mockRs);
        when(mockRs.next()).thenReturn(true);
        when(mockRs.getInt("id_portafoglio")).thenReturn(1);

        // --- LA TRAPPOLA: Utente Povero! Solo 10 dollari di saldo ---
        when(mockRs.getBigDecimal("saldo_disponibile")).thenReturn(new BigDecimal("10.00"));

        try (MockedStatic<DatabaseManager> mockedDb = mockStatic(DatabaseManager.class)) {
            mockedDb.when(DatabaseManager::getConnection).thenReturn(mockConn);

            TradingService tradingService = new TradingService();
            tradingService.setObserver(mockObserver);

            // ACT: Proviamo a comprare 0.5 BTC a 50.000$ (costo 25.000$). Ma abbiamo solo 10$!
            tradingService.eseguiOrdine("studente.squattrinato@test.com", "BTC", "BUY", 50000.00, 0.5);

            // --- VERIFICA (La magia di never() ) ---
            // Verifichiamo che il database NON abbia mai salvato la transazione
            verify(mockConn, never()).commit();

            // Verifichiamo che l'interfaccia NON sia mai stata aggiornata
            verify(mockObserver, never()).onPortfolioChanged();
        }
    }
}