package com.bugboard26.coreservice.service.implementation;

import com.bugboard26.coreservice.dto.response.ProgettoResponse;
import com.bugboard26.coreservice.dto.response.UtenteResponse;
import com.bugboard26.coreservice.entity.Progetto;
import com.bugboard26.coreservice.entity.Utente;
import com.bugboard26.coreservice.exception.AccesoNegatoException;
import com.bugboard26.coreservice.exception.RisorsaNonTrovataException;
import com.bugboard26.coreservice.jwt.UtenteAutenticato;
import com.bugboard26.coreservice.repository.IssueRepository;
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
    private final IssueRepository issueRepository;

    @Override
    public List<ProgettoResponse> ottieniProgetti(UtenteAutenticato utenteCorrente) 
    {
        if (utenteCorrente.isAdmin()) 
            return progettoRepository.findAll().stream().map(this::mappaProgetto).toList();

        else
        {
            Utente utente = utenteRepository.findByEmail(utenteCorrente.email())
                    .orElseThrow(() -> new RisorsaNonTrovataException("Utente non trovato: " + utenteCorrente.email()));

            return progettoRepository.findProgettiConIssueAssegnateA(utente.getId()).stream().map(this::mappaProgetto).toList();
        }
    }

    @Override
    public List<UtenteResponse> ottieniMembri(Integer idProgetto, UtenteAutenticato utenteCorrente)
    {
        Progetto progetto = progettoRepository.findByIdConMembri(idProgetto)
                .orElseThrow(() -> new RisorsaNonTrovataException("Progetto non trovato: id=" + idProgetto));

        if (!utenteCorrente.isAdmin())
        {
            Utente utente = utenteRepository.findByEmail(utenteCorrente.email())
                    .orElseThrow(() -> new RisorsaNonTrovataException("Utente non trovato: " + utenteCorrente.email()));
            if (!issueRepository.existsByIdProgettoAndAssegnatari_Id(idProgetto, utente.getId()))
                throw new AccesoNegatoException("Non hai accesso al progetto con id " + idProgetto);
        }

        return progetto.getMembri().stream()
                .map(u -> UtenteResponse.builder()
                        .id(u.getId())
                        .email(u.getEmail())
                        .nome(u.getNome())
                        .cognome(u.getCognome())
                        .build())
                .toList();
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
