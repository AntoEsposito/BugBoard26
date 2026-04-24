package com.bugboard26.coreservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

/**
 * Rappresenta un progetto software.
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
