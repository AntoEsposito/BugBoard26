package com.bugboard26.auth_service.repository;

import com.bugboard26.auth_service.entity.Utente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UtenteRepository extends JpaRepository<Utente, Integer> {

    Optional<Utente> findByEmail(String email);

    boolean existsByEmail(String email);
}