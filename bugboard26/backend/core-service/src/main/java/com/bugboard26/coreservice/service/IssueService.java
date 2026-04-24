package com.bugboard26.coreservice.service;

import com.bugboard26.coreservice.dto.request.CreaIssueRequest;
import com.bugboard26.coreservice.dto.request.ModificaIssueRequest;
import com.bugboard26.coreservice.dto.response.IssueDettaglioResponse;
import com.bugboard26.coreservice.dto.response.IssueRiepilogoResponse;
import com.bugboard26.coreservice.jwt.UtenteAutenticato;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IssueService {

    /**
     * Issue di un progetto al click sul nome del progetto.
     * Admin: tutte le issue del progetto.
     * Utente: solo le issue a lui assegnate in quel progetto.
     */
    List<IssueRiepilogoResponse> ottieniIssuePerProgetto(Integer idProgetto, UtenteAutenticato utenteCorrente);

    /**
     * Crea una nuova issue nel progetto specificato.
     * Il creatore viene aggiunto automaticamente agli assegnatari.
     * Stato iniziale: TO-DO. Priorità iniziale: LOW.
     * @param immagine file opzionale da allegare alla issue
     */
    IssueRiepilogoResponse creaIssue(CreaIssueRequest request, MultipartFile immagine, UtenteAutenticato utenteCorrente);

    /**
     * Dettaglio di una issue con assegnatari e commenti.
     * Admin: accesso libero. Utente: solo se assegnato.
     */
    IssueDettaglioResponse ottieniDettaglioIssue(Integer idIssue, UtenteAutenticato utenteCorrente);

    /**
     * Aggiorna una issue esistente.
     * Utente: tutti i campi, ma solo delle issue a lui assegnate.
     * Admin: tutti i campi di qualsiasi issue.
     * @param immagine nuova immagine opzionale; se null lascia invariata quella esistente
     */
    IssueRiepilogoResponse modificaIssue(Integer idIssue, ModificaIssueRequest request, MultipartFile immagine, UtenteAutenticato utenteCorrente);
}
