package org.example.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.example.exception.MarketDataException;
import org.example.ui.TradingTerminalUI;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Date;

public class MarketDataService {
    private final HttpClient client;

    // Al posto del vecchio CandelaService, ora usiamo i due nuovi moduli separati
    private final MarketDataCache dataCache;
    private TradingTerminalUI ui;

    public MarketDataService(MarketDataCache dataCache) {
        this.client = HttpClient.newHttpClient();
        this.dataCache = dataCache;
    }

    // Aggiungiamo un setter per la UI in modo da evitare dipendenze circolari
    public void setUi(TradingTerminalUI ui) {
        this.ui = ui;
    }

    public void caricaStorico(String simbolo) throws MarketDataException {
        eseguiChiamata(simbolo, 100);
    }

    public void aggiornaLive(String simbolo) throws MarketDataException {
        eseguiChiamata(simbolo, 1);
    }

    private void eseguiChiamata(String simbolo, int limit) throws MarketDataException {
        try {
            String url = "https://min-api.cryptocompare.com/data/v2/histominute?fsym=" + simbolo + "&tsym=USD&limit=" + limit;
            HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).build();
            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());

            if (res.statusCode() == 200) {
                JsonObject json = JsonParser.parseString(res.body()).getAsJsonObject();
                JsonArray dataArray = json.getAsJsonObject("Data").getAsJsonArray("Data");

                for (int i = 0; i < dataArray.size(); i++) {
                    JsonObject point = dataArray.get(i).getAsJsonObject();
                    Date date = new Date(point.get("time").getAsLong() * 1000);

                    // 1. Salviamo i dati puri nel "motore" (Cache)
                    dataCache.aggiungiCandela(
                            simbolo, date,
                            point.get("open").getAsDouble(),
                            point.get("high").getAsDouble(),
                            point.get("low").getAsDouble(),
                            point.get("close").getAsDouble()
                    );
                }

                // 2. Ordiniamo alla "carrozzeria" (UI) di aggiornare il disegno visivo
                if (ui != null) {
                    ui.aggiornaGraficoVisivo(simbolo);
                }

            } else {
                throw new MarketDataException("Errore API HTTP: " + res.statusCode());
            }
        } catch (Exception e) {
            throw new MarketDataException("Errore di rete per " + simbolo, e);
        }
    }
}