package org.example.dao;
import org.example.model.StockInfo;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public interface MercatoDAO {
    List<StockInfo> getAllStocks() throws SQLException;
    BigDecimal getPrezzoCorrente(int idAsset) throws SQLException;
}