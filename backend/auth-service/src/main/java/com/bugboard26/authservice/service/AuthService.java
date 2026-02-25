package com.bugboard26.authservice.service;

import com.bugboard26.authservice.dto.LoginAnswer;
import com.bugboard26.authservice.dto.LoginRequest;

/**
 * Interfaccia del servizio di autenticazione.
 */
public interface AuthService
{
    /**
     * Autentica un utente tramite email e password.
     */
    LoginAnswer login(LoginRequest request);
}
