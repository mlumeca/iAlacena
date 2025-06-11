package com.luisa.iAlacena.error;

public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}