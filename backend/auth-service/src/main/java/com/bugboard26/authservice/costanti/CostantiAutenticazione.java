package com.bugboard26.authservice.costanti;

// Costanti di sicurezza utilizzate in tutto il modulo di autenticazione.
public final class CostantiAutenticazione
{
    // JWT
    public static final String PREFISSO_BEARER = "Bearer ";
    public static final String HEADER_AUTORIZZAZIONE = "Authorization";
    
    // Ruoli
    public static final String PREFISSO_RUOLO = "ROLE_";
    public static final String RUOLO_UTENTE = "ROLE_UTENTE";
    public static final String RUOLO_ADMIN = "ROLE_ADMIN";
    
    // Endpoint pubblici
    public static final String PATH_AUTENTICAZIONE = "/api/auth/**";
    
    // Costruttore privato per impedire istanziazione
    private CostantiAutenticazione() 
    {
        throw new UnsupportedOperationException("Questa classe non può essere istanziata (non so come hai fatto a provarci).");
    }
}