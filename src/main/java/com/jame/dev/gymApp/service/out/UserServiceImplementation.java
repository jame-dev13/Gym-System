package com.jame.dev.gymApp.service.out;

import com.jame.dev.gymApp.aspects.annotations.aspects.CacheEvictUsers;
import com.jame.dev.gymApp.aspects.annotations.aspects.PublishUserRecovered;
import com.jame.dev.gymApp.entity.UserEntity;
import com.jame.dev.gymApp.exception.AlreadyExistsException;
import com.jame.dev.gymApp.exception.NoActiveException;
import com.jame.dev.gymApp.exception.UserEntityNotFoundException;
import com.jame.dev.gymApp.factories.in.UserFactory;
import com.jame.dev.gymApp.model.dto.in.UserDtoInput;
import com.jame.dev.gymApp.model.dto.out.PageDto;
import com.jame.dev.gymApp.model.dto.out.UserDtoOutput;
import com.jame.dev.gymApp.model.dto.out.UserMinimalInfo;
import com.jame.dev.gymApp.repository.UserRepository;
import com.jame.dev.gymApp.service.in.UserInactiveService;
import com.jame.dev.gymApp.service.in.UserService;
import com.jame.dev.gymApp.shared.enums.CacheValues;
import com.jame.dev.gymApp.shared.enums.Role;
import com.jame.dev.gymApp.updaters.in.UserUpdater;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
      key = "#pageable.pageNumber + ':' + #pageable.pageSize",
      unless = "#result == null || #result.content.isEmpty()"
   )
   public PageDto<UserDtoOutput> getPage(Pageable pageable) {
      final Page<UserEntity> entityPage = repo.findAll(pageable);
      return userFactory.createPageFrom(entityPage);
   }

   @Override
   @Transactional(readOnly = true)
   @Cacheable(
      value = CacheValues.USERS_INACTIVE,
      key = "#pageable.pageNumber + ':' + #pageable.pageSize",
      unless = "#result == null || #result.content.isEmpty()"
   )
   public PageDto<UserMinimalInfo> getInactivePage(Pageable pageable) {
      final Page<UserMinimalInfo> page = repo.findAllInactives(pageable);
      return userFactory.createMinimalInfoPage(page);
   }

   @Override
   @Transactional
   @CacheEvictUsers
   @PublishUserRecovered
   public void recover(Long id) {
      final UserEntity user = repo.findDeactivatedById(id)
         .orElseThrow(() -> new UserEntityNotFoundException("User not found."));
      final var inputWrapper = new UserDtoInput(
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
   public UserDtoOutput save(UserDtoInput input) {
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
   public UserDtoOutput getById(long id) {
      return repo.findById(id)
         .map(userFactory::createFromEntity)
         .orElseThrow(() -> new UserEntityNotFoundException("User not found."));
   }

   @Override
   @Transactional
   @CacheEvictUsers
   public UserDtoOutput update(long id, UserDtoInput input) {
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
