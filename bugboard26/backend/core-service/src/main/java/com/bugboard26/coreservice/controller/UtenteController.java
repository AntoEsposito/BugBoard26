package com.bugboard26.coreservice.controller;

import com.bugboard26.coreservice.dto.response.UtenteResponse;
import com.bugboard26.coreservice.entity.Utente;
import com.bugboard26.coreservice.repository.UtenteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/utenti")
@RequiredArgsConstructor
public class UtenteController
{
    private final UtenteRepository utenteRepository;

    @GetMapping
    public ResponseEntity<List<UtenteResponse>> ottieniUtenti()
    {
        List<UtenteResponse> utenti = utenteRepository.findAll().stream()
                .map(this::mappaUtente)
                .toList();
        return ResponseEntity.ok(utenti);
    }

    private UtenteResponse mappaUtente(Utente u)
    {
        return UtenteResponse.builder()
                .id(u.getId())
                .email(u.getEmail())
                .nome(u.getNome())
                .cognome(u.getCognome())
                .build();
    }
}
