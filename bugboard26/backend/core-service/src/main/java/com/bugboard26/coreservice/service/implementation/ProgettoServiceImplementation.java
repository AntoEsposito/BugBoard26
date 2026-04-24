package com.bugboard26.coreservice.service.implementation;

import com.bugboard26.coreservice.dto.response.ProgettoResponse;
import com.bugboard26.coreservice.entity.Progetto;
import com.bugboard26.coreservice.entity.Utente;
import com.bugboard26.coreservice.exception.RisorsaNonTrovataException;
import com.bugboard26.coreservice.jwt.UtenteAutenticato;
import com.bugboard26.coreservice.repository.ProgettoRepository;
import com.bugboard26.coreservice.repository.UtenteRepository;
import com.bugboard26.coreservice.service.ProgettoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProgettoServiceImplementation implements ProgettoService
{
    private final ProgettoRepository progettoRepository;
    private final UtenteRepository utenteRepository;

    @Override
    public List<ProgettoResponse> ottieniProgetti(UtenteAutenticato utenteCorrente)
    {
        Utente utente = trovaUtentePerEmail(utenteCorrente.email());

        return trovaProgetti(utente, utenteCorrente)
                .stream().map(this::mappaProgetto).toList();
    }

    // ── Query condizionali per ruolo

    private List<Progetto> trovaProgetti(Utente utente, UtenteAutenticato utenteCorrente)
    {
        if (utenteCorrente.isAdmin())
            return progettoRepository.findAll();

        return progettoRepository.findProgettiConIssueAssegnateA(utente.getId());
    }

    // ── Utility

    private Utente trovaUtentePerEmail(String email)
    {
        return utenteRepository.findByEmail(email)
                .orElseThrow(() -> new RisorsaNonTrovataException("Utente non trovato: " + email));
    }

    private ProgettoResponse mappaProgetto(Progetto p)
    {
        return ProgettoResponse.builder()
                .id(p.getId())
                .nome(p.getNome())
                .descrizione(p.getDescrizione())
                .build();
    }
}
