package com.jame.dev.gymApp.service.out;

import com.jame.dev.gymApp.entity.CustomerEntity;
import com.jame.dev.gymApp.entity.UserEntity;
import com.jame.dev.gymApp.exception.AlreadyExistsException;
import com.jame.dev.gymApp.exception.UserEntityNotFoundException;
import com.jame.dev.gymApp.exception.UserNotFoundException;
import com.jame.dev.gymApp.factories.UserFactory;
import com.jame.dev.gymApp.model.dto.in.UserDtoInput;
import com.jame.dev.gymApp.model.dto.out.CacheMutated;
import com.jame.dev.gymApp.repository.CustomerRepository;
import com.jame.dev.gymApp.repository.UserRepository;
import com.jame.dev.gymApp.service.in.UserService;
import com.jame.dev.gymApp.updaters.UserUpdater;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImplementation implements UserService {
   private final UserRepository repo;
   private final CustomerRepository customerRepo;
   private final UserFactory userFactory;
   private final UserUpdater userUpdater;
   private final ApplicationEventPublisher eventPublisher;

   @Override
   public Page<@NonNull UserEntity> getPage(@NonNull Pageable pageable) {
      return repo.findAllByActiveTrue(pageable);
   }

   @Override
   public Optional<UserEntity> getUserByEmail(String email) {
      return repo.findByEmail(email);
   }

   @Override
   public Optional<UserEntity> getById(@NonNull Long id) {
      return repo.findById(id);
   }

   @Override
   public UserEntity update(final Long id, @NonNull final UserDtoInput dto) {
      final UserEntity userEntity = repo.findById(id)
              .orElseThrow(() -> new UserNotFoundException("User Not Found."));
      final UserEntity userUpdated = repo.save(userUpdater.apply(userEntity, dto));
      eventPublisher.publishEvent(new CacheMutated("users"));
      return userUpdated;
   }

   @Transactional
   @Override
   public UserEntity save(@NonNull UserDtoInput dto) {
      final boolean userExists = repo.existsByEmail(dto.email());
      if (userExists) {
         throw new AlreadyExistsException("User already exists.");
      }
      final UserEntity userEntity = repo.save(userFactory.createFrom(dto));
      eventPublisher.publishEvent(new CacheMutated("users"));
      return userEntity;
   }

   @Transactional
   @Override
   public void softDelete(@NonNull Long id) {
      final UserEntity userEntity = repo.findById(id)
              .orElseThrow(() -> new UserEntityNotFoundException("User not found."));
      customerRepo.findByUser_EmailAndActiveTrue(userEntity.getEmail())
              .map(CustomerEntity::getId)
              .ifPresent(customerRepo::softDelete);
      repo.softDelete(id);
      eventPublisher.publishEvent(new CacheMutated("users"));
   }
}
