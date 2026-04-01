package com.jame.dev.gymApp.repository;

import com.jame.dev.gymApp.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AuthenticationChecksQueriesRepository extends JpaRepository<UserEntity, Long> {

   @Query(value = """
           SELECT EXISTS(
                      SELECT 1 FROM users u
                      WHERE u.email = :email
                      AND u.auth_provider = 'LOCAL'
                      )
           """, nativeQuery = true)
   boolean isLocalProvider(@Param("email") final String email);

   @Query(nativeQuery = true,
           value = """
                   SELECT EXISTS(
                      SELECT 1 FROM users u
                      WHERE u.email = :email
                   )
                   """)
   boolean existsDeactivatedByEmail(@Param("email") final String email);

   @Query(nativeQuery = true,
   value = """
           SELECT EXISTS(
              SELECT 1 FROM tokens t
              INNER JOIN users u
              ON u.id = t.user_id
              WHERE t.verified = false AND u.email = :email
                      )
           """)
   boolean existsButNotVerified(@Param("email") final String email);
}
