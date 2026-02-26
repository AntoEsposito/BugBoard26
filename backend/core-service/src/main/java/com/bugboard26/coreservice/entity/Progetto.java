package com.bugboard26.coreservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Rappresenta un progetto software.
 * I membri del progetto sono salvati nella join table "progetti_membri"
 * come semplici id interi (FK verso la tabella utenti dell'auth-service).
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

    // Mappa la join table progetti_membri — Set per garantire unicità dei membri
    @Builder.Default
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "progetti_membri",
            joinColumns = @JoinColumn(name = "id_progetto")
    )
    @Column(name = "id_utente", nullable = false)
    private Set<Integer> idMembri = new HashSet<>();

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
