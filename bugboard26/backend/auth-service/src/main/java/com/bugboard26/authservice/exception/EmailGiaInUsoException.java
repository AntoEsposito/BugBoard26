package com.bugboard26.authservice.exception;

public class EmailGiaInUsoException extends RuntimeException
{
    public EmailGiaInUsoException(String email)
    {
        super("Email già in uso: " + email);
    }
}
