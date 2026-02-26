package com.bugboard26.coreservice.repository;

import com.bugboard26.coreservice.entity.Commento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentoRepository extends JpaRepository<Commento, Integer> {

    /** Tutti i commenti di una issue, dal più recente al più vecchio (RD-06). */
    List<Commento> findByIdIssueOrderByDataCreazioneDesc(Integer idIssue);

    /** Cancella tutti i commenti di una issue (usato alla cancellazione della issue). */
    void deleteAllByIdIssue(Integer idIssue);

    /** Conta i commenti scritti da un utente (UC-09, prima dell'eliminazione utente). */
    long countByIdUtenteCreatore(Integer idUtenteCreatore);
}
