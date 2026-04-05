package org.example.ui;

import org.example.database.DatabaseManager;
import org.example.service.PortfolioObserver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class WalletPanel extends JPanel implements PortfolioObserver {
    private final String emailCorrente;
    private final JLabel lblSaldo;
    private final JPanel assetContainer;

    public WalletPanel(String emailCorrente) {
        this.emailCorrente = emailCorrente;

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(260, 0));
        setBackground(new Color(25, 25, 25));
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY), "IL TUO WALLET", 0, 0, null, Color.WHITE));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        topPanel.setBackground(new Color(35, 35, 35));

        lblSaldo = new JLabel("Saldo: Loading...");
        lblSaldo.setForeground(new Color(255, 215, 0));
        lblSaldo.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblSaldo.setCursor(new Cursor(Cursor.HAND_CURSOR));

        lblSaldo.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                apriFinestraDettaglioWallet();
            }
        });

        topPanel.add(lblSaldo);
        add(topPanel, BorderLayout.NORTH);

        assetContainer = new JPanel();
        assetContainer.setLayout(new BoxLayout(assetContainer, BoxLayout.Y_AXIS));
        assetContainer.setBackground(new Color(25, 25, 25));

        JScrollPane scroll = new JScrollPane(assetContainer);
        scroll.setBorder(null);
        add(scroll, BorderLayout.CENTER);

        onPortfolioChanged();
    }

    private void apriFinestraDettaglioWallet() {
        JFrame walletFrame = new JFrame("Dettaglio Wallet");
        walletFrame.setSize(400, 300);
        walletFrame.setLocationRelativeTo(this);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(new Color(30, 30, 30));

        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "SELECT saldo_disponibile FROM portafoglio p JOIN utenti u ON p.id_utente = u.id_utente WHERE u.email = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, emailCorrente);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                JLabel s = new JLabel("Saldo Disponibile: $" + String.format("%,.2f", rs.getDouble("saldo_disponibile")));
                s.setForeground(Color.WHITE);
                s.setFont(new Font("SansSerif", Font.BOLD, 22));
                s.setAlignmentX(Component.CENTER_ALIGNMENT);
                content.add(Box.createVerticalStrut(30));
                content.add(s);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        walletFrame.add(content);
        walletFrame.setVisible(true);
    }

    @Override
    public void onPortfolioChanged() {
        SwingUtilities.invokeLater(() -> {
            assetContainer.removeAll();

            try (Connection conn = DatabaseManager.getConnection()) {
                String sqlSaldo = "SELECT saldo_disponibile FROM portafoglio p JOIN utenti u ON p.id_utente = u.id_utente WHERE u.email = ?";
                PreparedStatement ps1 = conn.prepareStatement(sqlSaldo);
                ps1.setString(1, emailCorrente);
                ResultSet rs1 = ps1.executeQuery();
                if (rs1.next()) {
                    lblSaldo.setText("Totale: $" + String.format("%,.2f", rs1.getDouble("saldo_disponibile")));
                }

                String sqlAssets = "SELECT s.symbol, p.quantita, p.prezzo_medio_carico FROM posizioni p " +
                        "JOIN stock_info s ON p.id_asset = s.id_asset " +
                        "JOIN portafoglio port ON p.id_portafoglio = port.id_portafoglio " +
                        "JOIN utenti u ON port.id_utente = u.id_utente WHERE u.email = ? AND p.quantita > 0";
                PreparedStatement ps2 = conn.prepareStatement(sqlAssets);
                ps2.setString(1, emailCorrente);
                ResultSet rs2 = ps2.executeQuery();

                while (rs2.next()) {
                    String labelText = String.format(
                            "<html><div style='color:white; margin:10px; border-bottom:1px solid gray; padding-bottom:5px; width:200px;'>" +
                                    "<b style='font-size:14px; color:#26a69a;'>%s</b><br/>" +
                                    "Qta: %.4f pz<br/>" +
                                    "PMC: $%,.2f</div></html>",
                            rs2.getString("symbol"),
                            rs2.getDouble("quantita"),
                            rs2.getDouble("prezzo_medio_carico")
                    );
                    assetContainer.add(new JLabel(labelText));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            assetContainer.revalidate();
            assetContainer.repaint();
        });
    }
}