package com.bugboard26.coreservice.repository;

import com.bugboard26.coreservice.entity.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IssueRepository extends JpaRepository<Issue, Integer> {

    /** Admin — tutte le issue di un progetto, ordinate per data di creazione discendente. */
    List<Issue> findByIdProgettoOrderByDataCreazioneDesc(Integer idProgetto);

    /** Utente normale — issue di un progetto specifico assegnate all'utente. */
    List<Issue> findByIdProgettoAndAssegnatari_IdOrderByDataCreazioneDesc(Integer idProgetto, Integer idUtente);

    /** Bulk — tutte le issue assegnate a un utente su tutti i progetti. */
    List<Issue> findByAssegnatari_Id(Integer idUtente);

    /** Verifica se una issue è assegnata a un utente (controllo permessi UC-05, UC-06, UC-07). */
    boolean existsByIdAndAssegnatari_Id(Integer id, Integer idUtente);

    /** Verifica se un utente ha creato almeno una issue (UC-09, prima dell'eliminazione utente). */
    boolean existsByIdUtenteCreatore(Integer idUtenteCreatore);

    /** Dettaglio issue con assegnatari in un'unica query per evitare N+1 (UC-07). */
    @Query("SELECT i FROM Issue i LEFT JOIN FETCH i.assegnatari WHERE i.id = :issueId")
    Optional<Issue> findByIdConDettagli(@Param("issueId") Integer issueId);
}
