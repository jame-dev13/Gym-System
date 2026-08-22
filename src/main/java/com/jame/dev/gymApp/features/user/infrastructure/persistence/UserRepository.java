package com.jame.dev.gymApp.features.user.infrastructure.persistence;

import com.jame.dev.gymApp.domain.repository.CustomJpaRepository;
import com.jame.dev.gymApp.features.user.api.response.UserMinimalInfoResponse;
import com.jame.dev.gymApp.features.user.domain.model.UserEntity;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends CustomJpaRepository<UserEntity, Long> {

   @Query(nativeQuery = true,
      value = """
         SELECT u.* FROM users u WHERE u.email = :email
         """)
   Optional<UserEntity> findByEmail(@Param("email") @NonNull final String email);

   @Query(value = """
      SELECT
          u.id AS id,
          u.name AS name,
          u.email AS email
      FROM users u
      WHERE u.active = false
        AND u.deleted_at IS NOT NULL
        AND (
              :search IS NULL
              OR TRIM(:search) = ''
              OR u.name ILIKE CONCAT('%', :search, '%')
              OR u.email ILIKE CONCAT('%', :search, '%')
        )
      """,
      countQuery = """
         SELECT COUNT(*)
         FROM users u
         WHERE u.active = false
           AND u.deleted_at IS NOT NULL
           AND (
                 :search IS NULL
                 OR TRIM(:search) = ''
                 OR u.name ILIKE CONCAT('%', :search, '%')
                 OR u.email ILIKE CONCAT('%', :search, '%')
           )
         """,
      nativeQuery = true)
   Page<UserMinimalInfoResponse> findAllInactives(
      @Param("search") String search,
      Pageable pageable
   );

   @NativeQuery("""
      SELECT * FROM users u
      WHERE u.id = :id AND
      u.active = false
      """)
   Optional<UserEntity> findDeactivatedById(@Param("id") long id);

   @Modifying(clearAutomatically = true, flushAutomatically = true)
   @NativeQuery(value = """
      DELETE FROM users u
      WHERE u.id = :id AND
      u.active = false
      """)
   void hardDelete(@Param("id") long id);

   boolean existsByEmail(final String email);

   @NativeQuery("""
      SELECT EXISTS(SELECT 1 FROM users u WHERE u.email = :email AND u.active = false)
      """)
   boolean existsAndIsDeactivatedByEmail(final String email);

   @NativeQuery("""
      SELECT EXISTS(SELECT 1 FROM users u WHERE u.id = :id AND u.active = false)
      """)
   boolean existsByIdAndActiveFalse(@Param("id") final long id);

   boolean existsByIdAndEmail(final Long id, final String email);
}
