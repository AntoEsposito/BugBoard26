package com.bugboard26.authservice.exception;

/**
 * Lanciata quando un ruolo richiesto non esiste nel database.
 * Tipicamente durante il DataSeeder o operazioni particolari.
 * HTTP 500 Internal Server Error.
 */
public class RoleNotFoundException extends RuntimeException 
{
    public RoleNotFoundException(String roleName) {super("Ruolo non trovato: " + roleName);}
}