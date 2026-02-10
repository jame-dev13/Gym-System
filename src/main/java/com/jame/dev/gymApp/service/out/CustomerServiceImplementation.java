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
import jakarta.validation.constraints.NotBlank;
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
   public CustomerEntity update(Long id, @NonNull CustomerDtoInput dto) {
      return updateCustomer(id, dto);
   }

   @Override
   @Transactional
   public CustomerEntity save(@NonNull CustomerDtoInput dto) {
      final boolean userExists = repo.existsByUser_EmailAndActiveTrue(dto.email());
      if (userExists) {
         throw new AlreadyExistsException("User id already exists.");
      }
      final UserEntity user = userRepo.findByEmail(dto.email())
              .orElseThrow(() -> new UserNotFoundException("User Not Found."));
      final CustomerEntity customerEntity = customerMapper.toEntity(dto, user);
      return repo.save(customerEntity);
   }


   @Override
   @Transactional
   public void softDelete(@NonNull Long id) {
      final CustomerEntity customer = repo.findById(id)
              .orElseThrow(() -> new CustomerNotFoundException("Customer Not found."));
      Optional.of(customer.getUser())
              .map(UserEntity::getId)
              .ifPresent(userRepo::softDelete);
      repo.softDelete(id);
   }

   private CustomerEntity updateCustomer(@NonNull Long id, @NonNull CustomerDtoInput dto) {
      final CustomerEntity customer = repo.findById(id)
              .orElseThrow(() -> new CustomerNotFoundException("Customer not found, id: " + id));
      final UserEntity user = customer.getUser();
      user.setEmail(dto.email());
      customer.setUser(user);
      customer.setPhoneContact(dto.contact());
      return repo.save(customer);
   }

   @Override
   public Optional<CustomerEntity> getUserByEmail(@NotBlank final String email) {
      return repo.findByUser_EmailAndActiveTrue(email);
   }

   @Override
   public boolean exitsByIdAndCustomerEmail(long id, String email) {
      return repo.existsByIdAndUser_EmailAndActiveTrue(id, email);
   }

   @Override
   public Optional<CustomerEntity> getByEmail(String email) {
      return repo.findByUser_EmailAndActiveTrue(email);
   }
}
