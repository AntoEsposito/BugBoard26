package com.bugboard26.coreservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Rappresenta un progetto software.
 * I membri sono mappati via @ManyToMany sulla join table "progetti_membri". La tabella utenti è gestita dall'auth-service.
 */
@Entity
@Table(name = "progetti")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Progetto
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String nome;

    @Column
    private String descrizione;

    // Set per garantire unicità dei membri — nessun cascade, Utente è read-only
    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "progetti_membri",
            joinColumns = @JoinColumn(name = "id_progetto"),
            inverseJoinColumns = @JoinColumn(name = "id_utente")
    )
    private Set<Utente> membri = new HashSet<>();

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Progetto progetto = (Progetto) o;
        return id != null && Objects.equals(id, progetto.id);
    }

    @Override
    public int hashCode() { return getClass().hashCode(); }
}
