package com.luisa.iAlacena;

import com.luisa.iAlacena.error.PasswordMismatchException;
import com.luisa.iAlacena.error.UserAlreadyExistsException;
import com.luisa.iAlacena.storage.dto.FileResponse;
import com.luisa.iAlacena.storage.model.FileMetadata;
import com.luisa.iAlacena.storage.service.StorageService;
import com.luisa.iAlacena.user.dto.*;
import com.luisa.iAlacena.user.model.User;
import com.luisa.iAlacena.user.model.UserRole;
import com.luisa.iAlacena.user.repository.UserRepository;
import com.luisa.iAlacena.user.service.UserService;
import com.luisa.iAlacena.util.SendGridMailSender;
import com.luisa.iAlacena.shoppingcart.model.ShoppingCart;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private SendGridMailSender mailSender;

    @Mock
    private StorageService storageService;

    @InjectMocks
    private UserService userService;

    private User user;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user = User.builder()
                .id(userId)
                .username("testuser")
                .email("test@example.com")
                .password("encodedPassword")
                .role(UserRole.USER)
                .activationToken("activation-token")
                .createdAt(LocalDateTime.now().minusMinutes(10))
                .shoppingCart(new ShoppingCart())
                .build();
        user.getShoppingCart().setUser(user);
    }

    @Test
    void registerUser_success() throws IOException {
        // Arrange
        CreateUserRequest request = new CreateUserRequest("newuser", "password", "password", "new@example.com");
        when(userRepository.existsByEmailOrUsername("new@example.com", "newuser")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        doNothing().when(mailSender).sendMail(anyString(), anyString(), anyString());

        // Act
        User result = userService.registerUser(request);

        // Assert
        assertNotNull(result);
        assertEquals("newuser", result.getUsername());
        assertEquals("new@example.com", result.getEmail());
        assertEquals("encodedPassword", result.getPassword());
        assertEquals(UserRole.USER, result.getRole());
        assertNotNull(result.getActivationToken());

        verify(userRepository, times(1)).existsByEmailOrUsername("new@example.com", "newuser");
        verify(passwordEncoder, times(1)).encode("password");
        verify(userRepository, times(1)).save(any(User.class));
        verify(mailSender, times(1)).sendMail(eq("new@example.com"), eq("Activa tu cuenta en Alacena"), anyString());
    }

    @Test
    void registerUser_userExists_throwsException() throws IOException {
        // Arrange
        CreateUserRequest request = new CreateUserRequest("newuser", "password", "password", "new@example.com");
        when(userRepository.existsByEmailOrUsername("new@example.com", "newuser")).thenReturn(true);

        // Act & Assert
        UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class, () -> {
            userService.registerUser(request);
        });
        assertEquals("user.exists", exception.getMessage());

        verify(userRepository, times(1)).existsByEmailOrUsername("new@example.com", "newuser");
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
        verify(mailSender, never()).sendMail(anyString(), anyString(), anyString());
    }

    @Test
    void registerUser_passwordMismatch_throwsException() {
        // Arrange
        CreateUserRequest request = new CreateUserRequest("newuser", "password", "different", "new@example.com");
        when(userRepository.existsByEmailOrUsername("new@example.com", "newuser")).thenReturn(false);

        // Act & Assert
        PasswordMismatchException exception = assertThrows(PasswordMismatchException.class, () -> {
            userService.registerUser(request);
        });
        assertEquals("password.mismatch", exception.getMessage());

        verify(userRepository, times(1)).existsByEmailOrUsername("new@example.com", "newuser");
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void editUserProfile_success() {
        // Arrange
        EditUserRequest request = new EditUserRequest("updateduser", "updated@example.com");
        when(userRepository.existsByEmailOrUsername("updated@example.com", "updateduser")).thenReturn(false);
        when(userRepository.save(user)).thenReturn(user);

        // Act
        User result = userService.editUserProfile(user, request);

        // Assert
        assertEquals("updateduser", result.getUsername());
        assertEquals("updated@example.com", result.getEmail());

        verify(userRepository, times(2)).existsByEmailOrUsername(anyString(), anyString());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void editUserProfile_usernameExists_throwsException() {
        // Arrange
        EditUserRequest request = new EditUserRequest("takenuser", "test@example.com");
        when(userRepository.existsByEmailOrUsername("test@example.com", "takenuser")).thenReturn(true);

        // Act & Assert
        UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class, () -> {
            userService.editUserProfile(user, request);
        });
        assertEquals("user.exists", exception.getMessage());

        verify(userRepository, times(1)).existsByEmailOrUsername("test@example.com", "takenuser");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateProfilePicture_success() throws IOException {
        // Arrange
        MultipartFile file = mock(MultipartFile.class);
        FileMetadata fileMetadata = mock(FileMetadata.class); // Mockeamos FileMetadata en lugar de instanciarlo
        FileResponse fileResponse = FileResponse.builder()
                .id("fileId")
                .name("avatar.jpg")
                .uri("http://localhost:8080/download/fileId")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(storageService.store(file)).thenReturn(fileMetadata); // Devolvemos el mock
        when(fileMetadata.getId()).thenReturn("fileId"); // Simulamos el método getId
        when(fileMetadata.getFilename()).thenReturn("avatar.jpg"); // Simulamos el método getFilename
        when(file.getSize()).thenReturn(1024L);
        when(file.getContentType()).thenReturn("image/jpeg");
        when(userRepository.save(user)).thenReturn(user);

        // Act
        FileResponse result = userService.updateProfilePicture(user, userId, file);

        // Assert
        assertEquals("http://localhost:8080/download/fileId", result.uri());
        assertEquals("avatar.jpg", result.name());
        verify(storageService, times(1)).store(file);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void updateProfilePicture_accessDenied_throwsException() {
        // Arrange
        MultipartFile file = mock(MultipartFile.class);
        UUID differentId = UUID.randomUUID();

        // Act & Assert
        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
            userService.updateProfilePicture(user, differentId, file);
        });
        assertEquals("You can only update your own profile picture", exception.getMessage());

        verify(userRepository, never()).findById(any(UUID.class));
        verify(storageService, never()).store(any(MultipartFile.class));
    }

    @Test
    void activateAccount_success() {
        // Arrange
        when(userRepository.findByActivationToken("activation-token")).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        // Act
        User result = userService.activateAccount("activation-token");

        // Assert
        assertTrue(result.isVerified());
        assertNull(result.getActivationToken());
        verify(userRepository, times(1)).findByActivationToken("activation-token");
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void activateAccount_expiredToken_throwsException() {
        // Arrange
        user.setCreatedAt(LocalDateTime.now().minusHours(2)); // Más de 60 minutos
        when(userRepository.findByActivationToken("activation-token")).thenReturn(Optional.of(user));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.activateAccount("activation-token");
        });
        assertEquals("Invalid or expired activation token", exception.getMessage());

        verify(userRepository, times(1)).findByActivationToken("activation-token");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void forgotPassword_success() throws IOException {
        // Arrange
        ForgotPasswordRequest request = new ForgotPasswordRequest("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        doNothing().when(mailSender).sendMail(anyString(), anyString(), anyString());

        // Act
        userService.forgotPassword(request);

        // Assert
        verify(userRepository, times(1)).findByUsername("testuser");
        verify(userRepository, times(1)).save(user);
        verify(mailSender, times(1)).sendMail(eq("test@example.com"), eq("Recuperación de Contraseña - Alacena"), anyString());
        assertNotNull(user.getResetPasswordToken());
        assertNotNull(user.getResetPasswordTokenExpiry());
    }

    @Test
    void resetPassword_success() {
        // Arrange
        user.setResetPasswordToken("reset-token");
        user.setResetPasswordTokenExpiry(LocalDateTime.now().plusHours(1));
        ResetPasswordRequest request = new ResetPasswordRequest("reset-token", "newPassword");
        when(userRepository.findByResetPasswordToken("reset-token")).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newPassword")).thenReturn("newEncodedPassword");
        when(userRepository.save(user)).thenReturn(user);

        // Act
        userService.resetPassword(request);

        // Assert
        assertEquals("newEncodedPassword", user.getPassword());
        assertNull(user.getResetPasswordToken());
        assertNull(user.getResetPasswordTokenExpiry());
        verify(userRepository, times(1)).findByResetPasswordToken("reset-token");
        verify(passwordEncoder, times(1)).encode("newPassword");
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void changePassword_success() {
        // Arrange
        ChangePasswordRequest request = new ChangePasswordRequest("oldPassword", "newPassword");
        when(passwordEncoder.matches("oldPassword", "encodedPassword")).thenReturn(true);
        when(passwordEncoder.encode("newPassword")).thenReturn("newEncodedPassword");
        when(userRepository.save(user)).thenReturn(user);

        // Act
        User result = userService.changePassword(user, request);

        // Assert
        assertEquals("newEncodedPassword", result.getPassword());
        verify(passwordEncoder, times(1)).matches("oldPassword", "encodedPassword");
        verify(passwordEncoder, times(1)).encode("newPassword");
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void changePassword_incorrectOldPassword_throwsException() {
        // Arrange
        ChangePasswordRequest request = new ChangePasswordRequest("wrongPassword", "newPassword");
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        // Act & Assert
        PasswordMismatchException exception = assertThrows(PasswordMismatchException.class, () -> {
            userService.changePassword(user, request);
        });
        assertEquals("oldPassword.incorrect", exception.getMessage());

        verify(passwordEncoder, times(1)).matches("wrongPassword", "encodedPassword");
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }
}