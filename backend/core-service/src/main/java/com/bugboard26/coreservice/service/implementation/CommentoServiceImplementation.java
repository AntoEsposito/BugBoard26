package com.bugboard26.coreservice.service.implementation;

import com.bugboard26.coreservice.dto.request.CreaCommentoRequest;
import com.bugboard26.coreservice.dto.response.CommentoResponse;
import com.bugboard26.coreservice.entity.Commento;
import com.bugboard26.coreservice.entity.Utente;
import com.bugboard26.coreservice.exception.AccesoNegatoException;
import com.bugboard26.coreservice.exception.RisorsaNonTrovataException;
import com.bugboard26.coreservice.jwt.UtenteAutenticato;
import com.bugboard26.coreservice.repository.CommentoRepository;
import com.bugboard26.coreservice.repository.IssueRepository;
import com.bugboard26.coreservice.repository.UtenteRepository;
import com.bugboard26.coreservice.service.CommentoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CommentoServiceImplementation implements CommentoService 
{
    private final CommentoRepository commentoRepository;
    private final IssueRepository issueRepository;
    private final UtenteRepository utenteRepository;

    @Override
    public CommentoResponse aggiungiCommento(Integer idIssue, CreaCommentoRequest request, UtenteAutenticato utenteCorrente) 
    {
        Utente utente = utenteRepository.findByEmail(utenteCorrente.email())
                .orElseThrow(() -> new RisorsaNonTrovataException("Utente non trovato: " + utenteCorrente.email()));

        if (!issueRepository.existsById(idIssue)) 
            throw new RisorsaNonTrovataException("Issue non trovata: id=" + idIssue);

        if (!utenteCorrente.isAdmin() && !issueRepository.existsByIdAndAssegnatari_Id(idIssue, utente.getId())) 
            throw new AccesoNegatoException("Non hai accesso alla issue con id " + idIssue);

        Commento commento = Commento.builder()
                .idIssue(idIssue)
                .idUtenteCreatore(utente.getId())
                .contenuto(request.getContenuto())
                .build();

        Commento salvato = commentoRepository.save(commento);
        log.info("Commento aggiunto: id={}, issue={}, utente={}", salvato.getId(), idIssue, utente.getId());

        return CommentoResponse.builder()
                .id(salvato.getId())
                .idIssue(salvato.getIdIssue())
                .idUtenteCreatore(salvato.getIdUtenteCreatore())
                .contenuto(salvato.getContenuto())
                .dataCreazione(salvato.getDataCreazione())
                .build();
    }
}
