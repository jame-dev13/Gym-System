package com.jame.dev.gymApp.service.out;

import com.jame.dev.gymApp.entity.CustomerEntity;
import com.jame.dev.gymApp.entity.UserEntity;
import com.jame.dev.gymApp.exception.AlreadyExistsException;
import com.jame.dev.gymApp.exception.CustomerNotFoundException;
import com.jame.dev.gymApp.exception.NoActiveException;
import com.jame.dev.gymApp.exception.UserEntityNotFoundException;
import com.jame.dev.gymApp.mapper.CustomerMapper;
import com.jame.dev.gymApp.model.dto.in.CustomerDtoInput;
import com.jame.dev.gymApp.repository.CustomerRepository;
import com.jame.dev.gymApp.repository.UserRepository;
import com.jame.dev.gymApp.service.in.CustomerService;
import jakarta.validation.constraints.NotBlank;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerServiceImplementation implements CustomerService {
   private final CustomerRepository repo;
   private final UserRepository userRepo;
   private final CustomerMapper customerMapper;

   @Override
   @Transactional(readOnly = true)
   public Page<@NonNull CustomerEntity> getPage(@NonNull Pageable pageable) {
      return repo.findAllByActiveTrue(pageable);
   }

   @Override
   @Transactional(readOnly = true)
   public Optional<CustomerEntity> getById(@NonNull Long id) {
      return repo.findById(id);
   }

   @Override
   @Transactional(readOnly = true)
   public Optional<CustomerEntity> getByEmail(String email) {
      return repo.findByUser_Email(email);
   }

   @Override
   @Transactional(readOnly = true)
   public Optional<CustomerEntity> getUserByEmail(@NotBlank final String email) {
      return repo.findByUser_Email(email);
   }

   @Override
   @Transactional(readOnly = true)
   public boolean exitsByIdAndCustomerEmail(long id, String email) {
      return repo.existsByIdAndUser_EmailAndActiveTrue(id, email);
   }

   @Override
   public CustomerEntity update(Long id, @NonNull CustomerDtoInput dto) {
      return updateCustomer(id, dto);
   }

   @Override
   @Transactional
   public CustomerEntity save(@NonNull CustomerDtoInput dto) {
      final UserEntity user = userRepo.findByEmail(dto.email())
              .orElseThrow(() -> new UserEntityNotFoundException("User not found."));
      if (!user.isActive()) {
         throw new NoActiveException("This user's account is deactivated.");
      }

      return (CustomerEntity) repo.findByUser(user)
              .map(customer -> {
                 if (!customer.isActive()) {
                    throw new NoActiveException("Account is deactivated.");
                 }
                 throw new AlreadyExistsException("Customer Already exists.");
              })
              .orElseGet(() -> {
                 final CustomerEntity customerEntity = customerMapper.toEntity(dto, user);
                 customerEntity.setCreatedAt(Instant.now());
                 return repo.saveAndFlush(customerEntity);
              });
   }

   @Override
   @Transactional
   public void softDelete(@NonNull Long id) {
      repo.deleteById(id);
   }

   private CustomerEntity updateCustomer(@NonNull Long id, @NonNull CustomerDtoInput dto) {
      final CustomerEntity customer = repo.findById(id)
              .orElseThrow(() -> new CustomerNotFoundException("Customer not found, id: " + id));
      final UserEntity user = customer.getUser();
      user.setEmail(dto.email());
      customer.setUser(user);
      customer.setPhoneContact(dto.contact());
      customer.setUpdatedAt(Instant.now());
      return repo.save(customer);
   }
}
