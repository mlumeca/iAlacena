package com.luisa.iAlacena.user.controller;

import com.luisa.iAlacena.user.dto.CreateUserRequest;
import com.luisa.iAlacena.user.model.UserRole;
import com.luisa.iAlacena.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Creación de un nuevo perfil de administrador")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Se ha creado un administrador",
                    content = {
                            @Content(mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = CreateUserRequest.class)),
                                    examples = {
                                            @ExampleObject(
                                                    value = """
                                         {
                                                                   "username": "juanperez",
                                                                   "password": "12345",
                                                                   "email": "juan.perez@example.com",
                                                                   "verifyPassword": "12345",
                                                                 }
                                    """
                                            )
                                    })
                    }),
            @ApiResponse(responseCode = "400",
                    description = "¡Error!, Datos incorrectos.",
                    content = @Content)
    })
    @PostMapping("/register")
    public ResponseEntity<Void> registerAdmin(@Valid @RequestBody CreateUserRequest request) {
        userService.registerUser(request, UserRole.ADMIN);
        return ResponseEntity.status(201).build();
    }
}