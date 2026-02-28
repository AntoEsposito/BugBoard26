package com.bugboard26.coreservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgettoResponse
{
    private Integer id;
    private String nome;
    private String descrizione;
}
