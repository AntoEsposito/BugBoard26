package com.bugboard26.authservice.config;

import com.bugboard26.authservice.entity.RuoloUtente;
import com.bugboard26.authservice.entity.Utente;
import com.bugboard26.authservice.repository.RuoloRepository;
import com.bugboard26.authservice.repository.UtenteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

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
    private final RuoloRepository  ruoloRepository;
    private final UtenteRepository utenteRepository;
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
                RuoloUtente.builder()
                    .nome("ROLE_UTENTE")
                    .descrizione("Utente con permessi standard")
                    .build(),
                RuoloUtente.builder()
                    .nome("ROLE_ADMIN")
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

        RuoloUtente ruoloAdmin = ruoloRepository.findByNome("ROLE_ADMIN").orElseThrow
            (() -> new IllegalStateException("ROLE_ADMIN non trovato."));

        Utente admin = Utente.builder()
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
    public void run(ApplicationArguments args) 
    {
        seedRuoli();
        seedAdmin();
    }
}