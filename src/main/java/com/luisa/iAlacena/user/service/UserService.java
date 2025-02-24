package com.luisa.iAlacena.user.service;

import com.luisa.iAlacena.error.PasswordMismatchException;
import com.luisa.iAlacena.error.UserAlreadyExistsException;
import com.luisa.iAlacena.user.dto.CreateUserRequest;
import com.luisa.iAlacena.user.dto.EditUserRequest;
import com.luisa.iAlacena.user.model.User;
import com.luisa.iAlacena.user.model.UserRole;
import com.luisa.iAlacena.user.repository.UserRepository;
import com.luisa.iAlacena.util.SendGridMailSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SendGridMailSender mailSender;

    @Value("${activation.duration:60}")
    private int activationDuration;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, SendGridMailSender mailSender) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
    }

    public User registerUser(CreateUserRequest request) {
        return registerUser(request, UserRole.USER);
    }

    public User registerUser(CreateUserRequest request, UserRole defaultRole) {
        if (userRepository.existsByEmailOrUsername(request.email(), request.username())) {
            throw new UserAlreadyExistsException("user.exists");
        }
        if (!request.password().equals(request.verifyPassword())) {
            throw new PasswordMismatchException("password.mismatch");
        }

        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(defaultRole)
                .activationToken(generateRandomActivationCode())
                .build();

        user = userRepository.save(user);

        try {
            String message = "Tu código de activación es: " + user.getActivationToken() +
                    "\nUsa este enlace para activar tu cuenta: http://localhost:8080/auth/activate?token=" + user.getActivationToken();
            mailSender.sendMail(user.getEmail(), "Activa tu cuenta en Alacena", message);
            log.info("Activation email sent to: {}", user.getEmail());
        } catch (IOException e) {
            log.error("Failed to send activation email to {}: {}", user.getEmail(), e.getMessage());
            throw new RuntimeException("Error sending activation email: " + e.getMessage(), e);
        }

        return user;
    }

    public User editUserProfile(User currentUser, EditUserRequest request) {
        if (!request.username().equals(currentUser.getUsername()) &&
                userRepository.existsByEmailOrUsername(request.email(), request.username())) {
            throw new UserAlreadyExistsException("user.exists");
        }
        if (!request.email().equals(currentUser.getEmail()) &&
                userRepository.existsByEmailOrUsername(request.email(), request.username())) {
            throw new UserAlreadyExistsException("user.exists");
        }

        currentUser.setUsername(request.username());
        currentUser.setEmail(request.email());

        return userRepository.save(currentUser);
    }

    public Page<User> getAllUsers(User currentUser, Pageable pageable) {
        if (!currentUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
            throw new AccessDeniedException("Only administrators can access the list of users");
        }
        return userRepository.findAll(pageable);
    }

    private String generateRandomActivationCode() {
        return UUID.randomUUID().toString();
    }

    public User activateAccount(String token) {
        return userRepository.findByActivationToken(token)
                .filter(user -> ChronoUnit.MINUTES.between(user.getCreatedAt(), java.time.LocalDateTime.now()) < activationDuration)
                .map(user -> {
                    user.setVerified(true);
                    user.setActivationToken(null);
                    return userRepository.save(user);
                })
                .orElseThrow(() -> new RuntimeException("Invalid or expired activation token"));
    }
}