package com.bugboard26.authservice.config;

import com.bugboard26.authservice.entity.User;
import com.bugboard26.authservice.entity.enums.Ruolo;
import com.bugboard26.authservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * DataSeeder è un componente che si occupa di popolare il database con dati iniziali.
 * Crea l'utente admin e l'utente base se non esistono già.
 * Viene eseguito all'avvio dell'applicazione grazie all'implementazione di ApplicationRunner.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements ApplicationRunner
{
    private final UserRepository utenteRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    @Value("${admin.nome}")
    private String adminNome;

    @Value("${admin.cognome}")
    private String adminCognome;

    @Value("${utente.email}")
    private String utenteEmail;

    @Value("${utente.password}")
    private String utentePassword;

    @Value("${utente.nome}")
    private String utenteNome;

    @Value("${utente.cognome}")
    private String utenteCognome;

    // ----------------------------------------------------------------

    private void seedAdmin()
    {
        if (utenteRepository.existsByEmail(adminEmail))
        {
            log.info("DataSeeder: admin già presente, skip.");
            return;
        }

        User admin = User.builder()
                .email(adminEmail)
                .nome(adminNome)
                .cognome(adminCognome)
                .password(passwordEncoder.encode(adminPassword))
                .ruolo(Ruolo.ROLE_ADMIN)
                .build();

        utenteRepository.save(admin);
        log.info("DataSeeder: utente admin creato con credenziali standard.");
    }

    private void seedUtente()
    {
        if (utenteRepository.existsByEmail(utenteEmail))
        {
            log.info("DataSeeder: utente base già presente, skip.");
            return;
        }

        User utente = User.builder()
                .email(utenteEmail)
                .nome(utenteNome)
                .cognome(utenteCognome)
                .password(passwordEncoder.encode(utentePassword))
                .ruolo(Ruolo.ROLE_UTENTE)
                .build();

        utenteRepository.save(utente);
        log.info("DataSeeder: utente base creato con credenziali standard.");
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args)
    {
        seedAdmin();
        seedUtente();
    }
}
