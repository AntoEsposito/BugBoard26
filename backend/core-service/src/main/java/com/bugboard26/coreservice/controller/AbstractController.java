package com.bugboard26.coreservice.controller;

import com.bugboard26.coreservice.constants.CoreConstants;
import com.bugboard26.coreservice.jwt.UtenteAutenticato;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Classe base che fornisce ai controller il metodo per estrarre
 * l'utente autenticato dagli attributi impostati da JwtInterceptor.
 */
public abstract class AbstractController
{
    protected UtenteAutenticato estraiUtente(HttpServletRequest request)
    {
        String email = (String) request.getAttribute(CoreConstants.USER_EMAIL_ATTR);
        String ruolo = (String) request.getAttribute(CoreConstants.USER_RUOLO_ATTR);
        return new UtenteAutenticato(email, ruolo);
    }
}
