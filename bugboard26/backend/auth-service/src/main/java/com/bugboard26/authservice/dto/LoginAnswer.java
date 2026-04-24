package com.bugboard26.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginAnswer
{
    private String token;
    private String tokenType;   // nel nostro caso sempre "Bearer"
    private String email;
    private String nome;
    private String cognome;
    private String userRole;
    private long expireTime;     // millisecondi
}