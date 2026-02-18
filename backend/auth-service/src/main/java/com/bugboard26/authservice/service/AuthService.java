package com.bugboard26.authservice.service;

import com.bugboard26.authservice.constants.AuthenticationConstants;
import com.bugboard26.authservice.dto.LoginAnswer;
import com.bugboard26.authservice.dto.LoginRequest;
import com.bugboard26.authservice.entity.User;
import com.bugboard26.authservice.exception.InvalidCredentialsException;
import com.bugboard26.authservice.exception.UserNotFoundException;
import com.bugboard26.authservice.jwt.JwtService;
import com.bugboard26.authservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Servizio che gestisce la logica di autenticazione.
 * 1. Riceve le credenziali
 * 2. Delega la validazione all'AuthenticationManager di Spring Security
 * 3. Se valide, carica l'utente dal DB
 * 4. Genera un token JWT
 * 5. Restituisce la risposta completa (token + dati utente)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService 
{
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    /**
     * Autentica un utente tramite email e password.
     */
    public LoginAnswer login(LoginRequest request) 
    {
        // Validazione credenziali tramite Spring Security AuthenticationManager
        autenticaCredenziali(request.getEmail(), request.getPassword());

        // Caricamento utente dal database
        User utente = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new UserNotFoundException(request.getEmail()));

        // Generazione JWT con claim del ruolo
        String token = jwtService.generaToken(utente, Map.of("ruolo", utente.getRuoloUtente().getNome()));

        log.info("Login effettuato con successo per: {}", request.getEmail());

        // Costruzione risposta
        return LoginAnswer.builder()
                .token(token)
                .tokenType(AuthenticationConstants.BEARER_PREFIX.trim())
                .email(utente.getEmail())
                .nome(utente.getNome())
                .cognome(utente.getCognome())
                .userRole(utente.getRuoloUtente().getNome())
                .expireTime(jwtService.ottieniScadenzaMs())
                .build();
    }

    /**
     * Se le credenziali non sono valide, AuthenticationManager lancia AuthenticationException
     * che viene intercettata e rilanciata come InvalidCredentialsException.
     */
    private void autenticaCredenziali(String email, String password) 
    {
        try 
        {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        } 
        catch (AuthenticationException _) 
        {
            log.warn("Tentativo di login fallito per: {}", email);
            throw new InvalidCredentialsException();
        }
    }
}