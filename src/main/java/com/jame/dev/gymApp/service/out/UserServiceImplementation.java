package com.jame.dev.gymApp.service.out;

import com.jame.dev.gymApp.entity.RoleEntity;
import com.jame.dev.gymApp.entity.UserEntity;
import com.jame.dev.gymApp.exception.AlreadyExistsException;
import com.jame.dev.gymApp.exception.UserNotFoundException;
import com.jame.dev.gymApp.mapper.RoleMapper;
import com.jame.dev.gymApp.mapper.UserMapper;
import com.jame.dev.gymApp.model.dto.in.UserDtoInput;
import com.jame.dev.gymApp.repository.CustomerRepository;
import com.jame.dev.gymApp.repository.UserRepository;
import com.jame.dev.gymApp.service.in.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImplementation implements UserService {
   private final UserRepository repo;
   private final CustomerRepository customerRepo;
   private final PasswordEncoder passwordEncoder;
   private final UserMapper userMapper;
   private final RoleMapper roleMapper;

   @Override
   public List<UserEntity> getAll() {
      return repo.findAll();
   }

   @Override
   public List<UserEntity> getActives() {
      return repo.findAllByActiveTrue();
   }

   @Override
   public Page<@NonNull UserEntity> getPageOfActives(@NonNull Pageable pageable) {
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
      boolean userExists = repo.existsByEmail(dto.email());
      if(userExists){
         throw new AlreadyExistsException("User already exists.");
      }
      Set<RoleEntity> roles = dto.roles().stream()
              .map(roleMapper::toEntity).collect(Collectors.toSet());
      UserEntity userEntity = userMapper.toEntity(dto, roles);
      userEntity.setPassword(passwordEncoder.encode(dto.password()));
      return repo.save(userEntity);
   }

   @Transactional
   @Override
   public UserEntity update(@NonNull Long id, @NonNull UserDtoInput dto) {
      UserEntity userEntity = repo.findById(id)
              .orElseThrow(() -> new UserNotFoundException("User Not Found."));
      userEntity.setName(dto.name());
      userEntity.setEmail(dto.email());
      userEntity.setPassword(passwordEncoder.encode(dto.password()));
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
