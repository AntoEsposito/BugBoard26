package com.bugboard26.auth_service.repository;

import com.bugboard26.auth_service.entity.Ruolo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RuoloRepository extends JpaRepository<Ruolo, Integer> {

    Optional<Ruolo> findByNome(String nome);
}