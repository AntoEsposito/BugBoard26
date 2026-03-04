package com.bugboard26.coreservice.dto.request;

import com.bugboard26.coreservice.entity.enums.PrioritaIssue;
import com.bugboard26.coreservice.entity.enums.StatoIssue;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class ModificaIssueRequest
{
    @Size(max = 500)
    private String descrizione;

    private StatoIssue stato;

    private PrioritaIssue priorita;

    private Set<Integer> idAssegnatari;
}
