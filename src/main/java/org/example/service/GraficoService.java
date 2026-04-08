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

        chart.getStyler().setYAxisLogarithmic(false);

        for (String s : simboli) {
            xDataMap.put(s, new ArrayList<>());
            yDataMap.put(s, new ArrayList<>());

            chart.addSeries(s, new double[]{0}, new double[]{1});
        }

        sw = new SwingWrapper<>(chart);
        sw.displayChart();
    }

    public void aggiorna(String simbolo, double tempo, double prezzo) {
        List<Double> xData = xDataMap.get(simbolo);
        List<Double> yData = yDataMap.get(simbolo);

        if (xData != null && yData != null) {
            if (xData.size() == 0) {
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