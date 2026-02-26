package com.bugboard26.authservice.config;

import com.bugboard26.authservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 * Implementazione di UserDetailsService che carica gli utenti dal database.
 * Estratta da SecurityConfiguration per rispettare la separazione delle responsabilità:
 * la configurazione di sicurezza non deve conoscere il layer di persistenza.
 */
@Component
@RequiredArgsConstructor
public class UserDetailsServiceImplementation implements UserDetailsService
{
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException
    {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utente non trovato: " + email));
    }
}
