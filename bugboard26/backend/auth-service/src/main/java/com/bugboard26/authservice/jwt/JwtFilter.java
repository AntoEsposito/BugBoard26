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
 * Filtro di autenticazione JWT che intercetta ogni richiesta HTTP.
 * Responsabile di:
 * - Estrarre il token JWT dall'header Authorization
 * - Validare il token e autenticare l'utente se valido
 * - Aggiornare il SecurityContext con l'autenticazione dell'utente
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService servizioJwt;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) 
    throws ServletException, IOException 
    {
        final String jwt = estraiTokenDaHeader(request);
        // Se c'è un token e l'utente non è già autenticato nel contesto
        if (jwt != null && SecurityContextHolder.getContext().getAuthentication() == null) processaAutenticazioneToken(jwt, request);
        filterChain.doFilter(request, response);
    }

    /**
     * Estrae la stringa JWT dall'header Authorization se presente e ben formattata.
     */
    private String estraiTokenDaHeader(HttpServletRequest request) 
    {
        final String authorizationHeader = request.getHeader(AuthenticationConstants.AUTHORIZATION_HEADER);
        if (authorizationHeader == null || !authorizationHeader.startsWith(AuthenticationConstants.BEARER_PREFIX)) return null;

        return authorizationHeader.substring(AuthenticationConstants.BEARER_PREFIX.length());
    }

    /**
     * Gestisce il ciclo di vita della validazione e autenticazione del token.
     */
    private void processaAutenticazioneToken(String jwt, HttpServletRequest request) 
    {
        try
        {
            final String userEmail = servizioJwt.estraiUsername(jwt);
            if (userEmail != null) autenticaUtente(userEmail, jwt, request);
        } 
        catch (JwtException | IllegalArgumentException e) 
        {
            // Log dettagliato per facilitare il debug di problemi con i token
            log.warn("Tentativo di autenticazione fallito con token non valido: {}", e.getMessage());
        }
    }

    /**
     * Carica i dettagli utente, verifica il token e aggiorna il SecurityContext.
     */
    private void autenticaUtente(String userEmail, String jwt, HttpServletRequest request) 
    {
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

        if (servizioJwt.tokenValido(jwt, userDetails))
        {
            impostaSecurityContext(userDetails, request);
            log.debug("Utente autenticato con successo: {}", userEmail);
        }
    }

    /**
     * Crea l'oggetto Authentication e lo inietta nel contesto di Spring Security.
     */
    private void impostaSecurityContext(UserDetails userDetails, HttpServletRequest request) 
    {
        var authToken = new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }
}