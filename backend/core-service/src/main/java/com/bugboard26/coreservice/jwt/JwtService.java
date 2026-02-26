package com.bugboard26.coreservice.jwt;

import io.jsonwebtoken.JwtException;

/**
 * Interfaccia del servizio JWT per il core-service.
 * A differenza dell'auth-service, questo servizio NON genera token:
 * valida e legge i token emessi dall'auth-service in un'unica operazione.
 */
public interface JwtService
{
    /**
     * Valida il token (firma + scadenza) ed estrae email e ruolo in un solo parse.
     * Lancia JwtException se il token è assente, malformato o scaduto.
     */
    ClaimsUtente validaEOttieniClaims(String token) throws JwtException;
}
