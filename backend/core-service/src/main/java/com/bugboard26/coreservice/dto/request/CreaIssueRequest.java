package com.bugboard26.coreservice.dto.request;

import com.bugboard26.coreservice.entity.enums.TipoIssue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreaIssueRequest
{
    @NotNull
    private Integer idProgetto;

    @NotBlank
    private String titolo;

    @NotNull
    private TipoIssue tipo;

    private String descrizione;
}
