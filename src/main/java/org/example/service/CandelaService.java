package org.example.service;

import org.example.ui.WalletPanel;
import org.knowm.xchart.OHLCChart;
import org.knowm.xchart.OHLCChartBuilder;
import org.knowm.xchart.XChartPanel;

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

    public void inizializzaGrafici(List<String> simboli, String emailUtente) {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        WalletPanel sidebarWallet = new WalletPanel(emailUtente);
        frame.add(sidebarWallet, BorderLayout.EAST);

        TradingService trading = new TradingService();
        trading.setObserver(sidebarWallet);

        JPanel chartsContainer = new JPanel(new GridLayout(simboli.size(), 1));

        for (String s : simboli) {
            JPanel container = new JPanel(new BorderLayout());

            OHLCChart chart = new OHLCChartBuilder()
                    .width(800).height(250)
                    .title(s + " / USD")
                    .build();

            chart.getStyler().setPlotBackgroundColor(new Color(30, 30, 30));
            chart.getStyler().setChartBackgroundColor(new Color(45, 45, 45));
            chart.getStyler().setChartFontColor(Color.WHITE);
            chart.getStyler().setPlotGridLinesColor(new Color(60, 60, 60));
            chart.getStyler().setLegendVisible(false);
            chart.getStyler().setAxisTickLabelsColor(Color.LIGHT_GRAY);
            chart.getStyler().setSeriesColors(new Color[]{new Color(38, 166, 154)});
            chart.getStyler().setToolTipsEnabled(true);
            chart.getStyler().setYAxisDecimalPattern("$ #,###.00");

            XChartPanel<OHLCChart> chartPanel = new XChartPanel<>(chart);

            JPanel controlPanel = new JPanel();
            controlPanel.setBackground(new Color(45, 45, 45));

            JLabel lblQty = new JLabel("Quantità:");
            lblQty.setForeground(Color.WHITE);
            JTextField txtQty = new JTextField("0.1", 5);

            JButton btnBuy = new JButton("BUY " + s);
            JButton btnSell = new JButton("SELL " + s);

            btnBuy.setBackground(new Color(38, 166, 154));
            btnBuy.setForeground(Color.WHITE);
            btnSell.setBackground(new Color(239, 83, 80));
            btnSell.setForeground(Color.WHITE);

            btnBuy.addActionListener(e -> {
                if (!closeMap.get(s).isEmpty()) {
                    try {
                        double qta = Double.parseDouble(txtQty.getText().replace(",", "."));
                        double lastPrice = closeMap.get(s).get(closeMap.get(s).size() - 1);
                        trading.eseguiOrdine(emailUtente, s, "BUY", lastPrice, qta);
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(frame, "Inserisci una quantità numerica valida (es. 0.15)");
                    }
                }
            });

            btnSell.addActionListener(e -> {
                if (!closeMap.get(s).isEmpty()) {
                    try {
                        double qta = Double.parseDouble(txtQty.getText().replace(",", "."));
                        double lastPrice = closeMap.get(s).get(closeMap.get(s).size() - 1);
                        trading.eseguiOrdine(emailUtente, s, "SELL", lastPrice, qta);
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(frame, "Inserisci una quantità numerica valida (es. 0.15)");
                    }
                }
            });

            controlPanel.add(lblQty);
            controlPanel.add(txtQty);
            controlPanel.add(btnBuy);
            controlPanel.add(btnSell);

            container.add(chartPanel, BorderLayout.CENTER);
            container.add(controlPanel, BorderLayout.SOUTH);

            charts.put(s, chart);
            panels.put(s, chartPanel);
            chartsContainer.add(container);

            xDataMap.put(s, new ArrayList<>());
            openMap.put(s, new ArrayList<>());
            highMap.put(s, new ArrayList<>());
            lowMap.put(s, new ArrayList<>());
            closeMap.put(s, new ArrayList<>());
        }

        frame.add(chartsContainer, BorderLayout.CENTER);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);
    }

    public void aggiungiCandela(String simbolo, Date data, double o, double h, double l, double c) {
        xDataMap.get(simbolo).add(data);
        openMap.get(simbolo).add(o);
        highMap.get(simbolo).add(h);
        lowMap.get(simbolo).add(l);
        closeMap.get(simbolo).add(c);

        if (xDataMap.get(simbolo).size() > 100) {
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