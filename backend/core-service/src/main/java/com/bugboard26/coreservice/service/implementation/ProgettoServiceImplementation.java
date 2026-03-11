package com.bugboard26.coreservice.service.implementation;

import com.bugboard26.coreservice.dto.response.ProgettoResponse;
import com.bugboard26.coreservice.dto.response.UtenteResponse;
import com.bugboard26.coreservice.entity.Progetto;
import com.bugboard26.coreservice.entity.Utente;
import com.bugboard26.coreservice.exception.AccesoNegatoException;
import com.bugboard26.coreservice.exception.OperazioneNonConsentitaException;
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
        Utente utente = trovaUtentePerEmail(utenteCorrente.email());

        return trovaProgetti(utente, utenteCorrente)
                .stream().map(this::mappaProgetto).toList();
    }

    @Override
    public List<UtenteResponse> ottieniMembri(Integer idProgetto, UtenteAutenticato utenteCorrente)
    {
        Progetto progetto = trovaProgettoConMembri(idProgetto);
        verificaAccessoProgetto(idProgetto, utenteCorrente);
        return mappaMembri(progetto);
    }

    @Override
    @Transactional
    public List<UtenteResponse> aggiungiMembri(Integer idProgetto, List<Integer> idUtenti, UtenteAutenticato utenteCorrente)
    {
        verificaAdmin(utenteCorrente);
        Progetto progetto = trovaProgettoConMembri(idProgetto);
        List<Utente> utenti = trovaUtentiPerIds(idUtenti);

        progetto.getMembri().addAll(utenti);
        progettoRepository.save(progetto);

        log.info("Aggiunti {} membri al progetto id={}", utenti.size(), idProgetto);
        return mappaMembri(progetto);
    }

    @Override
    @Transactional
    public void rimuoviMembri(Integer idProgetto, List<Integer> idUtenti, UtenteAutenticato utenteCorrente)
    {
        verificaAdmin(utenteCorrente);
        Progetto progetto = trovaProgettoConMembri(idProgetto);
        List<Utente> utenti = trovaUtentiPerIds(idUtenti);

        List<Integer> conIssue = issueRepository.findAssegnatariConIssueInProgetto(idProgetto, idUtenti);
        if (!conIssue.isEmpty())
            throw new OperazioneNonConsentitaException("Impossibile rimuovere utenti con issue assegnate nel progetto. ID utenti bloccati: " + conIssue);

        progetto.getMembri().removeAll(utenti);
        progettoRepository.save(progetto);

        log.info("Rimossi {} membri dal progetto id={}", utenti.size(), idProgetto);
    }

    // ── Verifiche di autorizzazione

    private void verificaAdmin(UtenteAutenticato utenteCorrente)
    {
        if (!utenteCorrente.isAdmin())
            throw new AccesoNegatoException("Solo un admin può eseguire questa operazione");
    }

    private void verificaAccessoProgetto(Integer idProgetto, UtenteAutenticato utenteCorrente)
    {
        if (utenteCorrente.isAdmin()) return;

        Utente utente = trovaUtentePerEmail(utenteCorrente.email());
        if (!issueRepository.existsByIdProgettoAndAssegnatari_Id(idProgetto, utente.getId()))
            throw new AccesoNegatoException("Non hai accesso al progetto con id " + idProgetto);
    }

    // ── Query condizionali per ruolo

    private List<Progetto> trovaProgetti(Utente utente, UtenteAutenticato utenteCorrente)
    {
        if (utenteCorrente.isAdmin())
            return progettoRepository.findAll();

        return progettoRepository.findProgettiConIssueAssegnateA(utente.getId());
    }

    // ── Utility

    private Progetto trovaProgettoConMembri(Integer idProgetto)
    {
        return progettoRepository.findByIdConMembri(idProgetto)
                .orElseThrow(() -> new RisorsaNonTrovataException("Progetto non trovato: id=" + idProgetto));
    }

    private Utente trovaUtentePerEmail(String email)
    {
        return utenteRepository.findByEmail(email)
                .orElseThrow(() -> new RisorsaNonTrovataException("Utente non trovato: " + email));
    }

    private List<Utente> trovaUtentiPerIds(List<Integer> idUtenti)
    {
        List<Utente> utenti = utenteRepository.findAllByIdIn(idUtenti);
        if (utenti.size() != new HashSet<>(idUtenti).size())
        {
            Set<Integer> trovati = utenti.stream().map(Utente::getId).collect(Collectors.toSet());
            List<Integer> mancanti = idUtenti.stream().filter(id -> !trovati.contains(id)).toList();
            throw new RisorsaNonTrovataException("Utenti non trovati: id=" + mancanti);
        }
        return utenti;
    }

    private List<UtenteResponse> mappaMembri(Progetto progetto)
    {
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
