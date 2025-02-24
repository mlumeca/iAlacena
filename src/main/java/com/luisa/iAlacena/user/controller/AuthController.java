package com.luisa.iAlacena.user.controller;

import com.luisa.iAlacena.security.jwt.access.JwtService;
import com.luisa.iAlacena.security.jwt.refresh.RefreshTokenService;
import com.luisa.iAlacena.user.dto.LoginRequest;
import com.luisa.iAlacena.user.dto.LogoutRequest;
import com.luisa.iAlacena.user.dto.UserResponse;
import com.luisa.iAlacena.user.model.User;
import com.luisa.iAlacena.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autentificación", description = "El controlador de la autentificación.")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public AuthController(AuthenticationManager authenticationManager, UserService userService,
                          JwtService jwtService, RefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }
    
    @Operation(summary = "Activar cuenta con token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cuenta activada con éxito",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Token inválido o expirado",
                    content = @Content)
    })
    @GetMapping("/activate")
    public ResponseEntity<UserResponse> activateAccount(@RequestParam("token") String token) {
        log.info("Received activation request with token: {}", token);
        User user = userService.activateAccount(token);
        log.info("Account activated for user: {}", user.getUsername());
        return ResponseEntity.ok(UserResponse.of(user));
    }

    @Operation(summary = "Inicio de sesión.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Se ha iniciado sesión.",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UserResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    value = """
                                                    {
                                                        "username": "juanperez",
                                                        "password": "password1234",
                                                    }
                                                    """
                                            )
                                    })
                    }),
            @ApiResponse(responseCode = "401",
                    description = "¡Error!, Datos incorrectos.",
                    content = @Content)
    })
    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Received login request for username: {}", request.username());
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password())
            );
            log.info("Authentication successful for user: {}", request.username());
            User user = (User) authentication.getPrincipal();
            log.info("User details - Enabled: {}, Authorities: {}", user.isEnabled(), user.getAuthorities());
            String accessToken = jwtService.generateAccessToken(user);
            String refreshToken = refreshTokenService.create(user).getToken();
            log.info("Tokens generated - Access: {}, Refresh: {}", accessToken, refreshToken);
            return ResponseEntity.ok(UserResponse.of(user, accessToken, refreshToken));
        } catch (BadCredentialsException e) {
            log.error("Invalid credentials for username: {}", request.username());
            throw new RuntimeException("Invalid username or password", e);
        } catch (AuthenticationException e) {
            log.error("Authentication failed for username: {}. Reason: {}", request.username(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during login for username: {}. Error: {}", request.username(), e.getMessage());
            throw e;
        }
    }

    @Operation(summary = "Cerrado de sesión.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204",
                    description = "Se ha cerrado la sesión.",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UserResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    value = """
                                                    {
                                                        "refreshToken": "938b04e9-b6cb-4759-a59f-814d7a780fe2",
                                                    }
                                                    """
                                            )
                                    })
                    }),
            @ApiResponse(responseCode = "401",
                    description = "Sesión caducada.",
                    content = @Content)
    })
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody LogoutRequest request) {
        log.info("Received logout request with refresh token: {}", request.refreshToken());
        refreshTokenService.invalidateRefreshToken(request.refreshToken());
        log.info("Refresh token invalidated successfully");
        return ResponseEntity.noContent().build();
    }
}