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
     * Admin: accesso libero. Utente: solo se ha almeno una issue assegnata nel progetto.
     */
    List<UtenteResponse> ottieniMembri(Integer idProgetto, UtenteAutenticato utenteCorrente);

    /** Aggiunge membri a un progetto. Solo admin. */
    List<UtenteResponse> aggiungiMembri(Integer idProgetto, List<Integer> idUtenti, UtenteAutenticato utenteCorrente);

    /** Rimuove membri da un progetto. Solo admin. Blocca la rimozione se l'utente ha issue assegnate nel progetto. */
    void rimuoviMembri(Integer idProgetto, List<Integer> idUtenti, UtenteAutenticato utenteCorrente);
}
