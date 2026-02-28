package com.bugboard26.coreservice.dto.response;

import com.bugboard26.coreservice.entity.enums.PrioritaIssue;
import com.bugboard26.coreservice.entity.enums.StatoIssue;
import com.bugboard26.coreservice.entity.enums.TipoIssue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IssueRiepilogoResponse
{
    private Integer id;
    private Integer idProgetto;
    private Integer idUtenteCreatore;
    private String titolo;
    private StatoIssue stato;
    private TipoIssue tipo;
    private PrioritaIssue priorita;
    private String descrizione;
    private OffsetDateTime dataCreazione;
    private OffsetDateTime dataUltimaModifica;
}
