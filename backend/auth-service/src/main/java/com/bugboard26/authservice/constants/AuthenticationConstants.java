package com.bugboard26.authservice.constants;

// Costanti di sicurezza utilizzate in tutto il modulo di autenticazione.
public final class AuthenticationConstants
{
    // JWT
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    
    // Ruoli
    public static final String ROLE_PREFIX = "ROLE_"; // spring security richiede che i ruoli siano prefissati con "ROLE_"
    public static final String ROLE_UTENTE = "ROLE_UTENTE";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    
    // Endpoint pubblici
    public static final String PUBLIC_PATH = "/api/auth/**";
    
    // Costruttore privato per impedire istanziazione
    private AuthenticationConstants() 
    {
        throw new UnsupportedOperationException("Questa classe non può essere istanziata (non so come hai fatto a provarci).");
    }
}