package com.luisa.iAlacena.user.controller;

import com.luisa.iAlacena.storage.dto.FileResponse;
import com.luisa.iAlacena.user.dto.*;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/user")
@Tag(name = "Usuario", description = "El controlador de usuarios.")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Creación de un nuevo perfil de usuario",
            description = "Registra un nuevo usuario con rol USER.")
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

    @Operation(summary = "Creación de un nuevo perfil de administrador",
            description = "Registra un nuevo usuario con rol ADMIN.")
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
                                                        "username": "adminperez",
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

    @Operation(summary = "Actualizar la foto de perfil del usuario",
            description = "Permite al usuario autenticado actualizar su foto de perfil subiendo una imagen.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Foto de perfil actualizada con éxito",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = FileResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    value = """
                                                    {
                                                        "id": "profile-pic.jpg",
                                                        "name": "profile-pic.jpg",
                                                        "uri": "http://localhost:8080/download/profile-pic.jpg",
                                                        "type": "image/jpeg",
                                                        "size": 102400
                                                    }
                                                    """
                                            )
                                    })
                    }),
            @ApiResponse(responseCode = "400",
                    description = "Archivo inválido",
                    content = @Content),
            @ApiResponse(responseCode = "401",
                    description = "No autenticado",
                    content = @Content),
            @ApiResponse(responseCode = "403",
                    description = "Acceso denegado - Solo el propietario puede actualizar su foto",
                    content = @Content)
    })
    @PostMapping("/{id}/profile-picture")
    public ResponseEntity<FileResponse> updateProfilePicture(
            @AuthenticationPrincipal User currentUser,
            @PathVariable UUID id,
            @RequestPart("file") MultipartFile file) {
        FileResponse response = userService.updateProfilePicture(currentUser, id, file);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Eliminar la foto de perfil del usuario",
            description = "Permite al usuario autenticado eliminar su foto de perfil.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204",
                    description = "Foto de perfil eliminada con éxito",
                    content = @Content),
            @ApiResponse(responseCode = "401",
                    description = "No autenticado",
                    content = @Content),
            @ApiResponse(responseCode = "403",
                    description = "Acceso denegado - Solo el propietario puede eliminar su foto",
                    content = @Content),
            @ApiResponse(responseCode = "404",
                    description = "Usuario no encontrado",
                    content = @Content)
    })
    @DeleteMapping("/{id}/profile-picture")
    public ResponseEntity<Void> deleteProfilePicture(
            @AuthenticationPrincipal User currentUser,
            @PathVariable UUID id) {
        userService.deleteProfilePicture(currentUser, id);
        return ResponseEntity.noContent().build();
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
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage = userService.getAllUsers(currentUser, pageable);
        Page<UserResponse> responsePage = userPage.map(UserResponse::of);
        return ResponseEntity.ok(responsePage);
    }

    @Operation(summary = "Obtener un usuario por ID",
            description = "Devuelve la información básica de un usuario por su ID, accesible para administradores o el propio usuario.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Usuario encontrado con éxito",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UserResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    value = """
                                                    {
                                                        "id": "550e8400-e29b-41d4-a716-446655440001",
                                                        "username": "user1",
                                                        "token": null,
                                                        "refreshToken": null
                                                    }
                                                    """
                                            )
                                    })
                    }),
            @ApiResponse(responseCode = "401",
                    description = "No autenticado",
                    content = @Content),
            @ApiResponse(responseCode = "403",
                    description = "Acceso denegado - Requiere ser administrador o el propio usuario",
                    content = @Content),
            @ApiResponse(responseCode = "404",
                    description = "Usuario no encontrado",
                    content = @Content)
    })
    @GetMapping("/profile/{id}")
    public ResponseEntity<UserResponse> getUserById(
            @AuthenticationPrincipal User currentUser,
            @PathVariable UUID id) {
        User user = userService.getUserById(currentUser, id);
        return ResponseEntity.ok(UserResponse.of(user));
    }

    @Operation(summary = "Solicitar recuperación de contraseña",
            description = "Permite a un usuario solicitar un enlace de recuperación de contraseña por email.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204",
                    description = "Solicitud de recuperación enviada con éxito",
                    content = @Content),
            @ApiResponse(responseCode = "400",
                    description = "Datos inválidos",
                    content = @Content),
            @ApiResponse(responseCode = "404",
                    description = "Usuario no encontrado",
                    content = @Content)
    })
    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        userService.forgotPassword(request);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Restablecer contraseña",
            description = "Permite a un usuario restablecer su contraseña usando un token de recuperación.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204",
                    description = "Contraseña restablecida con éxito",
                    content = @Content),
            @ApiResponse(responseCode = "400",
                    description = "Datos inválidos o token expirado/inválido",
                    content = @Content)
    })
    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        userService.resetPassword(request);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Cambiar la contraseña del usuario autenticado",
            description = "Permite al usuario autenticado cambiar su contraseña tras verificar la contraseña actual.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Contraseña cambiada con éxito",
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
                                                        "refreshToken": null,
                                                        "avatar": null
                                                    }
                                                    """
                                            )
                                    })
                    }),
            @ApiResponse(responseCode = "400",
                    description = "Datos inválidos o contraseña antigua incorrecta",
                    content = @Content),
            @ApiResponse(responseCode = "401",
                    description = "No autenticado",
                    content = @Content)
    })
    @PutMapping("/change-password")
    public ResponseEntity<UserResponse> changePassword(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody ChangePasswordRequest request) {
        User updatedUser = userService.changePassword(currentUser, request);
        return ResponseEntity.ok(UserResponse.of(updatedUser));
    }
}