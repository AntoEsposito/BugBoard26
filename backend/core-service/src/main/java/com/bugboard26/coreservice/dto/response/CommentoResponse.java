package com.bugboard26.coreservice.dto.response;

import com.bugboard26.coreservice.entity.enums.TipoCommento;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentoResponse
{
    private Integer id;
    private Integer idIssue;
    private Integer idUtenteCreatore;
    private String contenuto;
    private TipoCommento tipo;
    private OffsetDateTime dataCreazione;
}
