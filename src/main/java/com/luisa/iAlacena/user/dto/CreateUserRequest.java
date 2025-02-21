package com.luisa.iAlacena.user.dto;

import com.luisa.iAlacena.validation.annotation.FieldsValueMatch;
import jakarta.validation.constraints.Email;
import org.hibernate.validator.constraints.UniqueElements;

// @NotBlank(username, email, password, verifyPassword)
@FieldsValueMatch(
        field = "password",
        fieldMatch = "verifyPassword",
        message = "La contraseña no coincide") // mejor si lo externalizamos al modelo de properties para futuras traducciones
public record CreateUserRequest(
        @UniqueElements
        String username,
        @Email
        String email,
        String password,
        String verifyPassword
) {
}