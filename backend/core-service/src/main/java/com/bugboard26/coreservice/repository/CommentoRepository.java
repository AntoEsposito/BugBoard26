package com.bugboard26.coreservice.repository;

import com.bugboard26.coreservice.entity.Commento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentoRepository extends JpaRepository<Commento, Integer> {

    /** Tutti i commenti di una issue, in ordine cronologico. */
    List<Commento> findByIdIssueOrderByDataCreazioneAsc(Integer idIssue);

    /** Cancella tutti i commenti di una issue (usato alla cancellazione della issue). */
    void deleteAllByIdIssue(Integer idIssue);
}
