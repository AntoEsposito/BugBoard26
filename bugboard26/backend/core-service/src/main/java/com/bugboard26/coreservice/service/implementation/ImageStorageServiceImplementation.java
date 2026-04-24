package com.bugboard26.coreservice.service.implementation;

import com.bugboard26.coreservice.exception.FormatoNonValidoException;
import com.bugboard26.coreservice.exception.SalvataggioImmagineException;
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
        String estensione = estraiEstensioneSicura(file.getOriginalFilename());
        String nomeFile = UUID.randomUUID() + "." + estensione;

        Path dirSicura = Paths.get(uploadDir).normalize().toAbsolutePath();
        Path destinazione = dirSicura.resolve(nomeFile).normalize();

        if (!destinazione.startsWith(dirSicura))
            throw new FormatoNonValidoException("Percorso file non consentito");

        try
        {
            file.transferTo(destinazione);
            log.info("Immagine salvata: {}", destinazione);
        }
        catch (IOException e)
        {
            throw new SalvataggioImmagineException("Errore durante il salvataggio dell'immagine", e);
        }

        return "/api/uploads/" + nomeFile;
    }

    @Override
    public void elimina(String percorsoRelativo)
    {
        if (percorsoRelativo == null || percorsoRelativo.isBlank()) return;

        String nomeFile = Paths.get(percorsoRelativo).getFileName().toString();
        Path dirSicura = Paths.get(uploadDir).normalize().toAbsolutePath();
        Path file = dirSicura.resolve(nomeFile).normalize();

        if (!file.startsWith(dirSicura))
            throw new FormatoNonValidoException("Percorso file non consentito");

        try
        {
            Files.deleteIfExists(file);
            log.info("Immagine eliminata: {}", file);
        }
        catch (IOException e)
        {
            log.warn("Impossibile eliminare l'immagine {}: {}", file, e.getMessage());
        }
    }

    private String estraiEstensioneSicura(String nomeFile)
    {
        if (nomeFile == null || !nomeFile.contains("."))
            throw new FormatoNonValidoException("Nome file non valido o privo di estensione");

        String estensione = nomeFile.substring(nomeFile.lastIndexOf('.') + 1).toLowerCase();

        if (!ESTENSIONI_AMMESSE.contains(estensione))
            throw new FormatoNonValidoException("Formato immagine non supportato: " + estensione);

        return estensione;
    }
}
