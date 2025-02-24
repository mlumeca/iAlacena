package com.luisa.iAlacena.user.controller;

import com.luisa.iAlacena.user.dto.CreateUserRequest;
import com.luisa.iAlacena.user.dto.EditUserRequest;
import com.luisa.iAlacena.user.dto.UserResponse;
import com.luisa.iAlacena.user.model.User;
import com.luisa.iAlacena.user.model.UserRole;
import com.luisa.iAlacena.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@Tag(name = "Usuario", description = "El controlador de usuarios.")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Obtener todos los usuarios registrados",
            description = "Devuelve una lista paginada de usuarios, accesible solo para administradores.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Lista de usuarios obtenida con éxito",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Page.class),
                                    examples = {
                                            @ExampleObject(
                                                    value = """
                                                    {
                                                        "content": [
                                                            {
                                                                "id": "550e8400-e29b-41d4-a716-446655440001",
                                                                "username": "user1",
                                                                "token": null,
                                                                "refreshToken": null
                                                            },
                                                            {
                                                                "id": "550e8400-e29b-41d4-a716-446655440002",
                                                                "username": "user2",
                                                                "token": null,
                                                                "refreshToken": null
                                                            }
                                                        ],
                                                        "pageable": {
                                                            "pageNumber": 0,
                                                            "pageSize": 2,
                                                            "offset": 0,
                                                            "paged": true,
                                                            "unpaged": false
                                                        },
                                                        "totalElements": 5,
                                                        "totalPages": 3,
                                                        "size": 2,
                                                        "number": 0
                                                    }
                                                    """
                                            )
                                    })
                    }),
            @ApiResponse(responseCode = "401",
                    description = "No autenticado",
                    content = @Content),
            @ApiResponse(responseCode = "403",
                    description = "Acceso denegado - Requiere rol ADMIN",
                    content = @Content)
    })
    @GetMapping("/all")
    public ResponseEntity<Page<UserResponse>> findAll(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage = userService.getAllUsers(currentUser, pageable);
        Page<UserResponse> responsePage = userPage.map(UserResponse::of);
        return ResponseEntity.ok(responsePage);
    }

    @Operation(summary = "Creación de un nuevo perfil de usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Se ha creado un usuario",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UserResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    value = """
                                                    {
                                                        "id": "550e8400-e29b-41d4-a716-446655440000",
                                                        "username": "juanperez",
                                                        "token": null,
                                                        "refreshToken": null
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
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody CreateUserRequest request) {
        User user = userService.registerUser(request);
        return ResponseEntity.status(201).body(UserResponse.of(user));
    }

    @Operation(summary = "Creación de un nuevo perfil de administrador")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Se ha creado un administrador",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UserResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    value = """
                                                    {
                                                        "id": "550e8400-e29b-41d4-a716-446655440000",
                                                        "username": "juanperez",
                                                        "token": null,
                                                        "refreshToken": null
                                                    }
                                                    """
                                            )
                                    })
                    }),
            @ApiResponse(responseCode = "400",
                    description = "¡Error!, Datos incorrectos.",
                    content = @Content)
    })
    @PostMapping("/register-admin")
    public ResponseEntity<UserResponse> registerAdmin(@Valid @RequestBody CreateUserRequest request) {
        User user = userService.registerUser(request, UserRole.ADMIN);
        return ResponseEntity.status(201).body(UserResponse.of(user));
    }

    @Operation(summary = "Editar el perfil del usuario autenticado",
            description = "Permite al usuario autenticado actualizar su username y email.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Perfil actualizado con éxito",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UserResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    value = """
                                                    {
                                                        "id": "550e8400-e29b-41d4-a716-446655440000",
                                                        "username": "juanperez_nuevo",
                                                        "token": null,
                                                        "refreshToken": null
                                                    }
                                                    """
                                            )
                                    })
                    }),
            @ApiResponse(responseCode = "400",
                    description = "Datos inválidos o ya en uso",
                    content = @Content),
            @ApiResponse(responseCode = "401",
                    description = "No autenticado",
                    content = @Content)
    })
    @PutMapping("/profile")
    public ResponseEntity<UserResponse> editUserProfile(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody EditUserRequest request) {
        User updatedUser = userService.editUserProfile(currentUser, request);
        return ResponseEntity.ok(UserResponse.of(updatedUser));
    }
}