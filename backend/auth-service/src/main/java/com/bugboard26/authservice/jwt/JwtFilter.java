package com.bugboard26.authservice.jwt;

import com.bugboard26.authservice.constants.AuthenticationConstants;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro che intercetta ogni richiesta HTTP per validare il token JWT.
 * 1. Estrae l'header "Authorization"
 * 2. Se presente e inizia con "Bearer ", estrae il token
 * 3. Valida il token e carica l'utente dal DB
 * 4. Se valido, imposta l'autenticazione nel SecurityContext
 * 5. Passa la richiesta al filtro successivo nella catena
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter 
{
    private final JwtService servizioJwt;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException 
    {
        final String authHeader = request.getHeader(AuthenticationConstants.AUTHORIZATION_HEADER);

        // Se manca l'header Authorization o non è un Bearer token, passa oltre
        if (authHeader == null || !authHeader.startsWith(AuthenticationConstants.BEARER_PREFIX)) 
        {
            filterChain.doFilter(request, response);
            return;
        }

        // Estrai il token (rimuovi "Bearer ")
        final String jwt = authHeader.substring(AuthenticationConstants.BEARER_PREFIX.length());
        final String userEmail;

        try 
        {
            userEmail = servizioJwt.estraiUsername(jwt);
        } 
        catch (JwtException e) 
        {
            // Token malformato o scaduto: lascia passare senza autenticare
            // Spring Security gestirà il 401 se l'endpoint richiede autenticazione
            log.warn("Token JWT non valido: {}", e.getMessage());
            filterChain.doFilter(request, response);
            return;
        }

        // Se abbiamo estratto un username e l'utente non è già autenticato
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) 
        {
            // Carica l'utente dal database
            UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

            // Valida il token rispetto all'utente caricato
            if (servizioJwt.tokenValido(jwt, userDetails)) 
            {
                // Crea l'oggetto Authentication
                var authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                
                // Aggiungi dettagli della richiesta (IP, session ID, ecc.)
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                // Imposta l'autenticazione nel SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authToken);
                
                log.debug("Utente autenticato: {}", userEmail);
            }
        }

        // Passa la richiesta al filtro successivo
        filterChain.doFilter(request, response);
    }
}