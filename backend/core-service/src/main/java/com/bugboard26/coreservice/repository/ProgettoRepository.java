package com.bugboard26.coreservice.repository;

import com.bugboard26.coreservice.entity.Progetto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProgettoRepository extends JpaRepository<Progetto, Integer> {

    boolean existsByNome(String nome);

    /** Tutti i progetti in cui l'utente è membro (via join table progetti_membri). */
    List<Progetto> findByMembri_Id(Integer idUtente);

    /**
     * Dashboard utente normale: progetti distinti che contengono almeno una issue assegnata all'utente.
     * Usa subquery JPQL perché Issue non ha @ManyToOne su Progetto.
     */
    @Query("SELECT DISTINCT p FROM Progetto p WHERE p.id IN (SELECT i.idProgetto FROM Issue i JOIN i.assegnatari a WHERE a.id = :idUtente)")
    List<Progetto> findProgettiConIssueAssegnateA(@Param("idUtente") Integer idUtente);
}
