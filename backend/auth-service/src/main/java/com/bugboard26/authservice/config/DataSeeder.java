package com.bugboard26.authservice.config;

import com.bugboard26.authservice.constants.AuthenticationConstants;
import com.bugboard26.authservice.entity.UserRole;
import com.bugboard26.authservice.entity.User;
import com.bugboard26.authservice.repository.UserRoleRepository;
import com.bugboard26.authservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * DataSeeder è un componente che si occupa di popolare il database con dati iniziali.
 * In questo caso, crea i ruoli di base e un utente admin se non esistono già.
 * Viene eseguito all'avvio dell'applicazione grazie all'implementazione di ApplicationRunner.
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements ApplicationRunner
{
    private final UserRoleRepository  ruoloRepository;
    private final UserRepository utenteRepository;
    private final PasswordEncoder  passwordEncoder;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    @Value("${admin.nome}")
    private String adminNome;

    @Value("${admin.cognome}")
    private String adminCognome;

    // ----------------------------------------------------------------

    private void seedRuoli()
    {
        if (ruoloRepository.count() == 0)
        {
            ruoloRepository.saveAll(List.of(
                UserRole.builder()
                    .nome(AuthenticationConstants.ROLE_UTENTE)
                    .descrizione("Utente con permessi standard")
                    .build(),
                UserRole.builder()
                    .nome(AuthenticationConstants.ROLE_ADMIN)
                    .descrizione("Amministratore con accesso completo a tutte le funzionalità")
                    .build()
            ));
            log.info("DataSeeder: ruoli di default inseriti.");
        } else {log.info("DataSeeder: ruoli già presenti, skip.");}
    }

    private void seedAdmin()
    {
        if (utenteRepository.existsByEmail(adminEmail))
        {
            log.info("DataSeeder: admin già presente, skip.");
            return;
        }

        UserRole ruoloAdmin = ruoloRepository.findByNome(AuthenticationConstants.ROLE_ADMIN).orElseThrow
            (() -> new IllegalStateException("ROLE_ADMIN non trovato."));

        User admin = User.builder()
                .email(adminEmail)
                .nome(adminNome)
                .cognome(adminCognome)
                .password(passwordEncoder.encode(adminPassword))
                .ruoloUtente(ruoloAdmin)
                .build();

        utenteRepository.save(admin);
        log.info("DataSeeder: utente admin creato con credenziali standard.");
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args)
    {
        seedRuoli();
        seedAdmin();
    }
}
