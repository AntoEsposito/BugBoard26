package com.bugboard26.authservice.config;

import com.bugboard26.authservice.constants.AuthenticationConstants;
import com.bugboard26.authservice.repository.UtenteRepository;
import com.bugboard26.authservice.jwt.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configurazione di Spring Security per l'applicazione.
 * 
 * Configurazioni applicate:
 * - CSRF disabilitato (API REST stateless)
 * - Sessioni STATELESS (niente cookie di sessione)
 * - Endpoint /api/auth/** pubblici (login)
 * - Tutti gli altri endpoint richiedono autenticazione
 * - Filtro JWT eseguito prima del filtro di autenticazione standard
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration 
{
    private final JwtFilter filtroJwt;
    private final UtenteRepository utenteRepository;

    /**
     * Catena di filtri di sicurezza principale.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws IllegalStateException 
    {
        http
            // Disabilita CSRF (non serve per API REST stateless)
            .csrf(AbstractHttpConfigurer::disable)
            
            // Configurazione autorizzazioni
            .authorizeHttpRequests(auth -> auth
                // Endpoint di autenticazione pubblici
                .requestMatchers(AuthenticationConstants.PUBLIC_PATH).permitAll()
                // Tutti gli altri endpoint richiedono autenticazione
                .anyRequest().authenticated()
            )
            
            // Sessioni STATELESS (niente cookie, solo JWT)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // Configura il provider di autenticazione
            .authenticationProvider(authenticationProvider())
            
            // Aggiungi il filtro JWT PRIMA del filtro di autenticazione standard
            .addFilterBefore(filtroJwt, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * UserDetailsService personalizzato che carica gli utenti dal database.
     * Cerca per email invece che per username.
     */
    @Bean
    public UserDetailsService userDetailsService() 
    {
        return email -> utenteRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utente non trovato: " + email));
    }

    /**
     * Provider che gestisce l'autenticazione tramite database.
     * Usa BCrypt per verificare le password.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() 
    {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
    /**
     * AuthenticationManager esposto come Bean per poterlo iniettare
     * nei servizi (es. AuthService per validare login).
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) 
        throws IllegalStateException
    {
        return config.getAuthenticationManager();
    }

    /**
     * Encoder per le password usando BCrypt.
     * BCrypt è un algoritmo di hashing lento di proposito (rallenta brute force).
     */
    @Bean
    public PasswordEncoder passwordEncoder() 
    {
        return new BCryptPasswordEncoder();
    }
}