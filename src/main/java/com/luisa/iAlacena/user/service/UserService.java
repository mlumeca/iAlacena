package com.luisa.iAlacena.user.service;

import com.luisa.iAlacena.error.PasswordMismatchException;
import com.luisa.iAlacena.error.UserAlreadyExistsException;
import com.luisa.iAlacena.user.dto.CreateUserRequest;
import com.luisa.iAlacena.user.model.User;
import com.luisa.iAlacena.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void registerUser(CreateUserRequest request) {
        // Check if user already exists
        if (userRepository.existsByEmailOrUsername(request.email(), request.username())) {
            throw new UserAlreadyExistsException("user.exists");
        }

        // Verify passwords match
        if (!request.password().equals(request.verifyPassword())) {
            throw new PasswordMismatchException("password.mismatch");
        }

        // Build and save user
        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .build();

        userRepository.save(user);
    }
}