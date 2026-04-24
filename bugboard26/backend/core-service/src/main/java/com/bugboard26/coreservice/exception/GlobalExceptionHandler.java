package com.bugboard26.coreservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler
{
    @ExceptionHandler(AccesoNegatoException.class)
    public ProblemDetail handleAccesoNegato(AccesoNegatoException e)
    {
        log.warn("Accesso negato: {}", e.getMessage());
        return creaProblemDetail(HttpStatus.FORBIDDEN, "Accesso negato", e.getMessage());
    }

    @ExceptionHandler(RisorsaNonTrovataException.class)
    public ProblemDetail handleRisorsaNonTrovata(RisorsaNonTrovataException e)
    {
        log.warn("Risorsa non trovata: {}", e.getMessage());
        return creaProblemDetail(HttpStatus.NOT_FOUND, "Risorsa non trovata", e.getMessage());
    }

    @ExceptionHandler(FormatoNonValidoException.class)
    public ProblemDetail handleFormatoNonValido(FormatoNonValidoException e)
    {
        log.warn("Formato non valido: {}", e.getMessage());
        return creaProblemDetail(HttpStatus.BAD_REQUEST, "Formato non valido", e.getMessage());
    }

    @ExceptionHandler(OperazioneNonConsentitaException.class)
    public ProblemDetail handleOperazioneNonConsentita(OperazioneNonConsentitaException e)
    {
        log.warn("Operazione non consentita: {}", e.getMessage());
        return creaProblemDetail(HttpStatus.CONFLICT, "Operazione non consentita", e.getMessage());
    }

    @ExceptionHandler(SalvataggioImmagineException.class)
    public ProblemDetail handleSalvataggioImmagine(SalvataggioImmagineException e)
    {
        log.error("Errore salvataggio immagine: {}", e.getMessage(), e);
        return creaProblemDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Errore salvataggio immagine", e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidazione(MethodArgumentNotValidException e)
    {
        String dettagli = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining("; "));
        log.warn("Errore di validazione: {}", dettagli);
        return creaProblemDetail(HttpStatus.BAD_REQUEST, "Errore di validazione", dettagli);
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenerico(Exception e)
    {
        log.error("Errore interno", e);
        return creaProblemDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Errore interno", "Si è verificato un errore imprevisto.");
    }

    private ProblemDetail creaProblemDetail(HttpStatus status, String title, String detail)
    {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, detail);
        pd.setTitle(title);
        pd.setProperty("timestamp", Instant.now());
        return pd;
    }
}
