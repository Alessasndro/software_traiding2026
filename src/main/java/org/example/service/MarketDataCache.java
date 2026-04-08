package org.example.service;

import java.util.*;

public class MarketDataCache {

    // Le mappe dei dati che prima "sporcavano" la UI ora vivono qui, al sicuro
    private final Map<String, List<Date>> xDataMap = new HashMap<>();
    private final Map<String, List<Double>> openMap = new HashMap<>();
    private final Map<String, List<Double>> highMap = new HashMap<>();
    private final Map<String, List<Double>> lowMap = new HashMap<>();
    private final Map<String, List<Double>> closeMap = new HashMap<>();

    public void inizializzaSimbolo(String simbolo) {
        xDataMap.putIfAbsent(simbolo, new ArrayList<>());
        openMap.putIfAbsent(simbolo, new ArrayList<>());
        highMap.putIfAbsent(simbolo, new ArrayList<>());
        lowMap.putIfAbsent(simbolo, new ArrayList<>());
        closeMap.putIfAbsent(simbolo, new ArrayList<>());
    }

    public void aggiungiCandela(String simbolo, Date data, double o, double h, double l, double c) {
        inizializzaSimbolo(simbolo);

        xDataMap.get(simbolo).add(data);
        openMap.get(simbolo).add(o);
        highMap.get(simbolo).add(h);
        lowMap.get(simbolo).add(l);
        closeMap.get(simbolo).add(c);

        // La logica delle 100 candele è di competenza del Service, non della UI
        if (xDataMap.get(simbolo).size() > 100) {
            xDataMap.get(simbolo).remove(0);
            openMap.get(simbolo).remove(0);
            highMap.get(simbolo).remove(0);
            lowMap.get(simbolo).remove(0);
            closeMap.get(simbolo).remove(0);
        }
    }

    // Metodo comodissimo per il tasto "BUY/SELL": restituisce l'ultimo prezzo senza far impazzire la UI
    public double getUltimoPrezzo(String simbolo) {
        List<Double> chiusure = closeMap.get(simbolo);
        if (chiusure == null || chiusure.isEmpty()) {
            throw new IllegalStateException("Nessun prezzo disponibile per " + simbolo);
        }
        return chiusure.get(chiusure.size() - 1);
    }

    // Getter che la UI userà SOLO quando dovrà disegnare il grafico
    public List<Date> getDates(String simbolo) { return xDataMap.getOrDefault(simbolo, new ArrayList<>()); }
    public List<Double> getOpens(String simbolo) { return openMap.getOrDefault(simbolo, new ArrayList<>()); }
    public List<Double> getHighs(String simbolo) { return highMap.getOrDefault(simbolo, new ArrayList<>()); }
    public List<Double> getLows(String simbolo) { return lowMap.getOrDefault(simbolo, new ArrayList<>()); }
    public List<Double> getCloses(String simbolo) { return closeMap.getOrDefault(simbolo, new ArrayList<>()); }
}