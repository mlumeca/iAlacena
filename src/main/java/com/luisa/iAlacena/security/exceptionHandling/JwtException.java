package com.luisa.iAlacena.security.exceptionHandling;

public class JwtException extends RuntimeException {
    public JwtException(String message) {
        super(message);
    }
}