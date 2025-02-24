package com.luisa.iAlacena.user.service;

import com.luisa.iAlacena.error.PasswordMismatchException;
import com.luisa.iAlacena.error.UserAlreadyExistsException;
import com.luisa.iAlacena.storage.dto.FileResponse;
import com.luisa.iAlacena.storage.model.FileMetadata;
import com.luisa.iAlacena.storage.service.StorageService;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SendGridMailSender mailSender;
    private final StorageService storageService;

    @Value("${activation.duration:60}")
    private int activationDuration;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, SendGridMailSender mailSender, StorageService storageService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
        this.storageService = storageService;
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

    public FileResponse updateProfilePicture(User currentUser, UUID id, MultipartFile file) {
        if (!currentUser.getId().equals(id)) {
            throw new AccessDeniedException("You can only update your own profile picture");
        }

        User managedUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        String currentAvatar = managedUser.getAvatar();
        if (currentAvatar != null && !currentAvatar.isEmpty()) {
            String filename = currentAvatar.substring(currentAvatar.lastIndexOf("/") + 1);
            try {
                storageService.deleteFile(filename);
                log.info("Deleted previous avatar: {}", filename);
            } catch (Exception e) {
                log.warn("Could not delete previous avatar: {}", filename, e);
            }
        }

        FileResponse response = uploadFile(file);

        managedUser.setAvatar(response.uri());
        userRepository.save(managedUser);

        return response;
    }

    public void deleteProfilePicture(User currentUser, UUID id) {
        if (!currentUser.getId().equals(id)) {
            throw new AccessDeniedException("You can only delete your own profile picture");
        }

        User managedUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        String currentAvatar = managedUser.getAvatar();
        if (currentAvatar != null && !currentAvatar.isEmpty()) {
            String filename = currentAvatar.substring(currentAvatar.lastIndexOf("/") + 1);
            try {
                storageService.deleteFile(filename);
                log.info("Deleted profile picture: {}", filename);
            } catch (Exception e) {
                log.warn("Could not delete profile picture: {}", filename, e);
            }
        }

        managedUser.setAvatar(null);
        userRepository.save(managedUser);
    }

    private FileResponse uploadFile(MultipartFile file) {
        var fileMetadata = storageService.store(file);
        String uri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/download/")
                .path(fileMetadata.getId())
                .toUriString();

        fileMetadata.setURL(uri);

        return FileResponse.builder()
                .id(fileMetadata.getId())
                .name(fileMetadata.getFilename())
                .size(file.getSize())
                .type(file.getContentType())
                .uri(uri)
                .build();
    }

    public Page<User> getAllUsers(User currentUser, Pageable pageable) {
        if (!currentUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
            throw new AccessDeniedException("Only administrators can access the list of users");
        }
        return userRepository.findAll(pageable);
    }

    public User getUserById(User currentUser, UUID id) {
        boolean isAdmin = currentUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        boolean isSelf = currentUser.getId().equals(id);

        if (!isAdmin && !isSelf) {
            throw new AccessDeniedException("You can only access your own profile or must be an administrator");
        }

        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
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