package com.luisa.iAlacena.security.jwt.refresh;

import com.luisa.iAlacena.security.jwt.access.JwtService;
import com.luisa.iAlacena.user.dto.UserResponse;
import com.luisa.iAlacena.user.model.User;
import com.luisa.iAlacena.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Value("${jwt.refresh.duration}")
    private int durationInMinutes;

    public RefreshToken create(User user) {
        refreshTokenRepository.deleteByUser(user);
        return refreshTokenRepository.save(
                RefreshToken.builder()
                        .user(user)
                        //.user(u)
                        //.token(UUID.randomUUID().toString())
                        .expireAt(Instant.now().plusSeconds(durationInMinutes*60))
                        .build()
        );
    }
}