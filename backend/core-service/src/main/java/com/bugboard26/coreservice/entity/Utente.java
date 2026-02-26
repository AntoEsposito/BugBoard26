package com.bugboard26.coreservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

import java.util.Objects;

/**
 * Entity read-only che mappa la tabella "utenti" gestita dall'auth-service.
 * Usata per risolvere email → id e per leggere i dati anagrafici da mostrare in UI.
 * @Immutable impedisce a Hibernate di emettere UPDATE su questa tabella.
 * Nessun metodo di scrittura deve essere invocato sul repository di questa entity.
 */
@Entity
@Table(name = "utenti")
@Immutable
@Getter
@NoArgsConstructor
public class Utente
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String cognome;

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Utente utente = (Utente) o;
        return id != null && Objects.equals(id, utente.id);
    }

    @Override
    public int hashCode() { return getClass().hashCode(); }
}
