package com.jame.dev.gymApp.repository;

import com.jame.dev.gymApp.entity.UserEntity;
import com.jame.dev.gymApp.model.dto.out.UserMinimalInfo;
import com.jame.dev.gymApp.repository.common.CustomJpaRepository;
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
      WHERE u.active = false AND
      u.deleted_at IS NOT NULL
      """, countQuery = """
      SELECT COUNT(*) FROM users u
      WHERE u.active = false AND
      u.deleted_at IS NOT NULL
      """,
      nativeQuery = true)
   Page<UserMinimalInfo> findAllInactives(Pageable pageable);

   @NativeQuery("""
      SELECT * FROM users u
      WHERE u.id = :id AND
      u.active = false
      """)
   Optional<UserEntity> findDeactivatedById(@Param("id") long id);

   @Modifying(clearAutomatically = true, flushAutomatically = true)
   @NativeQuery("""
      UPDATE users
      SET active = true
      WHERE id = :id AND active = false
      """)
   void recoverUser(@Param("id") long id);

   @Modifying(clearAutomatically = true, flushAutomatically = true)
   @NativeQuery(value = """
      DELETE FROM users u
      WHERE u.id = :id AND
      u.active = false
      """)
   void hardDelete(@Param("id") long id);
}
