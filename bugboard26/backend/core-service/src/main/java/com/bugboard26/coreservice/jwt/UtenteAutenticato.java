package com.bugboard26.coreservice.jwt;

import com.bugboard26.coreservice.constants.CoreConstants;

/**
 * Rappresenta l'identità dell'utente autenticato estratta dal JWT.
 * Costruita dal controller a partire dagli attributi di request impostati da JwtInterceptor.
 */
public record UtenteAutenticato(String email, String ruolo) 
{
    public boolean isAdmin() 
    {
        return CoreConstants.ROLE_ADMIN.equals(ruolo);
    }
}
