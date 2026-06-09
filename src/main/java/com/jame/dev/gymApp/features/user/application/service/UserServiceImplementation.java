package com.jame.dev.gymApp.features.user.application.service;

import com.jame.dev.gymApp.application.dto.PageDto;
import com.jame.dev.gymApp.application.model.CacheValues;
import com.jame.dev.gymApp.domain.exception.NoActiveException;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogAction;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogEntityType;
import com.jame.dev.gymApp.features.audit.infrastructure.annotation.AuditLog;
import com.jame.dev.gymApp.features.auth.domain.exception.AlreadyExistsException;
import com.jame.dev.gymApp.features.user.api.request.UserRequest;
import com.jame.dev.gymApp.features.user.api.response.UserMinimalInfoResponse;
import com.jame.dev.gymApp.features.user.api.response.UserResponse;
import com.jame.dev.gymApp.features.user.application.contract.UserFactory;
import com.jame.dev.gymApp.features.user.application.contract.UserInactiveService;
import com.jame.dev.gymApp.features.user.application.contract.UserService;
import com.jame.dev.gymApp.features.user.application.contract.UserUpdater;
import com.jame.dev.gymApp.features.user.domain.exception.UserEntityNotFoundException;
import com.jame.dev.gymApp.features.user.domain.model.Role;
import com.jame.dev.gymApp.features.user.domain.model.UserEntity;
import com.jame.dev.gymApp.features.user.infrastructure.persistence.UserRepository;
import com.jame.dev.gymApp.features.user.infrastructure.annotations.CacheEvictUsers;
import com.jame.dev.gymApp.features.user.infrastructure.annotations.PublishUserRecovered;
import com.jame.dev.gymApp.features.user.infrastructure.specification.UserSpecifications;
import com.jame.dev.gymApp.infrastructure.sort.SortPropertyResolver;
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
   private final SortPropertyResolver userSortApplicationResolver;

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
      final Pageable pageableWrapped = userSortApplicationResolver.resolve(pageable);
      final Specification<UserEntity> spec = new UserSpecifications(search);
      final Page<UserEntity> entityPage = repo.findAll(spec, pageableWrapped);
      return userFactory.createPageFrom(entityPage);
   }

   @Override
   @Transactional(readOnly = true)
   @Cacheable(
      value = CacheValues.USERS_INACTIVE,
      keyGenerator = "pageKeyGenerator",
      unless = "#result == null || #result.content.isEmpty()"
   )
   public PageDto<UserMinimalInfoResponse> getInactivePage(Pageable pageable, String search) {
      final Pageable pageableWrapped = userSortApplicationResolver.resolve(pageable);
      final Page<UserMinimalInfoResponse> page = repo.findAllInactives(search, pageableWrapped);
      return userFactory.createMinimalInfoPage(page);
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
   @PublishUserRecovered
   @AuditLog(
      action = AuditLogAction.RECOVER,
      entityType = AuditLogEntityType.USER,
      entityId = "#id"
   )
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
   @CacheEvict(value = CacheValues.USERS, allEntries = true)
   @AuditLog(
      action = AuditLogAction.INSERT,
      entityType = AuditLogEntityType.USER,
      input = "#input",
      entityId = "#result.id",
      result = "#result"
   )
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
   @Transactional
   @CacheEvictUsers
   @AuditLog(
      action = AuditLogAction.UPDATE,
      entityType = AuditLogEntityType.USER,
      input = "#input",
      entityId = "#id",
      result = "#result"
   )
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
   @AuditLog(
      action = AuditLogAction.DELETE,
      entityType = AuditLogEntityType.USER,
      entityId = "#id"
   )
   public void softDelete(long id) {
      repo.deleteById(id);
   }

   @Override
   @Transactional
   @CacheEvictUsers
   @AuditLog(
      action = AuditLogAction.HARD_DELETE,
      entityType = AuditLogEntityType.USER,
      entityId = "#id"
   )
   public void hardDelete(long id) {
      repo.hardDelete(id);
   }
}
