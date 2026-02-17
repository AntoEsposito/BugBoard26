package com.bugboard26.authservice.exception;

/**
 * Lanciata quando un utente cercato non esiste nel database.
 * HTTP 404 Not Found.
 */
public class UserNotFoundException extends RuntimeException 
{    
    public UserNotFoundException(String email) {super("Utente non trovato: " + email);}
}