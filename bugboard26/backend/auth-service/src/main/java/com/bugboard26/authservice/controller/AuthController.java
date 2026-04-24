package com.bugboard26.authservice.controller;

import com.bugboard26.authservice.dto.CreaUtenteRequest;
import com.bugboard26.authservice.dto.LoginAnswer;
import com.bugboard26.authservice.dto.LoginRequest;
import com.bugboard26.authservice.dto.UtenteResponse;
import com.bugboard26.authservice.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller REST per l'autenticazione.
 * POST /api/auth/login  — pubblico
 * POST /api/auth/utenti — richiede ROLE_ADMIN
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController 
{
    private final AuthService authService;

    /**
     * Endpoint di login.
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<LoginAnswer> login(@Valid @RequestBody LoginRequest request)
    {
        log.debug("Richiesta di login ricevuta per: {}", request.getEmail());
        LoginAnswer risposta = authService.login(request);
        return ResponseEntity.ok(risposta);
    }

    /**
     * Crea un nuovo utente. Richiede ROLE_ADMIN.
     * POST /api/auth/utenti
     */
    @PostMapping("/utenti")
    public ResponseEntity<UtenteResponse> creaUtente(@Valid @RequestBody CreaUtenteRequest request)
    {
        log.debug("Richiesta creazione utente: {}", request.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.creaUtente(request));
    }
}