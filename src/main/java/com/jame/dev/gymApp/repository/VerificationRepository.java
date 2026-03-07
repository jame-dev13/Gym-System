package com.jame.dev.gymApp.repository;

import com.jame.dev.gymApp.aspects.annotations.EmailValid;
import com.jame.dev.gymApp.aspects.annotations.NotEmptyNull;
import com.jame.dev.gymApp.entity.VerificationEntity;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface VerificationRepository extends JpaRepository<@NonNull VerificationEntity, @NonNull String> {
   Optional<VerificationEntity> findByUser_Email(@NotEmptyNull final String email);

   @Query(nativeQuery = true,
           value = """
                   SELECT t.* FROM tokens t
                   INNER JOIN users u ON u.id = t.user_id
                   WHERE u.email = :email
                   """
   )
   Optional<VerificationEntity> findDeactivatedByUser_Email(@Param("email") @EmailValid final String email);

   @Query(nativeQuery = true,
   value = """
           SELECT EXISTS(
                      SELECT 1 FROM tokens t
                      INNER JOIN users u ON u.id = t.user_id
                      WHERE u.email = :email AND t.verified = true)
           """)
   boolean existsDeactivatedByUser_Email(@Param("email") @NotEmptyNull final String email);

   boolean existsByUser_EmailAndVerifiedTrue(@NotEmptyNull final String email);
}
