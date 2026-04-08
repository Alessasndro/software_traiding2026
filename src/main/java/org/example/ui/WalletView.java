package org.example.ui;

import org.example.dto.PosizioneDTO;
import java.util.List;

public interface WalletView {
    void mostraSaldo(double saldo);
    void mostraPosizioni(List<PosizioneDTO> posizioni);
    void mostraErrore(String messaggio);
}