package com.jame.dev.gymApp.features.user.infrastructure.adapter;

import com.jame.dev.gymApp.features.user.api.response.UserMinimalInfoResponse;
import com.jame.dev.gymApp.features.user.domain.model.UserEntity;
import com.jame.dev.gymApp.features.user.domain.repository.UserQueryRepository;
import com.jame.dev.gymApp.features.user.infrastructure.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserQueryJpaRepositoryAdapter implements UserQueryRepository {
   private final UserRepository userRepository;

   @Override
   public Page<UserEntity> findAll(Pageable pageable, Specification<UserEntity> specification) {
      return userRepository.findAll(specification, pageable);
   }

   @Override
   public Page<UserMinimalInfoResponse> findAllDeactivated(Pageable pageable, String search) {
      return userRepository.findAllInactives(search, pageable);
   }

   @Override
   public Optional<UserEntity> findById(long id) {
      return userRepository.findById(id);
   }

   @Override
   public Optional<UserEntity> findByEmail(String email) {
      return userRepository.findByEmail(email);
   }

   @Override
   public Optional<UserEntity> findDeactivatedById(long id) {
      return userRepository.findDeactivatedById(id);
   }
}
