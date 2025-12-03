package com.jame.dev.gymApp.repository;

import com.jame.dev.gymApp.entity.VerificationEntity;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationRepository extends JpaRepository<@NonNull VerificationEntity, @NonNull String> {
}
