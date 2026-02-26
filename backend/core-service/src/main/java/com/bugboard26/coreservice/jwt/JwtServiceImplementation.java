package com.bugboard26.coreservice.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

/**
 * Implementazione del servizio JWT per il core-service.
 * Valida i token emessi dall'auth-service usando la stessa chiave segreta condivisa.
 * La SecretKey viene inizializzata una sola volta all'avvio tramite @PostConstruct.
 */
@Slf4j
@Component
public class JwtServiceImplementation implements JwtService
{
    @Value("${jwt.secret}")
    private String secretKeyString;

    private SecretKey chiaveFirma;

    @PostConstruct
    private void inizializza()
    {
        byte[] keyBytes = Decoders.BASE64.decode(secretKeyString);
        this.chiaveFirma = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Valida il token ed estrae email e ruolo in un solo parse.
     * jjwt lancia automaticamente JwtException se il token è scaduto o la firma non è valida.
     */
    @Override
    public ClaimsUtente validaEOttieniClaims(String token) throws JwtException
    {
        Claims claims = Jwts.parser()
                .verifyWith(chiaveFirma)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return new ClaimsUtente(
                claims.getSubject(),
                claims.get("ruolo", String.class)
        );
    }
}
