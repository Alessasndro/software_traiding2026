package org.example.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class AnalisiService {
    public BigDecimal calcolaSMA(List<BigDecimal> prezzi, int periodo) {
        if (prezzi.size() < periodo) return prezzi.isEmpty() ? BigDecimal.ZERO : prezzi.get(prezzi.size()-1);
        BigDecimal somma = BigDecimal.ZERO;
        for (int i = prezzi.size() - periodo; i < prezzi.size(); i++) {
            somma = somma.add(prezzi.get(i));
        }
        return somma.divide(new BigDecimal(periodo), 4, RoundingMode.HALF_UP);
    }
}