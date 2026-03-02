package com.bugboard26.coreservice.service;

import com.bugboard26.coreservice.dto.request.CreaCommentoRequest;
import com.bugboard26.coreservice.dto.response.CommentoResponse;
import com.bugboard26.coreservice.jwt.UtenteAutenticato;

public interface CommentoService {

    /**
     * Aggiunge un commento a una issue esistente.
     * Admin: accesso libero. Utente: solo se assegnato alla issue.
     */
    CommentoResponse aggiungiCommento(Integer idIssue, CreaCommentoRequest request, UtenteAutenticato utenteCorrente);
}
