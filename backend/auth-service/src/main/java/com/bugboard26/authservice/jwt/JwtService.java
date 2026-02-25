package com.bugboard26.authservice.jwt;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;

/**
 * Interfaccia del servizio per la gestione dei JSON Web Token (JWT).
 * Definisce le operazioni di generazione, estrazione e validazione dei token.
 */
public interface JwtService
{
    /**
     * Estrae l'username (subject) dal token JWT.
     * L'username corrisponde all'email dell'utente.
     */
    String estraiUsername(String token);

    /**
     * Genera un JWT con claims personalizzati.
     */
    String generaToken(UserDetails userDetails, Map<String, Object> extraClaims);

    /**
     * Valida il token verificando:
     * 1. L'username estratto corrisponde a quello di UserDetails
     * 2. Il token non è scaduto
     */
    boolean tokenValido(String token, UserDetails userDetails);

    /**
     * Ritorna il tempo di scadenza configurato in millisecondi.
     */
    long ottieniScadenzaMs();
}
