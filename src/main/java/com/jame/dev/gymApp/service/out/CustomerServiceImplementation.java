package com.jame.dev.gymApp.service.out;

import com.jame.dev.gymApp.entity.CustomerEntity;
import com.jame.dev.gymApp.entity.UserEntity;
import com.jame.dev.gymApp.exception.AlreadyExistsException;
import com.jame.dev.gymApp.exception.CustomerNotFoundException;
import com.jame.dev.gymApp.exception.NoOperationException;
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

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerServiceImplementation implements CustomerService {
   private final CustomerRepository repo;
   private final UserRepository userRepo;
  private final CustomerMapper customerMapper;

   @Override
   public List<CustomerEntity> getAll() {
      return repo.findAll();
   }

   @Override
   public List<CustomerEntity> getActives() {
      return repo.findAllByActiveTrue();
   }

   @Override
   public Page<@NonNull CustomerEntity> getPageOfActives(@NonNull Pageable pageable) {
      return repo.findAllByActiveTrue(pageable);
   }

   @Override
   public Optional<CustomerEntity> getById(@NonNull Long id) {
      return repo.findById(id);
   }

   @Override
   @Transactional
   public CustomerEntity save(@NonNull CustomerDtoInput dto) {
      boolean userExists = repo.existsByUser_IdAndActiveTrue(dto.userId());
      if(userExists){
         throw new AlreadyExistsException("User id already exists.");
      }
      UserEntity user = userRepo.findById(dto.userId())
              .orElseThrow(() -> new UserNotFoundException("User Not Found."));
      CustomerEntity customerEntity = customerMapper.toEntity(dto, user);
      return repo.save(customerEntity);
   }

   @Override
   @Transactional
   public CustomerEntity update(@NonNull Long id, @NonNull CustomerDtoInput dto) {
      throw new NoOperationException("Unsupported Operation.");
   }

   @Override
   public Optional<UserEntity> getUserAssociatedById(long id) {
      return repo.findUserAssociatedByIdUser(id);
   }

   @Override
   public CustomerEntity updateContact(@NonNull Long id, @NonNull CustomerDtoInput dto) {
      CustomerEntity customer = repo.findById(id)
              .orElseThrow(() -> new CustomerNotFoundException("Customer not found, id: " + id));
      customer.setPhoneContact(dto.contact());
      return repo.save(customer);
   }


   @Override
   @Transactional
   public void softDeleteById(@NonNull Long id) {
      CustomerEntity customer = repo.findById(id)
              .orElseThrow(() -> new CustomerNotFoundException("Customer Not found."));
      userRepo.softDelete(customer.getId());
      repo.softDelete(id);
   }
}
