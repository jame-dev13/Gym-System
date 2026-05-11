package com.jame.dev.gymApp.features.user.application.service;

import com.jame.dev.gymApp.features.user.infrastructure.annotations.CacheEvictUsers;
import com.jame.dev.gymApp.features.user.infrastructure.annotations.PublishUserRecovered;
import com.jame.dev.gymApp.features.user.domain.model.UserEntity;
import com.jame.dev.gymApp.features.auth.domain.exception.AlreadyExistsException;
import com.jame.dev.gymApp.domain.exception.NoActiveException;
import com.jame.dev.gymApp.features.user.domain.exception.UserEntityNotFoundException;
import com.jame.dev.gymApp.features.user.application.contract.UserFactory;
import com.jame.dev.gymApp.features.user.api.request.UserRequest;
import com.jame.dev.gymApp.application.dto.PageDto;
import com.jame.dev.gymApp.features.user.api.response.UserResponse;
import com.jame.dev.gymApp.features.user.api.response.UserMinimalInfoResponse;
import com.jame.dev.gymApp.features.user.domain.repository.UserRepository;
import com.jame.dev.gymApp.features.user.application.contract.UserInactiveService;
import com.jame.dev.gymApp.features.user.application.contract.UserService;
import com.jame.dev.gymApp.application.model.CacheValues;
import com.jame.dev.gymApp.features.user.domain.model.Role;
import com.jame.dev.gymApp.features.user.infrastructure.specification.UserSpecifications;
import com.jame.dev.gymApp.features.user.application.contract.UserUpdater;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@Validated
public class UserServiceImplementation implements
   UserService,
   UserInactiveService {

   private final UserRepository repo;
   private final UserFactory userFactory;
   private final UserUpdater userUpdater;

   @Override
   public Optional<UserEntity> getUserByEmail(String email) {
      return repo.findByEmail(email);
   }

   @Override
   @Transactional(readOnly = true)
   @Cacheable(
      value = CacheValues.USERS,
      keyGenerator = "pageKeyGenerator",
      unless = "#result == null || #result.content.isEmpty()"
   )
   public PageDto<UserResponse> getPage(Pageable pageable, String search) {
      final Specification<UserEntity> spec = new UserSpecifications(search);
      final Page<UserEntity> entityPage = repo.findAll(spec, pageable);
      return userFactory.createPageFrom(entityPage);
   }

   @Override
   @Transactional(readOnly = true)
   @Cacheable(
      value = CacheValues.USERS_INACTIVE,
      key = "#pageable.pageNumber + ':' + #pageable.pageSize",
      unless = "#result == null || #result.content.isEmpty()"
   )
   public PageDto<UserMinimalInfoResponse> getInactivePage(Pageable pageable) {
      final Page<UserMinimalInfoResponse> page = repo.findAllInactives(pageable);
      return userFactory.createMinimalInfoPage(page);
   }

   @Override
   @Transactional
   @CacheEvictUsers
   @PublishUserRecovered
   public void recover(Long id) {
      final UserEntity user = repo.findDeactivatedById(id)
         .orElseThrow(() -> new UserEntityNotFoundException("User not found."));
      final var inputWrapper = new UserRequest(
         user.getName(),
         user.getEmail(),
         user.getPassword(),
         user.getProvider(),
         Set.of(Role.USER)
      );

      userUpdater.apply(user, inputWrapper);
      user.setActive(true);
      repo.saveAndFlush(user);
   }

   @Override
   @Transactional
   @CacheEvictUsers
   public void hardDelete(long id) {
      repo.hardDelete(id);
   }

   @Override
   @Transactional
   @CacheEvict(value = CacheValues.USERS, allEntries = true)
   public UserResponse save(UserRequest input) {
      repo.findByEmail(input.email())
         .ifPresent(user -> {
            if (!user.isActive()) {
               throw new NoActiveException("This address is deactivated.");
            }
            throw new AlreadyExistsException("This account it's used by other user.");
         });

      final UserEntity userCreated = userFactory.createFromInput(input);
      final UserEntity userSaved = repo.saveAndFlush(userCreated);
      return userFactory.createFromEntity(userSaved);
   }

   @Override
   @Transactional(readOnly = true)
   @Cacheable(value = CacheValues.USER, key = "#id")
   public UserResponse getById(long id) {
      return repo.findById(id)
         .map(userFactory::createFromEntity)
         .orElseThrow(() -> new UserEntityNotFoundException("User not found."));
   }

   @Override
   @Transactional
   @CacheEvictUsers
   public UserResponse update(long id, UserRequest input) {
      final var userEntity = repo.findById(id)
         .orElseThrow(() -> new UserEntityNotFoundException("User Not found."));
      userUpdater.apply(userEntity, input);
      final UserEntity userSaved = repo.saveAndFlush(userEntity);
      return userFactory.createFromEntity(userSaved);
   }

   @Override
   @Transactional
   @CacheEvictUsers
   public void softDelete(long id) {
      repo.deleteById(id);
   }
}
