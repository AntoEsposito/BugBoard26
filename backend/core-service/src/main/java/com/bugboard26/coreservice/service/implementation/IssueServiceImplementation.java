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
import com.bugboard26.coreservice.service.ImageStorageService;
import com.bugboard26.coreservice.service.IssueService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
    private final ImageStorageService imageStorageService;

    @Override
    @Transactional(readOnly = true)
    public List<IssueRiepilogoResponse> ottieniIssuePerProgetto(Integer idProgetto, UtenteAutenticato utenteCorrente)
    {
        verificaEsistenzaProgetto(idProgetto);
        Utente utente = trovaUtentePerEmail(utenteCorrente.email());

        return trovaIssuePerProgetto(idProgetto, utente, utenteCorrente)
                .stream().map(this::mappaIssueRiepilogo).toList();
    }

    @Override
    public IssueRiepilogoResponse creaIssue(CreaIssueRequest request, MultipartFile immagine, UtenteAutenticato utenteCorrente)
    {
        Utente utente = trovaUtentePerEmail(utenteCorrente.email());
        verificaEsistenzaProgetto(request.getIdProgetto());

        HashSet<Utente> assegnatari = new HashSet<>();
        assegnatari.add(utente);

        String immaginePath = (immagine != null && !immagine.isEmpty()) ? imageStorageService.salva(immagine) : null;

        Issue nuovaIssue = Issue.builder()
                .idProgetto(request.getIdProgetto())
                .idUtenteCreatore(utente.getId())
                .titolo(request.getTitolo())
                .tipo(request.getTipo())
                .stato(StatoIssue.TODO)
                .priorita(request.getPriorita() != null ? request.getPriorita() : PrioritaIssue.LOW)
                .descrizione(request.getDescrizione())
                .immaginePath(immaginePath)
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
        Issue issue = issueRepository.findByIdConDettagli(idIssue)
                .orElseThrow(() -> new RisorsaNonTrovataException("Issue non trovata: id=" + idIssue));

        verificaAccessoIssue(idIssue, utenteCorrente);

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
                .immaginePath(issue.getImmaginePath())
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
                                .dataCreazione(c.getDataCreazione())
                                .build())
                        .toList())
                .build();
    }

    @Override
    public IssueRiepilogoResponse modificaIssue(Integer idIssue, ModificaIssueRequest request, MultipartFile immagine, UtenteAutenticato utenteCorrente)
    {
        Issue issue = issueRepository.findById(idIssue)
                .orElseThrow(() -> new RisorsaNonTrovataException("Issue non trovata: id=" + idIssue));

        verificaAccessoIssue(idIssue, utenteCorrente);

        if (request.getDescrizione() != null) issue.setDescrizione(request.getDescrizione());
        if (request.getStato() != null) issue.setStato(request.getStato());
        if (request.getPriorita() != null) issue.setPriorita(request.getPriorita());
        if (request.getTipo() != null) issue.setTipo(request.getTipo());
        if (utenteCorrente.isAdmin() && request.getIdAssegnatari() != null && !request.getIdAssegnatari().isEmpty())
        {
            List<Utente> trovati = utenteRepository.findAllByIdIn(request.getIdAssegnatari());
            if (trovati.size() != request.getIdAssegnatari().size())
                throw new RisorsaNonTrovataException("Uno o più utenti negli assegnatari non esistono");
            issue.setAssegnatari(new HashSet<>(trovati));
        }
        if (immagine != null && !immagine.isEmpty())
        {
            imageStorageService.elimina(issue.getImmaginePath());
            issue.setImmaginePath(imageStorageService.salva(immagine));
        }
        else if (request.isRimuoviImmagine())
        {
            imageStorageService.elimina(issue.getImmaginePath());
            issue.setImmaginePath(null);
        }

        Issue aggiornata = issueRepository.save(issue);
        log.info("Issue modificata: id={}", idIssue);
        return mappaIssueRiepilogo(aggiornata);
    }

    // ── Verifiche di autorizzazione

    private void verificaEsistenzaProgetto(Integer idProgetto)
    {
        if (!progettoRepository.existsById(idProgetto))
            throw new RisorsaNonTrovataException("Progetto non trovato: id=" + idProgetto);
    }

    private void verificaAccessoIssue(Integer idIssue, UtenteAutenticato utenteCorrente)
    {
        if (utenteCorrente.isAdmin()) return;

        Utente utente = trovaUtentePerEmail(utenteCorrente.email());
        if (!issueRepository.existsByIdAndAssegnatari_Id(idIssue, utente.getId()))
            throw new AccesoNegatoException("Non hai accesso alla issue con id " + idIssue);
    }

    // ── Query condizionali per ruolo

    private List<Issue> trovaIssuePerProgetto(Integer idProgetto, Utente utente, UtenteAutenticato utenteCorrente)
    {
        if (utenteCorrente.isAdmin())
            return issueRepository.findByIdProgettoOrderByDataCreazioneDesc(idProgetto);

        return issueRepository.findByIdProgettoAndAssegnatari_IdOrderByDataCreazioneDesc(idProgetto, utente.getId());
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
                .immaginePath(i.getImmaginePath())
                .dataCreazione(i.getDataCreazione())
                .dataUltimaModifica(i.getDataUltimaModifica())
                .build();
    }
}
