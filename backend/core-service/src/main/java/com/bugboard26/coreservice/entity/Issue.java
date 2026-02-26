package com.bugboard26.coreservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.bugboard26.coreservice.entity.enums.PrioritaIssue;
import com.bugboard26.coreservice.entity.enums.StatoIssue;
import com.bugboard26.coreservice.entity.enums.TipoIssue;

/**
 * Rappresenta una issue associata a un progetto.
 * Gli assegnatari sono mappati via @ManyToMany sulla join table "issue_utente_assegnato".
 * La data di creazione viene impostata automaticamente al momento del persist.
 */
@Entity
@Table(name = "issue")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Issue
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "id_progetto", nullable = false)
    private Integer idProgetto;

    @Column(name = "id_utente_creatore", nullable = false)
    private Integer idUtenteCreatore;

    @Column(nullable = false)
    private String titolo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatoIssue stato;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoIssue tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PrioritaIssue priorita;

    @Column
    private String descrizione;

    @Column(name = "data_creazione", nullable = false, updatable = false)
    private OffsetDateTime dataCreazione;

    @Column(name = "data_ultima_modifica")
    private OffsetDateTime dataUltimaModifica;

    // Set per garantire unicità degli assegnatari — nessun cascade, Utente è read-only
    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "issue_utente_assegnato",
            joinColumns = @JoinColumn(name = "id_issue"),
            inverseJoinColumns = @JoinColumn(name = "id_utente_assegnato")
    )
    private Set<Utente> assegnatari = new HashSet<>();

    @PrePersist
    private void impostaDataCreazione()
    {
        this.dataCreazione = OffsetDateTime.now();
    }

    @PreUpdate
    private void impostaDataUltimaModifica()
    {
        this.dataUltimaModifica = OffsetDateTime.now();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Issue issue = (Issue) o;
        return id != null && Objects.equals(id, issue.id);
    }

    @Override
    public int hashCode() { return getClass().hashCode(); }
}
