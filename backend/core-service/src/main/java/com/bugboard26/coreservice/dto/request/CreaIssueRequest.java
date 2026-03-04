package com.bugboard26.coreservice.dto.request;

import com.bugboard26.coreservice.entity.enums.PrioritaIssue;
import com.bugboard26.coreservice.entity.enums.TipoIssue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreaIssueRequest
{
    @NotNull
    private Integer idProgetto;

    @NotBlank
    @Size(max = 32)
    private String titolo;

    @NotNull
    private TipoIssue tipo;

    @Size(max = 500)
    private String descrizione;

    /** Se null, il service applica il default LOW. */
    private PrioritaIssue priorita;
}
