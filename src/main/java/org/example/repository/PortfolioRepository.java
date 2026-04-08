package org.example.repository;

import org.example.dto.PosizioneDTO;
import java.util.List;

public interface PortfolioRepository {
    double getSaldoDisponibile(String email);
    List<PosizioneDTO> getPosizioniAttive(String email);
}