package org.example.dao.impl;

import org.example.dao.MercatoDAO;
import org.example.database.DatabaseManager;
import org.example.model.StockInfo;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MercatoDAOImpl implements MercatoDAO {

    @Override
    public List<StockInfo> getAllStocks() throws SQLException {
        List<StockInfo> stocks = new ArrayList<>();
        String sql = "SELECT * FROM stock_info";

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                stocks.add(new StockInfo(
                        rs.getInt("id_asset"),
                        rs.getString("symbol"),
                        rs.getString("nome_azienda"),
                        rs.getString("settore")
                ));
            }
        }
        return stocks;
    }

    @Override
    public BigDecimal getPrezzoCorrente(int idAsset) throws SQLException {
        String sql = "SELECT prezzo_corrente FROM stock_live WHERE id_asset = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idAsset);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("prezzo_corrente");
                }
            }
        }
        return BigDecimal.ZERO;
    }
}