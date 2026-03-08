package com.bugboard26.coreservice.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImageStorageService
{
    /**
     * Salva il file su disco e restituisce il percorso relativo
     * da restituire al client (es. "/api/uploads/uuid.jpg").
     */
    String salva(MultipartFile file);

    /**
     * Elimina il file identificato dal percorso relativo.
     * Non lancia eccezioni se il file non esiste.
     */
    void elimina(String percorsoRelativo);
}
