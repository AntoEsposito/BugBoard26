package com.bugboard26.authservice.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

/**
 * Servizio per la gestione dei JSON Web Token (JWT).
 * Responsabile di:
 * - Generazione token con claims personalizzati
 * - Estrazione username e claims dal token
 * - Validazione token (firma + scadenza)
 */
@Slf4j
@Service
public class JwtService 
{
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expirationMs;

    /**
     * Estrae l'username (subject) dal token JWT.
     * L'username corrisponde all'email dell'utente.
     */
    public String estraiUsername(String token) 
    {
        return estraiClaim(token, Claims::getSubject);
    }

    /**
     * Genera un JWT con claims personalizzati.
     */
    public String generaToken(UserDetails userDetails, Map<String, Object> extraClaims) 
    {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(ottieniChiaveFirma())
                .compact();
    }

    /**
     * Valida il token verificando:
     * 1. L'username estratto corrisponde a quello di UserDetails
     * 2. Il token non è scaduto
     */
    public boolean tokenValido(String token, UserDetails userDetails) 
    {
        final String username = estraiUsername(token);
        return username.equals(userDetails.getUsername()) && !tokenScaduto(token);
    }

    /**
     * Ritorna il tempo di scadenza configurato in millisecondi.
     */
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
                    .verifyWith(ottieniChiaveFirma())
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

    /**
     * Ottiene la chiave segreta per firmare/verificare i token.
     * La chiave è una stringa in base 64 nel .env, che viene decodificata in byte array e convertita in SecretKey.
     */
    private SecretKey ottieniChiaveFirma() 
    {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}