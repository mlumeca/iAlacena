package com.luisa.iAlacena.user.repository;

import com.luisa.iAlacena.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByEmailOrUsername(String email, String username);
}