package com.bugboard26.coreservice.repository;

import com.bugboard26.coreservice.entity.Utente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Repository read-only per la tabella "utenti" gestita dall'auth-service.
 * Non invocare mai save() o delete(): @Immutable su Utente impedisce UPDATE,
 * ma DELETE non è bloccato a livello JPA.
 */
public interface UtenteRepository extends JpaRepository<Utente, Integer> {

    Optional<Utente> findByEmail(String email);

    List<Utente> findAllByIdIn(Collection<Integer> ids);
}
