package com.jame.dev.gymApp.repository;

import com.jame.dev.gymApp.entity.VerificationEntity;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerificationRepository extends JpaRepository<@NonNull VerificationEntity, @NonNull String> {
   Optional<VerificationEntity> findByUser_Email(@NonNull final String email);
}
