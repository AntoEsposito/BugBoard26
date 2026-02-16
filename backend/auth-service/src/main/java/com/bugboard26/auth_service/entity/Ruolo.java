package com.bugboard26.auth_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
@Table(name = "ruoli")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ruolo implements java.io.Serializable
{
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // il nome del ruolo deve iniziare con "ROLE_" per essere riconosciuto da Spring Security
    @Column(nullable = false, unique = true, length = 30)
    private String nome;

    @Column(length = 100)
    private String descrizione;

    
    // Override dei metodi di Serializable
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ruolo role = (Ruolo) o;
        // Due ruoli sono uguali se hanno lo stesso ID non nullo
        return id != null && Objects.equals(id, role.id);
    }

    @Override
    public int hashCode() {return getClass().hashCode();}
}