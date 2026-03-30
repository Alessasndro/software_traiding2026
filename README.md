Progetto Software Trading 2026

Sistema Java per la gestione e simulazione di un portafoglio titoli azionari con persistenza su database relazionale.
Stack Tecnologico

    Linguaggio: Java 25 (OpenJDK)

    Build Tool: Maven

    Database: MySQL 8.0+

    Connection Pool: HikariCP 5.1.0

    Logging: SLF4J (SimpleLogger)

Architettura Database (Schema: trading_wallet)

Il database è strutturato per gestire l'anagrafica dei titoli e la volatilità dei prezzi in tempo reale:

    stock_info: Contiene i dati statici degli asset (Simbolo, Nome, Settore).

    stock_live: Gestisce i dati dinamici (Prezzo corrente, Timestamp ultimo aggiornamento).

    utenti: Gestione profili e liquidità disponibile.

    portafoglio: Relazione tra utente, quantità possedute e prezzo medio di carico.

Il motore di storage utilizzato è InnoDB per garantire il supporto alle transazioni ACID e l'integrità referenziale tramite Foreign Keys.
Gestione Connessioni: HikariCP

Per l'interazione tra l'applicazione e il database è stato implementato HikariCP.
Analisi Tecnica del Pooling

A differenza della gestione standard tramite DriverManager, HikariCP mantiene un set di connessioni pre-allocate (Pool). Questo approccio risolve due problematiche critiche:

    Latenza: Elimina il tempo di overhead richiesto per l'handshake TCP e l'autenticazione a ogni query.

    Scalabilità: Limita il numero massimo di connessioni simultanee (impostato a 10), prevenendo il sovraccarico del server MySQL.

Il sistema include meccanismi di fail-fast e leak-detection per monitorare la salute delle connessioni attive.
Istruzioni per il Deployment

    Configurazione DB: Creare lo schema trading_wallet su MySQL locale.

    Credenziali: Verificare i parametri di accesso nel file DatabaseManager.java.

    Compilazione: Eseguire mvn clean install dalla root del progetto.

    Avvio: Eseguire la classe Main.java per avviare il test di diagnostica del pool.
