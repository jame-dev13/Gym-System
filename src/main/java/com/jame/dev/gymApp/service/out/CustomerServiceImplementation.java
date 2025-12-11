package com.jame.dev.gymApp.service.out;

import com.jame.dev.gymApp.entity.CustomerEntity;
import com.jame.dev.gymApp.entity.UserEntity;
import com.jame.dev.gymApp.exception.AlreadyExistsException;
import com.jame.dev.gymApp.exception.CustomerNotFoundException;
import com.jame.dev.gymApp.exception.UserNotFoundException;
import com.jame.dev.gymApp.mapper.CustomerMapper;
import com.jame.dev.gymApp.model.dto.in.CustomerDtoInput;
import com.jame.dev.gymApp.repository.CustomerRepository;
import com.jame.dev.gymApp.repository.UserRepository;
import com.jame.dev.gymApp.service.in.CustomerService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerServiceImplementation implements CustomerService {
   private final CustomerRepository repo;
   private final UserRepository userRepo;
   private final CustomerMapper customerMapper;

   @Override
   public Page<@NonNull CustomerEntity> getPage(@NonNull Pageable pageable) {
      return repo.findAllByActiveTrue(pageable);
   }

   @Override
   public Optional<CustomerEntity> getById(@NonNull Long id) {
      return repo.findById(id);
   }

   @Override
   @Transactional
   public CustomerEntity save(@NonNull CustomerDtoInput dto) {
      final boolean userExists = repo.existsByUser_IdAndActiveTrue(dto.userId());
      if (userExists) {
         throw new AlreadyExistsException("User id already exists.");
      }
      final UserEntity user = userRepo.findById(dto.userId())
              .orElseThrow(() -> new UserNotFoundException("User Not Found."));
      final CustomerEntity customerEntity = customerMapper.toEntity(dto, user);
      return repo.save(customerEntity);
   }

   @Override
   public CustomerEntity update(@NonNull Long id, @NonNull CustomerDtoInput customerDtoInput) {
      return updateContact(id, customerDtoInput);
   }

   @Override
   @Transactional
   public void softDelete(@NonNull Long id) {
      final CustomerEntity customer = repo.findById(id)
              .orElseThrow(() -> new CustomerNotFoundException("Customer Not found."));
      userRepo.softDelete(customer.getUser().getId());
      repo.softDelete(id);
   }

   private CustomerEntity updateContact(@NonNull Long id, @NonNull CustomerDtoInput dto) {
      final CustomerEntity customer = repo.findById(id)
              .orElseThrow(() -> new CustomerNotFoundException("Customer not found, id: " + id));
      customer.setPhoneContact(dto.contact());
      return repo.save(customer);
   }
}
