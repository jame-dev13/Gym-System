package com.jame.dev.gymApp.features.user.domain.repository;

public interface UserValidationRepository {
   boolean existsByEmail(final String email);

   boolean existsAndIsDeactivatedByEmail(final String email);

      boolean existsByIdAndEmail(final long id, final String email);

   boolean existsByIdAndNotActive(final long id);
}
