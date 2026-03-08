package com.bugboard26.coreservice.config;

import com.bugboard26.coreservice.entity.Progetto;
import com.bugboard26.coreservice.repository.ProgettoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Popola il database con 3 progetti di default se non esistono già.
 * Viene eseguito all'avvio dell'applicazione.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements ApplicationRunner
{
    private final ProgettoRepository progettoRepository;

    // ----------------------------------------------------------------

    private void seedProgetto(String nome, String descrizione)
    {
        if (progettoRepository.existsByNome(nome))
        {
            log.info("DataSeeder: progetto '{}' già presente, skip.", nome);
            return;
        }

        Progetto progetto = Progetto.builder()
                .nome(nome)
                .descrizione(descrizione)
                .build();

        progettoRepository.save(progetto);
        log.info("DataSeeder: progetto '{}' creato.", nome);
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args)
    {
        seedProgetto("BugBoard26", "Piattaforma web per il tracciamento di issue software.");
        seedProgetto("Progetto Walrider", "Sviluppo di un'arma informatica a sciame guidata da un'intelligenza artificiale");
        seedProgetto("Progetto Pegasus", "Sviluppo del software di spionaggio più avanzato al mondo.");
    }
}
