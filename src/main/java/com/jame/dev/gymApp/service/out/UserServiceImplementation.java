package com.jame.dev.gymApp.service.out;

import com.jame.dev.gymApp.entity.CustomerEntity;
import com.jame.dev.gymApp.entity.UserEntity;
import com.jame.dev.gymApp.exception.AlreadyExistsException;
import com.jame.dev.gymApp.exception.UserEntityNotFoundException;
import com.jame.dev.gymApp.exception.UserNotFoundException;
import com.jame.dev.gymApp.factories.UserFactory;
import com.jame.dev.gymApp.mapper.RoleMapper;
import com.jame.dev.gymApp.model.dto.in.UserDtoInput;
import com.jame.dev.gymApp.repository.CustomerRepository;
import com.jame.dev.gymApp.repository.RoleRepository;
import com.jame.dev.gymApp.repository.UserRepository;
import com.jame.dev.gymApp.service.in.UserService;
import com.jame.dev.gymApp.updaters.UserUpdater;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImplementation implements UserService {
   private final UserRepository repo;
   private final RoleRepository roleRepository;
   private final CustomerRepository customerRepo;
   private final PasswordEncoder passwordEncoder;
   private final UserFactory userFactory;
   private final UserUpdater userUpdater;
   private final RoleMapper roleMapper;

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

   @Transactional
   @Override
   public UserEntity save(@NonNull UserDtoInput dto) {
      final boolean userExists = repo.existsByEmail(dto.email());
      if (userExists) {
         throw new AlreadyExistsException("User already exists.");
      }
      final UserEntity userEntity = userFactory.createFrom(dto);
      return repo.save(userEntity);
   }

   @Transactional
   @Override
   public UserEntity update(@NonNull Long id, @NonNull UserDtoInput dto) {
      final UserEntity userEntity = repo.findById(id)
              .orElseThrow(() -> new UserNotFoundException("User Not Found."));
      final UserEntity userUpdated = userUpdater.apply(userEntity, dto);
      return repo.save(userUpdated);
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
   }
}
