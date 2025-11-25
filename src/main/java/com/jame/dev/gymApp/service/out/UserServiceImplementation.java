package com.jame.dev.gymApp.service.out;

import com.jame.dev.gymApp.entity.RoleEntity;
import com.jame.dev.gymApp.entity.UserEntity;
import com.jame.dev.gymApp.exception.UserNotFoundException;
import com.jame.dev.gymApp.model.dto.in.UserDtoInput;
import com.jame.dev.gymApp.repository.CustomerRepository;
import com.jame.dev.gymApp.repository.UserRepository;
import com.jame.dev.gymApp.service.in.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImplementation implements UserService {
   private final UserRepository repo;
   private final CustomerRepository customerRepo;
   @Override
   public List<UserEntity> getAll() {
      return repo.findByActiveTrue();
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
      UserEntity userEntity = UserEntity.builder()
              .name(dto.name())
              .email(dto.email())
              .password(dto.password())
              .roles(Set.of(new RoleEntity(null, dto.role())))
              .active(true)
              .build();
      return repo.save(userEntity);
   }

   @Transactional
   @Override
   public UserEntity update(@NonNull Long id, @NonNull UserDtoInput dto) {
      UserEntity userEntity = repo.findById(id)
              .orElseThrow(() -> new UserNotFoundException("User Not Found."));
      userEntity.setName(dto.name());
      userEntity.setEmail(dto.email());
      userEntity.setPassword(dto.password());
      return repo.save(userEntity);
   }

   @Transactional
   @Override
   public void softDeleteById(@NonNull Long id) {
      customerRepo
              .findUserAssociatedByIdUser(id)
              .ifPresentOrElse(u -> repo.softDelete(id), () -> repo.deleteById(id));
   }
}
