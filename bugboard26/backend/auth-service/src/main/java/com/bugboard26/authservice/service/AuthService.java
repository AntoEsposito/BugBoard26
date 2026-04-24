package com.bugboard26.authservice.service;

import com.bugboard26.authservice.dto.CreaUtenteRequest;
import com.bugboard26.authservice.dto.LoginAnswer;
import com.bugboard26.authservice.dto.LoginRequest;
import com.bugboard26.authservice.dto.UtenteResponse;

public interface AuthService
{
    /**
     * Autentica un utente tramite email e password.
     */
    LoginAnswer login(LoginRequest request);

    /**
     * Crea un nuovo utente (solo admin).
     */
    UtenteResponse creaUtente(CreaUtenteRequest request);
}
