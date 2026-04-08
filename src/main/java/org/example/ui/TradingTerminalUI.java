package org.example.ui;

import org.example.service.MarketDataCache;
import org.example.service.TradingService;
import org.knowm.xchart.OHLCChart;
import org.knowm.xchart.OHLCChartBuilder;
import org.knowm.xchart.XChartPanel;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TradingTerminalUI {
    private final String emailUtente;
    private final TradingService tradingService;
    private final MarketDataCache dataCache;
    private final WalletPanel walletPanel; // Riceve il pannello già pronto

    private final Map<String, OHLCChart> charts = new HashMap<>();
    private final Map<String, XChartPanel<OHLCChart>> panels = new HashMap<>();
    private final JFrame frame = new JFrame("Trading Wallet 2026 - Live Terminal");

    public TradingTerminalUI(String emailUtente, TradingService tradingService,
                             MarketDataCache dataCache, WalletPanel walletPanel) {
        this.emailUtente = emailUtente;
        this.tradingService = tradingService;
        this.dataCache = dataCache;
        this.walletPanel = walletPanel; // Iniettato!
    }

    public void inizializzaInterfaccia(List<String> simboli) {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Aggiunge semplicemente il pannello che gli è stato passato
        frame.add(walletPanel, BorderLayout.EAST);

        JPanel chartsContainer = new JPanel(new GridLayout(simboli.size(), 1));

        for (String s : simboli) {
            JPanel container = new JPanel(new BorderLayout());

            OHLCChart chart = new OHLCChartBuilder()
                    .width(800).height(250)
                    .title(s + " / USD")
                    .build();

            // grafica:
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

            // Controlli
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

            // LOGICA BUY
            btnBuy.addActionListener(e -> {
                try {
                    double qta = Double.parseDouble(txtQty.getText().replace(",", "."));
                    double lastPrice = dataCache.getUltimoPrezzo(s);
                    tradingService.eseguiOrdine(emailUtente, s, "BUY", lastPrice, qta);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Inserisci una quantità valida.");
                } catch (IllegalStateException ex) {
                    JOptionPane.showMessageDialog(frame, "Attendi il caricamento del prezzo...");
                }
            });

            // LOGICA SELL
            btnSell.addActionListener(e -> {
                try {
                    double qta = Double.parseDouble(txtQty.getText().replace(",", "."));
                    double lastPrice = dataCache.getUltimoPrezzo(s);
                    tradingService.eseguiOrdine(emailUtente, s, "SELL", lastPrice, qta);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Inserisci una quantità valida.");
                } catch (IllegalStateException ex) {
                    JOptionPane.showMessageDialog(frame, "Attendi il caricamento del prezzo...");
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
        }

        frame.add(chartsContainer, BorderLayout.CENTER);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);
    }

    public void aggiornaGraficoVisivo(String simbolo) {
        SwingUtilities.invokeLater(() -> {
            try {
                OHLCChart chart = charts.get(simbolo);
                if (chart.getSeriesMap().isEmpty()) {
                    chart.addSeries(simbolo, dataCache.getDates(simbolo), dataCache.getOpens(simbolo),
                            dataCache.getHighs(simbolo), dataCache.getLows(simbolo), dataCache.getCloses(simbolo));
                } else {
                    chart.updateOHLCSeries(simbolo, dataCache.getDates(simbolo), dataCache.getOpens(simbolo),
                            dataCache.getHighs(simbolo), dataCache.getLows(simbolo), dataCache.getCloses(simbolo));
                }
                panels.get(simbolo).repaint();
            } catch (Exception e) {
            }
        });
    }
}