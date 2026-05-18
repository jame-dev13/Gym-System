package com.jame.dev.gymApp.features.auth.domain.repository;

import com.jame.dev.gymApp.features.auth.domain.model.OneTimeTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.Optional;

public interface OneTimeTokenRepository extends JpaRepository<OneTimeTokenEntity, Long> {
   @Modifying
   void deleteByUserId(final long userId);

   boolean existsByUserId(final long userId);

   Optional<OneTimeTokenEntity> findByUserId(final long userId);
}
