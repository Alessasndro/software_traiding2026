package org.example.service;

import org.knowm.xchart.OHLCChart;
import org.knowm.xchart.OHLCChartBuilder;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.style.Styler;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class CandelaService {
    private final Map<String, OHLCChart> charts = new HashMap<>();
    private final Map<String, XChartPanel<OHLCChart>> panels = new HashMap<>();
    private final JFrame frame = new JFrame("Trading Wallet 2026 - Live Terminal");

    private final Map<String, List<Date>> xDataMap = new HashMap<>();
    private final Map<String, List<Double>> openMap = new HashMap<>();
    private final Map<String, List<Double>> highMap = new HashMap<>();
    private final Map<String, List<Double>> lowMap = new HashMap<>();
    private final Map<String, List<Double>> closeMap = new HashMap<>();

    // AGGIORNATO: Ora accetta List e String (Email)
    public void inizializzaGrafici(List<String> simboli, String emailUtente) {
        TradingService trading = new TradingService();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(simboli.size(), 1));

        for (String s : simboli) {
            JPanel container = new JPanel(new BorderLayout());

            OHLCChart chart = new OHLCChartBuilder()
                    .width(1000).height(250)
                    .title(s + " / USD")
                    .build();

            chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNE);
            chart.getStyler().setToolTipsEnabled(true);

            XChartPanel<OHLCChart> chartPanel = new XChartPanel<>(chart);

            // Pannello di Controllo Operativo
            JPanel controlPanel = new JPanel();
            JButton btnBuy = new JButton("BUY " + s);
            JButton btnSell = new JButton("SELL " + s);
            btnBuy.setBackground(new Color(34, 139, 34));
            btnBuy.setForeground(Color.WHITE);
            btnSell.setBackground(new Color(178, 34, 34));
            btnSell.setForeground(Color.WHITE);

            // Azione BUY
            btnBuy.addActionListener(e -> {
                if (!closeMap.get(s).isEmpty()) {
                    double lastPrice = closeMap.get(s).get(closeMap.get(s).size() - 1);
                    trading.eseguiOrdine(emailUtente, s, "BUY", lastPrice);
                    JOptionPane.showMessageDialog(frame, "Ordine BUY inviato per " + s + " a $" + lastPrice);
                }
            });

            // Azione SELL
            btnSell.addActionListener(e -> {
                if (!closeMap.get(s).isEmpty()) {
                    double lastPrice = closeMap.get(s).get(closeMap.get(s).size() - 1);
                    trading.eseguiOrdine(emailUtente, s, "SELL", lastPrice);
                    JOptionPane.showMessageDialog(frame, "Ordine SELL inviato per " + s + " a $" + lastPrice);
                }
            });

            controlPanel.add(btnBuy);
            controlPanel.add(btnSell);

            container.add(chartPanel, BorderLayout.CENTER);
            container.add(controlPanel, BorderLayout.SOUTH);

            charts.put(s, chart);
            panels.put(s, chartPanel);
            frame.add(container);

            xDataMap.put(s, new ArrayList<>());
            openMap.put(s, new ArrayList<>());
            highMap.put(s, new ArrayList<>());
            lowMap.put(s, new ArrayList<>());
            closeMap.put(s, new ArrayList<>());
        }

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public void aggiungiCandela(String simbolo, Date data, double o, double h, double l, double c) {
        xDataMap.get(simbolo).add(data);
        openMap.get(simbolo).add(o);
        highMap.get(simbolo).add(h);
        lowMap.get(simbolo).add(l);
        closeMap.get(simbolo).add(c);

        if (xDataMap.get(simbolo).size() > 50) {
            xDataMap.get(simbolo).remove(0);
            openMap.get(simbolo).remove(0);
            highMap.get(simbolo).remove(0);
            lowMap.get(simbolo).remove(0);
            closeMap.get(simbolo).remove(0);
        }

        SwingUtilities.invokeLater(() -> {
            try {
                OHLCChart chart = charts.get(simbolo);
                if (chart.getSeriesMap().isEmpty()) {
                    chart.addSeries(simbolo, xDataMap.get(simbolo), openMap.get(simbolo), highMap.get(simbolo), lowMap.get(simbolo), closeMap.get(simbolo));
                } else {
                    chart.updateOHLCSeries(simbolo, xDataMap.get(simbolo), openMap.get(simbolo), highMap.get(simbolo), lowMap.get(simbolo), closeMap.get(simbolo));
                }
                panels.get(simbolo).repaint();
            } catch (Exception e) {}
        });
    }
}