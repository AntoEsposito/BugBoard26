package com.bugboard26.coreservice.jwt;

/**
 * Interfaccia del servizio JWT per il core-service.
 * A differenza dell'auth-service, questo servizio NON genera token
 * ma valida e legge i token emessi dall'auth-service.
 */
public interface JwtService
{
    /**
     * Estrae l'username (email) dal subject del token.
     */
    String estraiUsername(String token);

    /**
     * Estrae il claim "ruolo" dal token.
     */
    String estraiRuolo(String token);

    /**
     * Verifica che il token sia valido: firma corretta e non scaduto.
     */
    boolean tokenValido(String token);
}
