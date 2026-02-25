package com.bugboard26.coreservice.constants;

// Costanti condivise in tutto il modulo core.
public final class CoreConstants
{
    // JWT header
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String AUTHORIZATION_HEADER = "Authorization";

    // Ruoli (devono corrispondere ai valori emessi dall'auth-service)
    public static final String ROLE_PREFIX = "ROLE_"; // Spring Security richiede che i ruoli siano prefissati con "ROLE_"
    public static final String ROLE_UTENTE = ROLE_PREFIX + "UTENTE";
    public static final String ROLE_ADMIN  = ROLE_PREFIX + "ADMIN";

    // Chiavi degli attributi di richiesta impostati da JwtInterceptor
    public static final String USER_EMAIL_ATTR = "userEmail";
    public static final String USER_RUOLO_ATTR = "userRuolo";

    private CoreConstants()
    {
        throw new UnsupportedOperationException("Classe non istanziabile.");
    }
}
