package com.jame.dev.gymApp.service;

import com.jame.dev.gymApp.entity.CustomerEntity;
import com.jame.dev.gymApp.entity.RoleEntity;
import com.jame.dev.gymApp.entity.UserEntity;
import com.jame.dev.gymApp.exception.AlreadyExistsException;
import com.jame.dev.gymApp.exception.NoActiveException;
import com.jame.dev.gymApp.mapper.CustomerMapper;
import com.jame.dev.gymApp.model.dto.in.CustomerDtoInput;
import com.jame.dev.gymApp.repository.CustomerRepository;
import com.jame.dev.gymApp.repository.UserRepository;
import com.jame.dev.gymApp.service.out.CustomerServiceImplementation;
import com.jame.dev.gymApp.shared.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

   @Mock
   private CustomerRepository repo;

   @Mock
   private UserRepository userRepo;

   @Mock
   private CustomerMapper customerMapper;

   @InjectMocks
   private CustomerServiceImplementation service;

   private final UserEntity mockUser = new UserEntity();
   private final CustomerEntity mockCustomer = new CustomerEntity();
   private final CustomerDtoInput mockDto = new CustomerDtoInput("email@mail.com", "27930527");

   private final Sort sort = Sort.sort(CustomerEntity.class).by(CustomerEntity::getId).descending();

   private final List<CustomerEntity> testCustomerList = IntStream.range(0, 10)
           .mapToObj(i -> {
              UserEntity user = UserEntity.builder()
                      .name("userTest" + i)
                      .email("test" + i + "@mail.com")
                      .password("testSecret123" + i)
                      .roles(Set.of(new RoleEntity(null, Role.ADMIN)))
                      .build();
              return new CustomerEntity(user, "312434" + i);
           })
           .toList();

   @BeforeEach
   void setUp() {
      mockCustomer.setUser(mockUser);
   }

   @Test
   @DisplayName("Should get a page of CustomerEntity.")
   void getByPageable() {
      final Pageable pageable = PageRequest.of(0, 5, sort);
      final List<CustomerEntity> subList = testCustomerList.subList(0, 5);

      when(repo.findAllByActiveTrue(pageable)).thenReturn(new PageImpl<>(subList));

      final var page = service.getPage(pageable);
      final var pageContent = page.getContent();

      assertNotNull(page, "Should not be null.");
      assertNotNull(pageContent, "Page content should not be null.");

      verify(repo, atLeastOnce()).findAllByActiveTrue(pageable);
      verifyNoMoreInteractions(repo);
   }

   @Test
   @DisplayName("Should Get Customer By Id")
   void getById() {
      when(repo.findById(anyLong())).thenReturn(Optional.of(mockCustomer));

      final Optional<CustomerEntity> result = service.getById(1L);

      verify(repo, times(1)).findById(anyLong());
      verifyNoMoreInteractions(repo);

      assertTrue(result.isPresent(), "The result should not be empty");
      assertDoesNotThrow(result::get, "Shouldn't throw any Exception.");
      assertNotNull(result.get(), "The result should not be null.");
   }

   @Test
   @DisplayName("Exists by id and email")
   void existsByIdAndEmail() {
      when(repo.existsByIdAndUser_EmailAndActiveTrue(anyLong(), anyString())).thenReturn(true);
      boolean exists = service.exitsByIdAndCustomerEmail(1L, mockDto.email());
      assertTrue(exists);
      verify(repo).existsByIdAndUser_EmailAndActiveTrue(anyLong(), anyString());
   }

   @Test
   @DisplayName("Should save a Customer")
   void saveCustomer() {
      when(userRepo.findByEmail(anyString())).thenReturn(Optional.of(mockUser));
      when(repo.findByUser(mockUser)).thenReturn(Optional.empty());
      when(customerMapper.toEntity(mockDto, mockUser)).thenReturn(mockCustomer);
      when(repo.saveAndFlush(mockCustomer))
              .thenReturn(mockCustomer);

      final CustomerEntity customerAdded = service.save(mockDto);

      assertNotNull(customerAdded, "Result should not be null.");

      verify(userRepo, atMostOnce()).findByEmail(anyString());
      verify(repo, atMostOnce()).findByUser(mockUser);
      verify(customerMapper, atMostOnce()).toEntity(mockDto, mockUser);
      verify(repo, atMostOnce()).saveAndFlush(mockCustomer);
      verifyNoMoreInteractions(userRepo, customerMapper, repo);
   }

   @Test
   @DisplayName("Already exists check works")
   void alreadyExistsCheck() {
      when(userRepo.findByEmail(anyString())).thenReturn(Optional.of(mockUser));
      when(repo.findByUser(any(UserEntity.class))).thenReturn(Optional.of(mockCustomer));

      assertThrows(AlreadyExistsException.class, () -> service.save(mockDto));

      verify(userRepo, atMostOnce()).findByEmail(anyString());
      verify(repo, atMostOnce()).findByUser(any(UserEntity.class));
   }

   @Test
   @DisplayName("Check for User deactivated, should works")
   void checkUserActiveStatus() {
      UserEntity user = new UserEntity();
      user.setActive(false);

      when(userRepo.findByEmail(anyString())).thenReturn(Optional.of(user));

      assertThrows(NoActiveException.class, () -> service.save(mockDto));

      verify(userRepo, atMostOnce()).findByEmail(anyString());
   }

   @Test
   @DisplayName("Customer deactivated check works")
   void deactivatedCheck() {
      CustomerEntity customer = new CustomerEntity();
      customer.setActive(false);
      when(userRepo.findByEmail(anyString())).thenReturn(Optional.of(mockUser));
      when(repo.findByUser(any(UserEntity.class))).thenReturn(Optional.of(customer));

      assertThrows(NoActiveException.class, () -> service.save(mockDto));

      verify(userRepo, atMostOnce()).findByEmail(anyString());
      verify(repo, atMostOnce()).findByUser(any(UserEntity.class));
   }

   @Test
   @DisplayName("Should update a customer entity")
   void updateCustomer() {
      final CustomerEntity customer = new CustomerEntity();
      customer.setUpdatedAt(Instant.now());

      when(repo.findById(anyLong())).thenReturn(Optional.of(mockCustomer));
      when(repo.saveAndFlush(any(CustomerEntity.class))).thenReturn(customer);

      final CustomerEntity result = service.update(1L, mockDto);

      assertNotNull(result);

      verify(repo, atMostOnce()).findById(anyLong());
      verify(repo, atMostOnce()).saveAndFlush(any(CustomerEntity.class));
      verifyNoMoreInteractions(repo);
   }

   @Test
   @DisplayName("Should do soft delete")
   void softDelete() {
      service.softDelete(1L);

      verify(repo, atMostOnce()).deleteById(anyLong());
      verifyNoMoreInteractions(repo);
   }
}
