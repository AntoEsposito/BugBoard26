package com.bugboard26.coreservice.service.implementation;

import com.bugboard26.coreservice.service.ImageStorageService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
public class ImageStorageServiceImplementation implements ImageStorageService
{
    private static final Set<String> ESTENSIONI_AMMESSE = Set.of("jpg", "jpeg", "png", "gif", "webp");

    @Value("${upload.dir}")
    private String uploadDir;

    @PostConstruct
    private void inizializza() throws IOException
    {
        Path dir = Paths.get(uploadDir);
        if (!Files.exists(dir))
        {
            Files.createDirectories(dir);
            log.info("Directory upload creata: {}", dir.toAbsolutePath());
        }
    }

    @Override
    public String salva(MultipartFile file)
    {
        String nomeOriginale = file.getOriginalFilename();
        String estensione = estraiEstensione(nomeOriginale);

        if (!ESTENSIONI_AMMESSE.contains(estensione.toLowerCase()))
            throw new IllegalArgumentException("Formato immagine non supportato: " + estensione);

        String nomeFile = UUID.randomUUID() + "." + estensione.toLowerCase();
        Path destinazione = Paths.get(uploadDir, nomeFile);

        try
        {
            file.transferTo(destinazione);
            log.info("Immagine salvata: {}", destinazione.toAbsolutePath());
        }
        catch (IOException e)
        {
            throw new RuntimeException("Errore durante il salvataggio dell'immagine", e);
        }

        return "/api/uploads/" + nomeFile;
    }

    @Override
    public void elimina(String percorsoRelativo)
    {
        if (percorsoRelativo == null || percorsoRelativo.isBlank()) return;

        String nomeFile = Paths.get(percorsoRelativo).getFileName().toString();
        Path file = Paths.get(uploadDir, nomeFile);

        try
        {
            Files.deleteIfExists(file);
            log.info("Immagine eliminata: {}", file.toAbsolutePath());
        }
        catch (IOException e)
        {
            log.warn("Impossibile eliminare l'immagine {}: {}", file.toAbsolutePath(), e.getMessage());
        }
    }

    private String estraiEstensione(String nomeFile)
    {
        if (nomeFile == null || !nomeFile.contains("."))
            throw new IllegalArgumentException("Nome file non valido o privo di estensione");
        return nomeFile.substring(nomeFile.lastIndexOf('.') + 1);
    }
}
