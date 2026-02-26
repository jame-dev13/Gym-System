package com.jame.dev.gymApp.service.out;

import com.jame.dev.gymApp.aspects.annotations.CacheEvictUsers;
import com.jame.dev.gymApp.entity.UserEntity;
import com.jame.dev.gymApp.exception.AlreadyExistsException;
import com.jame.dev.gymApp.exception.NoActiveException;
import com.jame.dev.gymApp.exception.UserEntityNotFoundException;
import com.jame.dev.gymApp.factories.PageDtoFactory;
import com.jame.dev.gymApp.factories.in.Factory;
import com.jame.dev.gymApp.model.dto.in.UserDtoInput;
import com.jame.dev.gymApp.model.dto.out.PageDto;
import com.jame.dev.gymApp.model.dto.out.UserDtoOutput;
import com.jame.dev.gymApp.repository.UserRepository;
import com.jame.dev.gymApp.service.in.UserService;
import com.jame.dev.gymApp.shared.enums.CacheValues;
import com.jame.dev.gymApp.updaters.UserUpdater;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Validated
public class UserServiceImplementation implements UserService {
   private final UserRepository repo;
   private final Factory<UserEntity, UserDtoOutput, UserDtoInput> userFactory;
   private final UserUpdater userUpdater;
   private final PageDtoFactory<UserEntity, UserDtoOutput> pageDtoFactory;

   @Override
   public Optional<UserEntity> getUserByEmail(String email) {
      return repo.findByEmail(email);
   }

   @Override
   @Transactional(readOnly = true)
   @Cacheable(
           value = CacheValues.USERS,
           key = "#pageable.pageNumber + ':' + #pageable.pageSize"
   )
   public PageDto<UserDtoOutput> getPage(Pageable pageable) {
      final Page<UserEntity> entityPage = repo.findAll(pageable);
      return pageDtoFactory.createPageDtoFrom(entityPage);
   }

   @Override
   @Transactional
   @CacheEvict(value = CacheValues.USERS, allEntries = true)
   public UserDtoOutput save(UserDtoInput input) {
      return (UserDtoOutput) repo.findByEmail(input.email())
              .map(user -> {
                 if (!user.isActive()) {
                    throw new NoActiveException("User exists but isn't active.");
                 }
                 throw new AlreadyExistsException("User already exists.");
              }).orElseGet(() -> {
                 final UserEntity userCreated = userFactory.createFromInput(input);
                 final UserEntity userSaved = repo.saveAndFlush(userCreated);
                 return userFactory.createFromEntity(userSaved);
              });
   }

   @Override
   @Transactional(readOnly = true)
   @Cacheable(value = CacheValues.USER, key = "#id")
   public Optional<UserDtoOutput> getById(long id) {
      final var entity = repo.findById(id)
              .orElseThrow(() -> new UserEntityNotFoundException("User not found."));
      return Optional.of(userFactory.createFromEntity(entity));
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
