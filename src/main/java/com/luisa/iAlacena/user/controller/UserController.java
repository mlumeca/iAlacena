package com.luisa.iAlacena.user.controller;

import com.luisa.iAlacena.security.jwt.access.JwtService;
import com.luisa.iAlacena.security.jwt.refresh.RefreshTokenService;
import com.luisa.iAlacena.user.dto.CreateUserRequest;
import com.luisa.iAlacena.user.dto.UserResponse;
import com.luisa.iAlacena.user.model.User;
import com.luisa.iAlacena.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@Tag(name = "Usuario", description = "Realiza todas las operaciones de gestión del usuario")
public class UserController {

    private final UserService userService;
//    private final AuthenticationManager authenticationManager;
//    private final JwtService jwtService;
//    private final RefreshTokenService refreshTokenService;


    @Operation(summary = "Creación de un nuevo perfil de usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Se ha creado un usuario",
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
                    description = "¡Error!, Datos incorrectos ",
                    content = @Content)
    })
    @PostMapping("/user/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody CreateUserRequest createUserRequest) {
        User user = userService.createUser(createUserRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(UserResponse.of(user));
    }
}