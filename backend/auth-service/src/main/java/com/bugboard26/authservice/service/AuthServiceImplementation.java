package com.bugboard26.authservice.service;

import com.bugboard26.authservice.constants.AuthenticationConstants;
import com.bugboard26.authservice.dto.CreaUtenteRequest;
import com.bugboard26.authservice.dto.LoginAnswer;
import com.bugboard26.authservice.dto.LoginRequest;
import com.bugboard26.authservice.dto.UtenteResponse;
import com.bugboard26.authservice.entity.User;
import com.bugboard26.authservice.entity.enums.Ruolo;
import com.bugboard26.authservice.exception.EmailGiaInUsoException;
import com.bugboard26.authservice.exception.InvalidCredentialsException;
import com.bugboard26.authservice.exception.UserNotFoundException;
import com.bugboard26.authservice.jwt.JwtService;
import com.bugboard26.authservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Implementazione concreta del servizio di autenticazione.
 * 1. Riceve le credenziali
 * 2. Delega la validazione all'AuthenticationManager di Spring Security
 * 3. Se valide, carica l'utente dal DB
 * 4. Genera un token JWT
 * 5. Restituisce la risposta completa (token + dati utente)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImplementation implements AuthService
{
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Autentica un utente tramite email e password.
     */
    @Override
    public LoginAnswer login(LoginRequest request)
    {
        // Validazione credenziali tramite Spring Security AuthenticationManager
        autenticaCredenziali(request.getEmail(), request.getPassword());

        // Caricamento utente dal database
        User utente = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException(request.getEmail()));

        // Generazione JWT con claim del ruolo
        String token = jwtService.generaToken(utente, Map.of("ruolo", utente.getRuolo().name()));

        log.info("Login effettuato con successo per: {}", request.getEmail());

        // Costruzione risposta
        return LoginAnswer.builder()
                .token(token)
                .tokenType(AuthenticationConstants.BEARER_PREFIX.trim())
                .email(utente.getEmail())
                .nome(utente.getNome())
                .cognome(utente.getCognome())
                .userRole(utente.getRuolo().name())
                .expireTime(jwtService.ottieniScadenzaMs())
                .build();
    }

    /**
     * Crea un nuovo utente nel sistema.
     * Solo un admin può invocare questo metodo.
     */
    @Override
    public UtenteResponse creaUtente(CreaUtenteRequest request)
    {
        if (userRepository.existsByEmail(request.getEmail()))
            throw new EmailGiaInUsoException(request.getEmail());

        Ruolo ruolo = request.getRuolo();

        User nuovoUtente = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nome(request.getNome())
                .cognome(request.getCognome())
                .ruolo(ruolo)
                .build();

        User salvato = userRepository.save(nuovoUtente);
        log.info("Nuovo utente creato: id={}, email={}, ruolo={}", salvato.getId(), salvato.getEmail(), ruolo.name());

        return UtenteResponse.builder()
                .id(salvato.getId())
                .email(salvato.getEmail())
                .nome(salvato.getNome())
                .cognome(salvato.getCognome())
                .ruolo(ruolo.name())
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
