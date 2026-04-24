package com.bugboard26.authservice.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

/**
 * Implementazione concreta del servizio per la gestione dei JSON Web Token (JWT).
 * Responsabile di:
 * - Generazione token con claims personalizzati
 * - Estrazione username e claims dal token
 * - Validazione token (firma + scadenza)
 *
 * La SecretKey viene calcolata una volta sola all'avvio (@PostConstruct)
 * ed è riutilizzata per tutte le operazioni di firma e verifica.
 */
@Slf4j
@Component
public class JwtServiceImplementation implements JwtService
{
    @Value("${jwt.secret}")
    private String secretKeyString;

    @Value("${jwt.expiration}")
    private long expirationMs;

    private SecretKey chiaveFirma;

    /**
     * Decodifica la chiave Base64 una sola volta all'avvio e la mantiene in memoria.
     * Evita di ricalcolarla ad ogni operazione JWT.
     */
    @PostConstruct
    private void inizializza()
    {
        byte[] keyBytes = Decoders.BASE64.decode(secretKeyString);
        this.chiaveFirma = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Estrae l'username (subject) dal token JWT.
     * L'username corrisponde all'email dell'utente.
     */
    @Override
    public String estraiUsername(String token)
    {
        return estraiClaim(token, Claims::getSubject);
    }

    /**
     * Genera un JWT con claims personalizzati.
     */
    @Override
    public String generaToken(UserDetails userDetails, Map<String, Object> extraClaims)
    {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(chiaveFirma)
                .compact();
    }

    /**
     * Valida il token verificando:
     * 1. L'username estratto corrisponde a quello di UserDetails
     * 2. Il token non è scaduto
     */
    @Override
    public boolean tokenValido(String token, UserDetails userDetails)
    {
        final String username = estraiUsername(token);
        return username.equals(userDetails.getUsername()) && !tokenScaduto(token);
    }

    /**
     * Ritorna il tempo di scadenza configurato in millisecondi.
     */
    @Override
    public long ottieniScadenzaMs()
    {
        return expirationMs;
    }

    // ----------------------------------------------------------------
    // Metodi privati di utilità
    // ----------------------------------------------------------------

    private boolean tokenScaduto(String token)
    {
        return estraiScadenza(token).before(new Date());
    }

    private Date estraiScadenza(String token)
    {
        return estraiClaim(token, Claims::getExpiration);
    }

    /**
     * Estrae un claim specifico dal token usando un resolver.
     */
    private <T> T estraiClaim(String token, Function<Claims, T> claimsResolver)
    {
        final Claims claims = estraiTuttiClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Estrae tutti i claims dal token.
     * Lancia JwtException se il token è malformato o la firma non è valida.
     */
    private Claims estraiTuttiClaims(String token)
    {
        try
        {
            return Jwts.parser()
                    .verifyWith(chiaveFirma)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        }
        catch (JwtException e)
        {
            log.error("Errore validazione JWT: {}", e.getMessage());
            throw e;
        }
    }
}
