package com.jame.dev.gymApp.service.out;

import com.jame.dev.gymApp.entity.UserEntity;
import com.jame.dev.gymApp.model.dto.in.UserDtoInput;
import com.jame.dev.gymApp.repository.UserRepository;
import com.jame.dev.gymApp.service.in.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImplementation implements UserService {
   private final UserRepository repo;

   @Override
   public Optional<UserEntity> getUserByEmail(String email) {
      return Optional.empty();
   }

   @Override
   public List<UserEntity> getAll() {
      return List.of();
   }

   @Override
   public Optional<UserEntity> getById(@NonNull Long id) {
      return Optional.empty();
   }

   @Override
   public UserEntity save(@NonNull UserDtoInput dto) {
      return null;
   }

   @Override
   public UserEntity update(@NonNull UserDtoInput dto) {
      return null;
   }

   @Override
   public void softDeleteById(@NonNull Long id) {

   }
}
