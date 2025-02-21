package com.luisa.iAlacena.user.error;

public class ActivationExpiredException extends RuntimeException {
    public ActivationExpiredException(String s) {
        super(s);
    }
}