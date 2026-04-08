package org.example.ui;

import org.example.dto.PosizioneDTO;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class WalletPanel extends JPanel implements WalletView {
    private final JLabel lblSaldo;
    private final JPanel assetContainer;

    public WalletPanel() { // <-- Niente dipendenze nel costruttore!
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(260, 0));
        setBackground(new Color(25, 25, 25));
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY), "IL TUO WALLET",
                0, 0, null, Color.WHITE));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        topPanel.setBackground(new Color(35, 35, 35));

        lblSaldo = new JLabel("Saldo: Caricamento...");
        lblSaldo.setForeground(new Color(255, 230, 0));
        lblSaldo.setFont(new Font("SansSerif", Font.BOLD, 18));
        topPanel.add(lblSaldo);
        add(topPanel, BorderLayout.NORTH);

        assetContainer = new JPanel();
        assetContainer.setLayout(new BoxLayout(assetContainer, BoxLayout.Y_AXIS));
        assetContainer.setBackground(new Color(25, 25, 25));

        JScrollPane scroll = new JScrollPane(assetContainer);
        scroll.setBorder(null);
        add(scroll, BorderLayout.CENTER);
    }

    @Override
    public void mostraSaldo(double saldo) {
        SwingUtilities.invokeLater(() -> {
            lblSaldo.setText("Totale: $" + String.format("%,.2f", saldo));
        });
    }

    @Override
    public void mostraPosizioni(List<PosizioneDTO> posizioni) {
        SwingUtilities.invokeLater(() -> {
            assetContainer.removeAll();
            for (PosizioneDTO pos : posizioni) {
                String labelText = String.format(
                        "<html><div style='color:white; margin:10px; border-bottom:1px solid gray; padding-bottom:5px; width:200px;'>" +
                                "<b style='font-size:14px; color:#26a69a;'>%s</b><br/>" +
                                "Qta: %.4f pz<br/>" +
                                "PMC: $%,.2f</div></html>",
                        pos.getSymbol(), pos.getQuantita(), pos.getPrezzoMedioCarico()
                );
                assetContainer.add(new JLabel(labelText));
            }
            assetContainer.revalidate();
            assetContainer.repaint();
        });
    }

    @Override
    public void mostraErrore(String messaggio) {
        SwingUtilities.invokeLater(() -> {
            assetContainer.removeAll();
            JLabel errorLabel = new JLabel(messaggio);
            errorLabel.setForeground(Color.RED);
            assetContainer.add(errorLabel);
            assetContainer.revalidate();
            assetContainer.repaint();
        });
    }
}