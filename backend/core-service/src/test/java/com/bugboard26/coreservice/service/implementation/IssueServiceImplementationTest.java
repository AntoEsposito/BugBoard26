package com.bugboard26.coreservice.service.implementation;

import com.bugboard26.coreservice.dto.request.CreaIssueRequest;
import com.bugboard26.coreservice.dto.request.ModificaIssueRequest;
import com.bugboard26.coreservice.dto.response.IssueRiepilogoResponse;
import com.bugboard26.coreservice.entity.Issue;
import com.bugboard26.coreservice.entity.Utente;
import com.bugboard26.coreservice.entity.enums.PrioritaIssue;
import com.bugboard26.coreservice.entity.enums.StatoIssue;
import com.bugboard26.coreservice.entity.enums.TipoIssue;
import com.bugboard26.coreservice.exception.AccesoNegatoException;
import com.bugboard26.coreservice.exception.RisorsaNonTrovataException;
import com.bugboard26.coreservice.jwt.UtenteAutenticato;
import com.bugboard26.coreservice.repository.CommentoRepository;
import com.bugboard26.coreservice.repository.IssueRepository;
import com.bugboard26.coreservice.repository.ProgettoRepository;
import com.bugboard26.coreservice.repository.UtenteRepository;
import com.bugboard26.coreservice.service.ImageStorageService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test di unita per IssueServiceImplementation — metodo modificaIssue.
 * Strategia: black-box con partizione in classi di equivalenza.
 */
@ExtendWith(MockitoExtension.class)
class IssueServiceImplementationTest
{
    @Mock private IssueRepository issueRepository;
    @Mock private ProgettoRepository progettoRepository;
    @Mock private UtenteRepository utenteRepository;
    @Mock private CommentoRepository commentoRepository;
    @Mock private ImageStorageService imageStorageService;
    @InjectMocks private IssueServiceImplementation issueService;

    private static final UtenteAutenticato ADMIN =
            new UtenteAutenticato("admin@test.com", "ROLE_ADMIN");
    private static final UtenteAutenticato UTENTE =
            new UtenteAutenticato("utente@test.com", "ROLE_UTENTE");


    private Utente creaUtente(Integer id, String email, String nome, String cognome)
    {
        Utente u = new Utente();
        ReflectionTestUtils.setField(u, "id", id);
        ReflectionTestUtils.setField(u, "email", email);
        ReflectionTestUtils.setField(u, "nome", nome);
        ReflectionTestUtils.setField(u, "cognome", cognome);
        return u;
    }

    private Issue creaIssueEsistente()
    {
        Utente assegnato = creaUtente(2, "utente@test.com", "Utente", "Test");
        HashSet<Utente> assegnatari = new HashSet<>();
        assegnatari.add(assegnato);

        return Issue.builder()
                .id(1)
                .idProgetto(10)
                .idUtenteCreatore(2)
                .titolo("Issue iniziale")
                .stato(StatoIssue.TODO)
                .tipo(TipoIssue.BUG)
                .priorita(PrioritaIssue.LOW)
                .descrizione("Descrizione iniziale")
                .immaginePath("/api/uploads/old.jpg")
                .assegnatari(assegnatari)
                .build();
    }

    // ── Test Case 01: Admin modifica solo descrizione ────────────────────────

    @Test
    @DisplayName("Test Case 01: modificaIssue — admin modifica solo descrizione")
    void modificaIssue_adminModificaSoloDescrizione()
    {
        Issue issue = creaIssueEsistente();
        when(issueRepository.findById(1)).thenReturn(Optional.of(issue));
        when(issueRepository.save(any(Issue.class))).thenReturn(issue);

        ModificaIssueRequest request = new ModificaIssueRequest();
        request.setDescrizione("Nuova descrizione aggiornata");

        IssueRiepilogoResponse response = issueService.modificaIssue(1, request, null, ADMIN);

        assertThat(response.getDescrizione()).isEqualTo("Nuova descrizione aggiornata");
        assertThat(response.getStato()).isEqualTo(StatoIssue.TODO);
        assertThat(response.getPriorita()).isEqualTo(PrioritaIssue.LOW);
        verify(issueRepository).save(any(Issue.class));
    }

    // ── Test Case 02: Utente assegnato modifica stato e priorita ─────────────

    @Test
    @DisplayName("Test Case 02: modificaIssue — utente assegnato modifica stato e priorita")
    void modificaIssue_utenteAssegnatoModificaStatoEPriorita()
    {
        Issue issue = creaIssueEsistente();
        Utente utente = creaUtente(2, "utente@test.com", "Utente", "Test");

        when(issueRepository.findById(1)).thenReturn(Optional.of(issue));
        when(utenteRepository.findByEmail("utente@test.com")).thenReturn(Optional.of(utente));
        when(issueRepository.existsByIdAndAssegnatari_Id(1, 2)).thenReturn(true);
        when(issueRepository.save(any(Issue.class))).thenReturn(issue);

        ModificaIssueRequest request = new ModificaIssueRequest();
        request.setStato(StatoIssue.IN_PROGRESS);
        request.setPriorita(PrioritaIssue.HIGH);

        IssueRiepilogoResponse response = issueService.modificaIssue(1, request, null, UTENTE);

        assertThat(response.getStato()).isEqualTo(StatoIssue.IN_PROGRESS);
        assertThat(response.getPriorita()).isEqualTo(PrioritaIssue.HIGH);
        assertThat(response.getDescrizione()).isEqualTo("Descrizione iniziale");
    }

    // ── Test Case 03: Issue inesistente ──────────────────────────────────────

    @Test
    @DisplayName("Test Case 03: modificaIssue — issue inesistente")
    void modificaIssue_issueInesistente()
    {
        when(issueRepository.findById(999)).thenReturn(Optional.empty());
        ModificaIssueRequest request = new ModificaIssueRequest();

        assertThatThrownBy(() -> issueService.modificaIssue(999, request, null, ADMIN))
                .isInstanceOf(RisorsaNonTrovataException.class);
    }

    // ── Test Case 04: Utente non assegnato ───────────────────────────────────

    @Test
    @DisplayName("Test Case 04: modificaIssue — utente non assegnato")
    void modificaIssue_utenteNonAssegnato()
    {
        Issue issue = creaIssueEsistente();
        Utente utenteEstraneo = creaUtente(99, "estraneo@test.com", "Utente", "Estraneo");
        UtenteAutenticato autenticatoEstraneo = new UtenteAutenticato("estraneo@test.com", "ROLE_UTENTE");

        when(issueRepository.findById(1)).thenReturn(Optional.of(issue));
        when(utenteRepository.findByEmail("estraneo@test.com")).thenReturn(Optional.of(utenteEstraneo));
        when(issueRepository.existsByIdAndAssegnatari_Id(1, 99)).thenReturn(false);

        ModificaIssueRequest request = new ModificaIssueRequest();
        request.setDescrizione("Tentativo non autorizzato");

        assertThatThrownBy(() -> issueService.modificaIssue(1, request, null, autenticatoEstraneo))
                .isInstanceOf(AccesoNegatoException.class);
    }

    // ── Test Case 05: Admin aggiorna assegnatari validi ──────────────────────

    @Test
    @DisplayName("Test Case 05: modificaIssue — admin aggiorna assegnatari con utenti validi")
    void modificaIssue_adminAggiornaAssegnatariValidi()
    {
        Issue issue = creaIssueEsistente();
        Utente utente2 = creaUtente(2, "mario@test.com", "Mario", "Rossi");
        Utente utente3 = creaUtente(3, "anna@test.com", "Anna", "Bianchi");

        when(issueRepository.findById(1)).thenReturn(Optional.of(issue));
        when(utenteRepository.findAllByIdIn(Set.of(2, 3))).thenReturn(List.of(utente2, utente3));
        when(issueRepository.save(any(Issue.class))).thenReturn(issue);

        ModificaIssueRequest request = new ModificaIssueRequest();
        request.setIdAssegnatari(Set.of(2, 3));

        issueService.modificaIssue(1, request, null, ADMIN);

        assertThat(issue.getAssegnatari()).containsExactlyInAnyOrder(utente2, utente3);
        verify(issueRepository).save(issue);
    }

    // ── Test Case 06: Non-admin ignora assegnatari ───────────────────────────

    @Test
    @DisplayName("Test Case 06: modificaIssue — non-admin invia idAssegnatari, vengono ignorati")
    void modificaIssue_nonAdminIgnoraAssegnatari()
    {
        Issue issue = creaIssueEsistente();
        Set<Utente> assegnatariOriginali = new HashSet<>(issue.getAssegnatari());
        Utente utente = creaUtente(2, "utente@test.com", "Utente", "Test");

        when(issueRepository.findById(1)).thenReturn(Optional.of(issue));
        when(utenteRepository.findByEmail("utente@test.com")).thenReturn(Optional.of(utente));
        when(issueRepository.existsByIdAndAssegnatari_Id(1, 2)).thenReturn(true);
        when(issueRepository.save(any(Issue.class))).thenReturn(issue);

        ModificaIssueRequest request = new ModificaIssueRequest();
        request.setIdAssegnatari(Set.of(2, 3, 4));

        issueService.modificaIssue(1, request, null, UTENTE);

        assertThat(issue.getAssegnatari()).isEqualTo(assegnatariOriginali);
        verify(issueRepository).save(issue);
        verify(utenteRepository, never()).findAllByIdIn(any());
    }

    // ── Test Case 07: Admin assegna utente inesistente ───────────────────────

    @Test
    @DisplayName("Test Case 07: modificaIssue — admin assegna utente inesistente")
    void modificaIssue_adminAssegnatarioInesistente()
    {
        Issue issue = creaIssueEsistente();
        Utente utente2 = creaUtente(2, "mario@test.com", "Mario", "Rossi");

        when(issueRepository.findById(1)).thenReturn(Optional.of(issue));
        when(utenteRepository.findAllByIdIn(Set.of(2, 999))).thenReturn(List.of(utente2));

        ModificaIssueRequest request = new ModificaIssueRequest();
        request.setIdAssegnatari(Set.of(2, 999));

        assertThatThrownBy(() -> issueService.modificaIssue(1, request, null, ADMIN))
                .isInstanceOf(RisorsaNonTrovataException.class);
    }

    // ── Test Case 08: Upload nuova immagine ──────────────────────────────────

    @Test
    @DisplayName("Test Case 08: modificaIssue — upload nuova immagine, vecchia eliminata")
    void modificaIssue_uploadNuovaImmagine()
    {
        Issue issue = creaIssueEsistente();
        when(issueRepository.findById(1)).thenReturn(Optional.of(issue));
        when(issueRepository.save(any(Issue.class))).thenReturn(issue);

        MultipartFile nuovaImmagine = mock(MultipartFile.class);
        when(nuovaImmagine.isEmpty()).thenReturn(false);
        when(imageStorageService.salva(nuovaImmagine)).thenReturn("/api/uploads/new-uuid.png");

        ModificaIssueRequest request = new ModificaIssueRequest();

        IssueRiepilogoResponse response = issueService.modificaIssue(1, request, nuovaImmagine, ADMIN);

        verify(imageStorageService).elimina("/api/uploads/old.jpg");
        verify(imageStorageService).salva(nuovaImmagine);
        assertThat(response.getImmaginePath()).isEqualTo("/api/uploads/new-uuid.png");
    }

    // ── Test Case 09: rimuoviImmagine=true senza file ────────────────────────

    @Test
    @DisplayName("Test Case 09: modificaIssue — rimuoviImmagine=true senza nuova immagine")
    void modificaIssue_rimuoviImmagineSenzaFile()
    {
        Issue issue = creaIssueEsistente();
        when(issueRepository.findById(1)).thenReturn(Optional.of(issue));
        when(issueRepository.save(any(Issue.class))).thenReturn(issue);

        ModificaIssueRequest request = new ModificaIssueRequest();
        request.setRimuoviImmagine(true);

        IssueRiepilogoResponse response = issueService.modificaIssue(1, request, null, ADMIN);

        verify(imageStorageService).elimina("/api/uploads/old.jpg");
        verify(imageStorageService, never()).salva(any());
        assertThat(response.getImmaginePath()).isNull();
    }

    // ── Test Case 10: Nuova immagine + rimuoviImmagine=true ──────────────────

    @Test
    @DisplayName("Test Case 10: modificaIssue — nuova immagine + rimuoviImmagine=true, upload ha precedenza")
    void modificaIssue_nuovaImmagineConFlagRimuovi()
    {
        Issue issue = creaIssueEsistente();
        when(issueRepository.findById(1)).thenReturn(Optional.of(issue));
        when(issueRepository.save(any(Issue.class))).thenReturn(issue);

        MultipartFile nuovaImmagine = mock(MultipartFile.class);
        when(nuovaImmagine.isEmpty()).thenReturn(false);
        when(imageStorageService.salva(nuovaImmagine)).thenReturn("/api/uploads/replaced.png");

        ModificaIssueRequest request = new ModificaIssueRequest();
        request.setRimuoviImmagine(true);

        IssueRiepilogoResponse response = issueService.modificaIssue(1, request, nuovaImmagine, ADMIN);

        verify(imageStorageService).elimina("/api/uploads/old.jpg");
        verify(imageStorageService).salva(nuovaImmagine);
        assertThat(response.getImmaginePath()).isEqualTo("/api/uploads/replaced.png");
    }

    // ── TC-CI-01: creaIssue — progetto esistente, no immagine, priorita null ─

    @Test
    @DisplayName("TC-CI-01: creaIssue — progetto esistente, no immagine, priorita null")
    void creaIssue_progettoEsistenteSenzaImmaginePrioritaNull()
    {
        // Arrange
        Utente utente = creaUtente(1, "utente@test.com", "Utente", "Test");
        when(utenteRepository.findByEmail("utente@test.com")).thenReturn(Optional.of(utente));
        when(progettoRepository.existsById(10)).thenReturn(true);

        CreaIssueRequest request = new CreaIssueRequest();
        request.setIdProgetto(10);
        request.setTitolo("Bug critico");
        request.setTipo(TipoIssue.BUG);
        request.setDescrizione("Descrizione del bug");
        request.setPriorita(null);

        ArgumentCaptor<Issue> issueCaptor = ArgumentCaptor.forClass(Issue.class);
        Issue issueSalvata = Issue.builder()
                .id(100)
                .idProgetto(10)
                .titolo("Bug critico")
                .stato(StatoIssue.TODO)
                .tipo(TipoIssue.BUG)
                .priorita(PrioritaIssue.LOW)
                .descrizione("Descrizione del bug")
                .assegnatari(new HashSet<>(Set.of(utente)))
                .build();
        when(issueRepository.save(any(Issue.class))).thenReturn(issueSalvata);

        // Act
        IssueRiepilogoResponse response = issueService.creaIssue(request, null, UTENTE);

        // Assert
        assertThat(response.getStato()).isEqualTo(StatoIssue.TODO);
        assertThat(response.getPriorita()).isEqualTo(PrioritaIssue.LOW);

        verify(issueRepository).save(issueCaptor.capture());
        assertThat(issueCaptor.getValue().getAssegnatari()).contains(utente);

        verify(imageStorageService, never()).salva(any());
    }

    // ── Test Case 11: Request vuota ──────────────────────────────────────────

    @Test
    @DisplayName("Test Case 11: modificaIssue — request completamente vuota, issue invariata")
    void modificaIssue_requestVuota()
    {
        Issue issue = creaIssueEsistente();
        when(issueRepository.findById(1)).thenReturn(Optional.of(issue));
        when(issueRepository.save(any(Issue.class))).thenReturn(issue);

        ModificaIssueRequest request = new ModificaIssueRequest();

        IssueRiepilogoResponse response = issueService.modificaIssue(1, request, null, ADMIN);

        assertThat(response.getDescrizione()).isEqualTo("Descrizione iniziale");
        assertThat(response.getStato()).isEqualTo(StatoIssue.TODO);
        assertThat(response.getPriorita()).isEqualTo(PrioritaIssue.LOW);
        assertThat(response.getImmaginePath()).isEqualTo("/api/uploads/old.jpg");
        verify(imageStorageService, never()).elimina(any());
        verify(imageStorageService, never()).salva(any());
        verify(utenteRepository, never()).findAllByIdIn(any());
    }
}
