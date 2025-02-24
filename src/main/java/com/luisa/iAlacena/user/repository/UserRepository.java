package com.luisa.iAlacena.user.repository;

import com.luisa.iAlacena.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    @Query("""
            SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END
            FROM User u
            WHERE u.email = :email OR u.username = :username
            """)
    boolean existsByEmailOrUsername(@Param("email") String email, @Param("username") String username);

    @Query("""
            SELECT u
            FROM User u
            WHERE u.username = :username
            """)
    Optional<User> findByUsername(@Param("username") String username);

    @Query("""
            SELECT u
            FROM User u
            WHERE u.activationToken = :token
            """)
    Optional<User> findByActivationToken(@Param("token") String token);

    @Query("""
            SELECT u
            FROM User u
            """)
    Page<User> findAll(Pageable pageable);
}