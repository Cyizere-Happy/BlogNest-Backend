package com.blognest.repositories;

import com.blognest.models.RefreshToken;
import com.blognest.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByUser(User user);

    @Query("SELECT r FROM RefreshToken r WHERE r.user.id = :userId")
    Optional<RefreshToken> findByUserId(@Param("userId") UUID userId);

    @Modifying
    void deleteByUser(User user);
}
