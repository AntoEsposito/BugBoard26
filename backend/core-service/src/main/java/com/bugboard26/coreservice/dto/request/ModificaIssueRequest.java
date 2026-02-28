package com.bugboard26.coreservice.dto.request;

import com.bugboard26.coreservice.entity.enums.PrioritaIssue;
import com.bugboard26.coreservice.entity.enums.StatoIssue;
import lombok.Data;

import java.util.Set;

@Data
public class ModificaIssueRequest
{
    private String descrizione;

    private StatoIssue stato;

    /** Modificabile solo da ADMIN. */
    private PrioritaIssue priorita;

    /** Modificabile solo da ADMIN. */
    private Set<Integer> idAssegnatari;
}
