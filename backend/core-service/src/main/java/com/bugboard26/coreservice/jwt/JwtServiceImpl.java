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
import java.util.function.Function;

/**
 * Implementazione del servizio JWT per il core-service.
 * Valida i token emessi dall'auth-service usando la stessa chiave segreta condivisa.
 * La SecretKey viene inizializzata una sola volta all'avvio tramite @PostConstruct.
 */
@Slf4j
@Component
public class JwtServiceImpl implements JwtService
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

    @Override
    public String estraiUsername(String token)
    {
        return estraiClaim(token, Claims::getSubject);
    }

    @Override
    public String estraiRuolo(String token)
    {
        return estraiClaim(token, claims -> claims.get("ruolo", String.class));
    }

    /**
     * Verifica firma e scadenza del token senza UserDetails.
     * Ritorna false in caso di qualsiasi eccezione JWT.
     */
    @Override
    public boolean tokenValido(String token)
    {
        try
        {
            estraiTuttiClaims(token);
            return true;
        }
        catch (JwtException | IllegalArgumentException e)
        {
            log.warn("Token JWT non valido: {}", e.getMessage());
            return false;
        }
    }

    // ----------------------------------------------------------------
    // Metodi privati
    // ----------------------------------------------------------------

    private <T> T estraiClaim(String token, Function<Claims, T> resolver)
    {
        return resolver.apply(estraiTuttiClaims(token));
    }

    private Claims estraiTuttiClaims(String token)
    {
        return Jwts.parser()
                .verifyWith(chiaveFirma)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
