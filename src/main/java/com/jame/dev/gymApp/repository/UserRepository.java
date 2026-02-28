package com.jame.dev.gymApp.repository;

import com.jame.dev.gymApp.entity.UserEntity;
import com.jame.dev.gymApp.repository.common.CustomJpaRepository;
import lombok.NonNull;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends CustomJpaRepository<UserEntity, Long> {

   @Query(nativeQuery = true,
   value = """
           SELECT u.* FROM users u WHERE u.email = :email
           """)
   Optional<UserEntity> findByEmail(@Param ("email") @NonNull final String email);
}
