package com.bugboard26.coreservice.repository;

import com.bugboard26.coreservice.entity.Issue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IssueRepository extends JpaRepository<Issue, Integer> {

    /** Admin — tutte le issue di un progetto, ordinate per data di creazione discendente. */
    List<Issue> findByIdProgettoOrderByDataCreazioneDesc(Integer idProgetto);

    /** Utente normale — issue di un progetto specifico assegnate all'utente. */
    List<Issue> findByIdProgettoAndAssegnatari_IdOrderByDataCreazioneDesc(Integer idProgetto, Integer idUtente);

    /** Bulk — tutte le issue assegnate a un utente su tutti i progetti. */
    List<Issue> findByAssegnatari_Id(Integer idUtente);
}
