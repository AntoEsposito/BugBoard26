package com.bugboard26.coreservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UtenteResponse
{
    private Integer id;
    private String email;
    private String nome;
    private String cognome;
}
