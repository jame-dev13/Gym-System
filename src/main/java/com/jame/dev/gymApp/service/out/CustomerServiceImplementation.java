package com.jame.dev.gymApp.service.out;

import com.jame.dev.gymApp.entity.CustomerEntity;
import com.jame.dev.gymApp.model.dto.in.CustomerDtoInput;
import com.jame.dev.gymApp.repository.CustomerRepository;
import com.jame.dev.gymApp.service.in.CustomerService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerServiceImplementation implements CustomerService {
   private final CustomerRepository repo;

   @Override
   public List<CustomerEntity> getAll() {
      return List.of();
   }

   @Override
   public Optional<CustomerEntity> getById(@NonNull Long id) {
      return Optional.empty();
   }

   @Override
   public CustomerEntity save(@NonNull CustomerDtoInput dto) {
      return null;
   }

   @Override
   public CustomerEntity update(@NonNull CustomerDtoInput dto) {
      return null;
   }

   @Override
   public void softDeleteById(@NonNull Long id) {

   }
}
