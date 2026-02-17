package com.bugboard26.authservice.repository;

import com.bugboard26.authservice.entity.RuoloUtente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RuoloRepository extends JpaRepository<RuoloUtente, Integer> 
{
    Optional<RuoloUtente> findByNome(String nome);
}