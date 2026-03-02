package com.bugboard26.coreservice.service;

import com.bugboard26.coreservice.dto.response.ProgettoResponse;
import com.bugboard26.coreservice.dto.response.UtenteResponse;
import com.bugboard26.coreservice.jwt.UtenteAutenticato;

import java.util.List;

public interface ProgettoService {

    /**
     * Restituisce la lista dei progetti visibili all'utente.
     * Admin: tutti i progetti.
     * Utente: solo i progetti in cui ha almeno una issue assegnata.
     */
    List<ProgettoResponse> ottieniProgetti(UtenteAutenticato utenteCorrente);

    /**
     * Restituisce i membri di un progetto.
     * Usato dall'admin per popolare la checklist degli assegnatari in fase di modifica issue.
     */
    List<UtenteResponse> ottieniMembri(Integer idProgetto);
}
