package org.example.model;
import java.math.BigDecimal;
import java.sql.Timestamp;

public record PrezzoStorico(int idAsset, BigDecimal prezzo, Timestamp timestamp) {}