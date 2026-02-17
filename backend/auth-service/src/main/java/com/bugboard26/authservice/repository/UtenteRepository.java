package com.bugboard26.authservice.repository;

import com.bugboard26.authservice.entity.Utente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UtenteRepository extends JpaRepository<Utente, Integer> 
{
    Optional<Utente> findByEmail(String email);

    boolean existsByEmail(String email);
}