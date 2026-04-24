package com.bugboard26.coreservice.jwt;

/**
 * Contiene i claim estratti da un JWT valido.
 * Usato da JwtInterceptor per popolare gli attributi della richiesta.
 */
public record ClaimsUtente(String email, String ruolo) {}
