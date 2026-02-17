package com.bugboard26.authservice.exception;

/**
 * Lanciata quando le credenziali fornite (email/password) non sono valide.
 * HTTP 401 Unauthorized.
 */
public class InvalidCredentialsException extends RuntimeException 
{
    public InvalidCredentialsException() {super("Credenziali non valide");}
}