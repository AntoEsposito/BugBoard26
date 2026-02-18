package com.bugboard26.authservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;
import java.util.stream.Collectors;

/**
 * Gestore globale delle eccezioni per l'auth-service, al posto di gestire le eccezioni nei singoli componenti con try-catch.
 * Restituisce risposte JSON strutturate:
 * {
 *   "type": "...",
 *   "title": "...",
 *   "status": 401,
 *   "detail": "...",
 *   "instance": "...",
 *   "timestamp": "..."
 * }
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler 
{

    /**
     * Gestisce credenziali non valide (email/password errate).
     * HTTP 401 Unauthorized.
     */
    @ExceptionHandler(InvalidCredentialsException.class)
    public ProblemDetail handleInvalidCredentials(InvalidCredentialsException e) 
    {
        log.warn("Credenziali non valide: {}", e.getMessage());
        return creaProblemDetail(HttpStatus.UNAUTHORIZED, "Credenziali non valide", e.getMessage(), "invalid-credentials");
    }

    /**
     * Gestisce eccezioni di Spring Security non catturate altrove.
     * HTTP 401 Unauthorized.
     */
    @ExceptionHandler(AuthenticationException.class)
    public ProblemDetail handleAuthenticationException(AuthenticationException e) 
    {
        log.warn("Errore di autenticazione: {}", e.getMessage());
        return creaProblemDetail(HttpStatus.UNAUTHORIZED, "Errore di autenticazione", e.getMessage(), "generic-authentication-error");
    }

    /**
     * Gestisce utente non trovato nel database.
     * HTTP 404 Not Found.
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ProblemDetail handleUserNotFound(UserNotFoundException e) 
    {
        log.warn("Utente non trovato: {}", e.getMessage());
        return creaProblemDetail(HttpStatus.NOT_FOUND, "Utente non trovato", e.getMessage(), "user-not-found");
    }

    /**
     * Gestisce errori di validazione dei DTO (es. campi mancanti, formato email errato).
     * HTTP 400 Bad Request.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationException(MethodArgumentNotValidException e) 
    {
        String dettagliErrori = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining("; "));

        log.warn("Errore di validazione: {}", dettagliErrori);
        return creaProblemDetail(HttpStatus.BAD_REQUEST, "Errore di validazione", dettagliErrori, "validation-error");
    }

    /**
     * Cattura qualsiasi eccezione non gestita dai metodi sopra.
     * HTTP 500 Internal Server Error.
     */
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception e) 
    {
        log.error("Errore interno non gestito", e); // log.error per stack trace completo
        return creaProblemDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Errore interno", "Si è verificato un errore imprevisto. Riprova più tardi.", "generic-error");
    }

    
    /**
     * Crea un ProblemDetail con i campi standard e un URI di tipo personalizzato.
     */
    private ProblemDetail creaProblemDetail(HttpStatus status, String title, String detail, String errorType) 
    {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setTitle(title);
        problemDetail.setType(URI.create("https://bugboard26.com/errori/" + errorType));
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }
}