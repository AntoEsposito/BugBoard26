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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test di unita per CommentoServiceImplementation — metodo aggiungiCommento.
 * Strategia: black-box con partizione in classi di equivalenza.
 */
@ExtendWith(MockitoExtension.class)
class CommentoServiceImplementationTest
{
    @Mock private CommentoRepository commentoRepository;
    @Mock private IssueRepository issueRepository;
    @Mock private UtenteRepository utenteRepository;
    @InjectMocks private CommentoServiceImplementation commentoService;

    private static final UtenteAutenticato ADMIN =
            new UtenteAutenticato("admin@test.com", "ROLE_ADMIN");
    private static final UtenteAutenticato UTENTE =
            new UtenteAutenticato("utente@test.com", "ROLE_UTENTE");

    // ── Helper ──────────────────────────────────────────────────────────

    // Utente e' @Immutable (no setter, no builder), quindi usiamo
    // ReflectionTestUtils di Spring per impostare i campi privati.
    private Utente creaUtente(Integer id, String email, String nome, String cognome)
    {
        Utente u = new Utente();
        ReflectionTestUtils.setField(u, "id", id);
        ReflectionTestUtils.setField(u, "email", email);
        ReflectionTestUtils.setField(u, "nome", nome);
        ReflectionTestUtils.setField(u, "cognome", cognome);
        return u;
    }

    private CreaCommentoRequest creaRequest(String contenuto)
    {
        CreaCommentoRequest req = new CreaCommentoRequest();
        req.setContenuto(contenuto);
        return req;
    }

    // ── Test Case 01: Admin aggiunge commento ────────────────────────────────

    @Test
    @DisplayName("Test Case 01: aggiungiCommento — admin, issue esistente")
    void aggiungiCommento_adminIssueEsistente()
    {
        Utente utenteAdmin = creaUtente(1, "admin@test.com", "Admin", "User");
        when(utenteRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(utenteAdmin));
        when(issueRepository.existsById(10)).thenReturn(true);

        Commento commentoSalvato = Commento.builder()
                .id(100)
                .idIssue(10)
                .idUtenteCreatore(1)
                .contenuto("Commento di test")
                .dataCreazione(OffsetDateTime.now())
                .build();
        when(commentoRepository.save(any(Commento.class))).thenReturn(commentoSalvato);

        CreaCommentoRequest request = creaRequest("Commento di test");

        CommentoResponse response = commentoService.aggiungiCommento(10, request, ADMIN);

        assertThat(response.getId()).isEqualTo(100);
        assertThat(response.getIdIssue()).isEqualTo(10);
        assertThat(response.getIdUtenteCreatore()).isEqualTo(1);
        assertThat(response.getContenuto()).isEqualTo("Commento di test");
        assertThat(response.getDataCreazione()).isNotNull();
        verify(commentoRepository).save(any(Commento.class));
    }

    // ── Test Case 02: Utente assegnato aggiunge commento ─────────────────────

    @Test
    @DisplayName("Test Case 02: aggiungiCommento — utente assegnato")
    void aggiungiCommento_utenteAssegnato()
    {
        Utente utente = creaUtente(2, "utente@test.com", "Mario", "Rossi");
        when(utenteRepository.findByEmail("utente@test.com")).thenReturn(Optional.of(utente));
        when(issueRepository.existsById(10)).thenReturn(true);
        when(issueRepository.existsByIdAndAssegnatari_Id(10, 2)).thenReturn(true);

        Commento commentoSalvato = Commento.builder()
                .id(101)
                .idIssue(10)
                .idUtenteCreatore(2)
                .contenuto("Aggiornamento dal campo")
                .dataCreazione(OffsetDateTime.now())
                .build();
        when(commentoRepository.save(any(Commento.class))).thenReturn(commentoSalvato);

        CreaCommentoRequest request = creaRequest("Aggiornamento dal campo");

        CommentoResponse response = commentoService.aggiungiCommento(10, request, UTENTE);

        assertThat(response.getIdUtenteCreatore()).isEqualTo(2);
        assertThat(response.getContenuto()).isEqualTo("Aggiornamento dal campo");
        verify(commentoRepository).save(any(Commento.class));
    }

    // ── Test Case 03: Utente non assegnato ───────────────────────────────────

    @Test
    @DisplayName("Test Case 03: aggiungiCommento — utente non assegnato")
    void aggiungiCommento_utenteNonAssegnato()
    {
        Utente utente = creaUtente(2, "utente@test.com", "Mario", "Rossi");
        when(utenteRepository.findByEmail("utente@test.com")).thenReturn(Optional.of(utente));
        when(issueRepository.existsById(10)).thenReturn(true);
        when(issueRepository.existsByIdAndAssegnatari_Id(10, 2)).thenReturn(false);

        CreaCommentoRequest request = creaRequest("Tentativo non autorizzato");

        assertThatThrownBy(() -> commentoService.aggiungiCommento(10, request, UTENTE))
                .isInstanceOf(AccesoNegatoException.class);
        verify(commentoRepository, never()).save(any());
    }

    // ── Test Case 04: Issue inesistente ──────────────────────────────────────

    @Test
    @DisplayName("Test Case 04: aggiungiCommento — issue inesistente")
    void aggiungiCommento_issueInesistente()
    {
        Utente utenteAdmin = creaUtente(1, "admin@test.com", "Admin", "User");
        when(utenteRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(utenteAdmin));
        when(issueRepository.existsById(999)).thenReturn(false);

        CreaCommentoRequest request = creaRequest("Commento su issue fantasma");

        assertThatThrownBy(() -> commentoService.aggiungiCommento(999, request, ADMIN))
                .isInstanceOf(RisorsaNonTrovataException.class);
        verify(commentoRepository, never()).save(any());
    }

    // ── Test Case 05: Utente inesistente nel DB ──────────────────────────────

    @Test
    @DisplayName("Test Case 05: aggiungiCommento — email utente inesistente nel DB")
    void aggiungiCommento_utenteInesistente()
    {
        UtenteAutenticato fantasma = new UtenteAutenticato("fantasma@test.com", "ROLE_UTENTE");
        when(utenteRepository.findByEmail("fantasma@test.com")).thenReturn(Optional.empty());

        CreaCommentoRequest request = creaRequest("Commento impossibile");

        assertThatThrownBy(() -> commentoService.aggiungiCommento(10, request, fantasma))
                .isInstanceOf(RisorsaNonTrovataException.class);
        verify(commentoRepository, never()).save(any());
    }
}
