package com.bugboard26.authservice.config;

import com.bugboard26.authservice.constants.AuthenticationConstants;
import com.bugboard26.authservice.jwt.JwtFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configurazione di Spring Security per l'applicazione.
 * Configurazioni applicate:
 * - CSRF disabilitato (API REST stateless)
 * - Sessioni STATELESS (niente cookie di sessione)
 * - Endpoint /api/auth/** pubblici (login)
 * - Tutti gli altri endpoint richiedono autenticazione
 * - Filtro JWT eseguito prima del filtro di autenticazione standard
 *
 * Dipende da UserDetailsService tramite interfaccia, delegando il caricamento
 * utenti a UserDetailsServiceImpl senza accoppiamento al layer di persistenza.
 */
@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration
{
    private final UserDetailsService userDetailsService;

    /**
     * Catena di filtri di sicurezza principale.
     */
    @Bean
    @SuppressWarnings("java:S4502")
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtFilter filtroJwt)
    {
        return http
            // Disabilitiamo CSRF perché stiamo usando JWT
            .csrf(csrf -> csrf.disable())
            // Configuriamo le regole di autorizzazione
            .authorizeHttpRequests(auth -> auth.requestMatchers(AuthenticationConstants.PUBLIC_PATH).permitAll().anyRequest().authenticated())
            // Configuriamo la gestione delle sessioni come stateless
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // Configuriamo il provider di autenticazione personalizzato
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(filtroJwt, UsernamePasswordAuthenticationFilter.class)
            .build();
    }

    /**
     * Provider che gestisce l'autenticazione tramite database.
     * Usa BCrypt per verificare le password.
     */
    @Bean
    public AuthenticationProvider authenticationProvider()
    {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * AuthenticationManager esposto come Bean per poterlo iniettare
     * nei servizi (es. AuthServiceImpl per validare login).
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
    {
        return config.getAuthenticationManager();
    }

    /**
     * Encoder per le password usando BCrypt.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {return new BCryptPasswordEncoder();}
}
