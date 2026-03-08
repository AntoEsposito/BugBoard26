package com.bugboard26.coreservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreaCommentoRequest
{
    @NotBlank
    @Size(max = 500)
    private String contenuto;
}
