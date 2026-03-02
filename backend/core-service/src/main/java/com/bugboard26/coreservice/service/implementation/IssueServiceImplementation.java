package com.bugboard26.coreservice.service.implementation;

import com.bugboard26.coreservice.dto.request.CreaIssueRequest;
import com.bugboard26.coreservice.dto.request.ModificaIssueRequest;
import com.bugboard26.coreservice.dto.response.CommentoResponse;
import com.bugboard26.coreservice.dto.response.IssueDettaglioResponse;
import com.bugboard26.coreservice.dto.response.IssueRiepilogoResponse;
import com.bugboard26.coreservice.dto.response.UtenteResponse;
import com.bugboard26.coreservice.entity.Commento;
import com.bugboard26.coreservice.entity.Issue;
import com.bugboard26.coreservice.entity.Utente;
import com.bugboard26.coreservice.entity.enums.PrioritaIssue;
import com.bugboard26.coreservice.entity.enums.StatoIssue;
import com.bugboard26.coreservice.exception.AccesoNegatoException;
import com.bugboard26.coreservice.exception.RisorsaNonTrovataException;
import com.bugboard26.coreservice.jwt.UtenteAutenticato;
import com.bugboard26.coreservice.repository.CommentoRepository;
import com.bugboard26.coreservice.repository.IssueRepository;
import com.bugboard26.coreservice.repository.ProgettoRepository;
import com.bugboard26.coreservice.repository.UtenteRepository;
import com.bugboard26.coreservice.service.IssueService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class IssueServiceImplementation implements IssueService 
{
    private final IssueRepository issueRepository;
    private final ProgettoRepository progettoRepository;
    private final UtenteRepository utenteRepository;
    private final CommentoRepository commentoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<IssueRiepilogoResponse> ottieniIssuePerProgetto(Integer idProgetto, UtenteAutenticato utenteCorrente)
    {
        if (utenteCorrente.isAdmin())
            return issueRepository.findByIdProgettoOrderByDataCreazioneDesc(idProgetto).stream().map(this::mappaIssueRiepilogo).toList();

        Utente utente = trovaUtentePerEmail(utenteCorrente.email());
        return issueRepository.findByIdProgettoAndAssegnatari_IdOrderByDataCreazioneDesc(idProgetto, utente.getId())
            .stream().map(this::mappaIssueRiepilogo).toList();
    }

    @Override
    public IssueRiepilogoResponse creaIssue(CreaIssueRequest request, UtenteAutenticato utenteCorrente) 
    {
        Utente utente = trovaUtentePerEmail(utenteCorrente.email());

        if (!progettoRepository.existsByIdAndMembri_Id(request.getIdProgetto(), utente.getId()))
            throw new AccesoNegatoException("Non sei membro del progetto con id " + request.getIdProgetto());

        HashSet<Utente> assegnatari = new HashSet<>();
        assegnatari.add(utente);

        Issue nuovaIssue = Issue.builder()
                .idProgetto(request.getIdProgetto())
                .idUtenteCreatore(utente.getId())
                .titolo(request.getTitolo())
                .tipo(request.getTipo())
                .stato(StatoIssue.TODO)
                .priorita(PrioritaIssue.LOW)
                .descrizione(request.getDescrizione())
                .assegnatari(assegnatari)
                .build();

        Issue salvata = issueRepository.save(nuovaIssue);
        log.info("Issue creata: id={}, progetto={}, creatore={}", salvata.getId(), request.getIdProgetto(), utente.getId());
        return mappaIssueRiepilogo(salvata);
    }

    @Override
    @Transactional(readOnly = true)
    public IssueDettaglioResponse ottieniDettaglioIssue(Integer idIssue, UtenteAutenticato utenteCorrente) 
    {
        Utente utente = trovaUtentePerEmail(utenteCorrente.email());

        if (!utenteCorrente.isAdmin() && !issueRepository.existsByIdAndAssegnatari_Id(idIssue, utente.getId())) 
            throw new AccesoNegatoException("Non hai accesso alla issue con id " + idIssue);

        Issue issue = issueRepository.findByIdConDettagli(idIssue).orElseThrow(() -> new RisorsaNonTrovataException("Issue non trovata: id=" + idIssue));

        List<Commento> commenti = commentoRepository.findByIdIssueOrderByDataCreazioneDesc(idIssue);

        return IssueDettaglioResponse.builder()
                .id(issue.getId())
                .idProgetto(issue.getIdProgetto())
                .idUtenteCreatore(issue.getIdUtenteCreatore())
                .titolo(issue.getTitolo())
                .stato(issue.getStato())
                .tipo(issue.getTipo())
                .priorita(issue.getPriorita())
                .descrizione(issue.getDescrizione())
                .dataCreazione(issue.getDataCreazione())
                .dataUltimaModifica(issue.getDataUltimaModifica())

                .assegnatari(issue.getAssegnatari().stream()
                        .map(u -> UtenteResponse.builder()
                                .id(u.getId())
                                .email(u.getEmail())
                                .nome(u.getNome())
                                .cognome(u.getCognome())
                                .build())
                        .toList())

                .commenti(commenti.stream()
                        .map(c -> CommentoResponse.builder()
                                .id(c.getId())
                                .idIssue(c.getIdIssue())
                                .idUtenteCreatore(c.getIdUtenteCreatore())
                                .contenuto(c.getContenuto())
                                .tipo(c.getTipo())
                                .dataCreazione(c.getDataCreazione())
                                .build())
                        .toList())
                .build();
    }

    @Override
    public IssueRiepilogoResponse modificaIssue(Integer idIssue, ModificaIssueRequest request, UtenteAutenticato utenteCorrente)
    {
        Issue issue = issueRepository.findById(idIssue)
                .orElseThrow(() -> new RisorsaNonTrovataException("Issue non trovata: id=" + idIssue));

        verificaPermessiModifica(idIssue, utenteCorrente);

        if (request.getDescrizione() != null) issue.setDescrizione(request.getDescrizione());
        if (request.getStato() != null) issue.setStato(request.getStato());

        if (utenteCorrente.isAdmin()) applicaCampiAdmin(issue, request);

        Issue aggiornata = issueRepository.save(issue);
        log.info("Issue modificata: id={}", idIssue);
        return mappaIssueRiepilogo(aggiornata);
    }

    private void verificaPermessiModifica(Integer idIssue, UtenteAutenticato utenteCorrente)
    {
        if (utenteCorrente.isAdmin()) return;

        Utente utente = trovaUtentePerEmail(utenteCorrente.email());
        if (!issueRepository.existsByIdAndAssegnatari_Id(idIssue, utente.getId()))
            throw new AccesoNegatoException("Non hai accesso alla issue con id " + idIssue);
    }

    private void applicaCampiAdmin(Issue issue, ModificaIssueRequest request)
    {
        if (request.getPriorita() != null) issue.setPriorita(request.getPriorita());
        if (request.getIdAssegnatari() != null && !request.getIdAssegnatari().isEmpty())
            issue.setAssegnatari(new HashSet<>(utenteRepository.findAllByIdIn(request.getIdAssegnatari())));
    }

    private Utente trovaUtentePerEmail(String email) 
    {
        return utenteRepository.findByEmail(email).orElseThrow(() -> new RisorsaNonTrovataException("Utente non trovato: " + email));
    }

    private IssueRiepilogoResponse mappaIssueRiepilogo(Issue i) 
    {
        return IssueRiepilogoResponse.builder()
                .id(i.getId())
                .idProgetto(i.getIdProgetto())
                .idUtenteCreatore(i.getIdUtenteCreatore())
                .titolo(i.getTitolo())
                .stato(i.getStato())
                .tipo(i.getTipo())
                .priorita(i.getPriorita())
                .descrizione(i.getDescrizione())
                .dataCreazione(i.getDataCreazione())
                .dataUltimaModifica(i.getDataUltimaModifica())
                .build();
    }
}
