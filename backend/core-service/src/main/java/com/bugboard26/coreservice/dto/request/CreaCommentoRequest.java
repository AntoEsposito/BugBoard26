package com.bugboard26.coreservice.dto.request;

import com.bugboard26.coreservice.entity.enums.TipoCommento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreaCommentoRequest
{
    @NotBlank
    private String contenuto;

    @NotNull
    private TipoCommento tipo;
}
