package com.bugboard26.coreservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * Rappresenta un commento scritto da un utente su una issue.
 * La data di creazione viene impostata automaticamente al momento del persist.
 */
@Entity
@Table(name = "commenti")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Commento
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "id_issue", nullable = false)
    private Integer idIssue;

    @Column(name = "id_utente_creatore", nullable = false)
    private Integer idUtenteCreatore;

    @Column(nullable = false)
    private String contenuto;

    @Column(name = "data_creazione", nullable = false, updatable = false)
    private OffsetDateTime dataCreazione;

    @PrePersist
    private void impostaDataCreazione()
    {
        this.dataCreazione = OffsetDateTime.now();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Commento commento = (Commento) o;
        return id != null && Objects.equals(id, commento.id);
    }

    @Override
    public int hashCode() { return getClass().hashCode(); }
}
