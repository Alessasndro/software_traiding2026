package org.example.service;

import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.style.Styler;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import javax.swing.SwingUtilities;

public class GraficoService {
    private XYChart chart;
    private SwingWrapper<XYChart> sw;
    private final Map<String, List<Double>> xDataMap = new HashMap<>();
    private final Map<String, List<Double>> yDataMap = new HashMap<>();

    public void inizializzaGrafico(List<String> simboli) {
        chart = new XYChartBuilder()
                .width(1000).height(700)
                .title("Trading Live")
                .xAxisTitle("Tempo")
                .yAxisTitle("Prezzo (USD)")
                .build();

        chart.getStyler().setLegendPosition(Styler.LegendPosition.OutsideS);
        chart.getStyler().setMarkerSize(0);

        // DISABILITIAMO il logaritmo per evitare l'errore "less or equal to zero"
        chart.getStyler().setYAxisLogarithmic(false);

        for (String s : simboli) {
            xDataMap.put(s, new ArrayList<>());
            yDataMap.put(s, new ArrayList<>());

            // Inizializziamo con 1.0 invece di 0.0 per sicurezza
            chart.addSeries(s, new double[]{0}, new double[]{1});
        }

        sw = new SwingWrapper<>(chart);
        sw.displayChart();
    }

    public void aggiorna(String simbolo, double tempo, double prezzo) {
        List<Double> xData = xDataMap.get(simbolo);
        List<Double> yData = yDataMap.get(simbolo);

        if (xData != null && yData != null) {
            // Se è il primo vero dato, puliamo il "1" iniziale
            if (xData.size() == 0) {
                // Rimuoviamo il punto di inizializzazione se necessario o gestiamo l'update
            }

            xData.add(tempo);
            yData.add(prezzo);

            SwingUtilities.invokeLater(() -> {
                try {
                    chart.updateXYSeries(simbolo, xData, yData, null);
                    sw.repaintChart();
                } catch (Exception e) {}
            });
        }
    }
}