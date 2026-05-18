package com.jame.dev.gymApp.service;

import com.jame.dev.gymApp.features.customer.domain.model.CustomerEntity;
import com.jame.dev.gymApp.features.user.domain.model.RoleEntity;
import com.jame.dev.gymApp.features.user.domain.model.UserEntity;
import com.jame.dev.gymApp.features.auth.domain.exception.AlreadyExistsException;
import com.jame.dev.gymApp.domain.exception.NoActiveException;
import com.jame.dev.gymApp.features.customer.application.contract.CustomerFactory;
import com.jame.dev.gymApp.features.customer.api.request.CustomerRequest;
import com.jame.dev.gymApp.features.customer.api.response.CustomerResponse;
import com.jame.dev.gymApp.application.dto.PageDto;
import com.jame.dev.gymApp.features.customer.domain.repository.CustomerRepository;
import com.jame.dev.gymApp.features.customer.application.service.CustomerApplicationService;
import com.jame.dev.gymApp.features.user.domain.model.Role;
import com.jame.dev.gymApp.features.customer.application.contract.CustomerUpdater;
import com.jame.dev.gymApp.features.customer.application.support.validator.CustomerValidator;
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
   private CustomerValidator customerValidator;
   @Mock
   private CustomerFactory customerFactory;
   @Mock
   private CustomerUpdater customerUpdater;

   @InjectMocks
   private CustomerApplicationService service;

   private final UserEntity mockUser = new UserEntity();
   private final CustomerEntity mockCustomer = new CustomerEntity();
   private final CustomerRequest mockDto = new CustomerRequest("email@mail.com", "27930527");

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
      final String search = "id=10";
      final Pageable pageable = PageRequest.of(0, 5, sort);
      final List<CustomerEntity> subList = testCustomerList.subList(0, 5);
      final PageDto<CustomerResponse> output = mock();
      when(repo.findAll(pageable))
              .thenReturn(new PageImpl<>(subList));
      when(customerFactory.createPageFrom(any())).thenReturn(output);

      final var page = service.getPage(pageable, search);
      final var pageContent = page.content();

      assertNotNull(page, "Should not be null.");
      assertNotNull(pageContent, "Page content should not be null.");

      verify(repo, atLeastOnce()).findAll(pageable);
      verify(customerFactory, atLeastOnce()).createPageFrom(any());
      verifyNoMoreInteractions(repo);
   }

   @Test
   @DisplayName("Should Get Customer By Id")
   void getById() {
      CustomerEntity customer = mock();
      CustomerResponse output = mock();

      when(repo.findById(anyLong()))
              .thenReturn(Optional.of(customer));
      when(customerFactory.createFromEntity(any()))
              .thenReturn(output);

      final var result = assertDoesNotThrow(() -> service.getById(1L));
      assertNotNull(result);

      verify(repo, times(1)).findById(anyLong());
      verify(customerFactory, times(1)).createFromEntity(any());
      verifyNoMoreInteractions(repo, customerFactory);
      verifyNoInteractions(customerUpdater, customerValidator);
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
      UserEntity user = mock();
      CustomerEntity customer = mock();

      CustomerResponse output =  mock(CustomerResponse.class);

      when(customerValidator.validateUserBeforeCreation(any(CustomerRequest.class)))
         .thenReturn(user);
      when(customerFactory.createFromInput(any())).thenReturn(customer);
      when(repo.saveAndFlush(any()))
              .thenReturn(customer);
      when(customerFactory.createFromEntity(any()))
              .thenReturn(output);

      final var result = assertDoesNotThrow(() -> service.save(mockDto));

      assertNotNull(result, "Result should not be null.");

      verify(customerValidator, atMostOnce()).validateUserBeforeCreation(any(CustomerRequest.class));
      verify(repo, atMostOnce()).findDeactivatedByUserId(1L);
      verify(customerFactory, atMostOnce()).createFromInput(any());
      verify(repo, atMostOnce()).saveAndFlush(mockCustomer);
      verify(customerFactory, atMostOnce()).createFromEntity(mockCustomer);
      verifyNoMoreInteractions(customerValidator, customerFactory, repo);
   }

   @Test
   @DisplayName("Already exists check works")
   void alreadyExistsCheck() {
      doThrow(AlreadyExistsException.class)
         .when(customerValidator)
         .validateUserBeforeCreation(any(CustomerRequest.class));

      assertThrows(AlreadyExistsException.class, () -> service.save(mockDto));

      verify(customerValidator, atMostOnce()).validateUserBeforeCreation(any(CustomerRequest.class));
      verify(repo, atMostOnce()).findDeactivatedByUserId(1L);
      verifyNoInteractions(customerFactory, customerUpdater);
   }


   @Test
   @DisplayName("Customer deactivated check works")
   void deactivatedCheck() {
      doThrow(NoActiveException.class)
         .when(customerValidator)
         .validateUserBeforeCreation(any());
      assertThrows(NoActiveException.class, () -> service.save(mockDto));

      verify(customerValidator, atMostOnce()).validateUserBeforeCreation(any());
      verify(repo, atMostOnce()).findDeactivatedByUserId(anyLong());
      verifyNoInteractions(customerFactory, customerUpdater);
   }

   @Test
   @DisplayName("Should update a customer entity")
   void updateCustomer() {
      final CustomerEntity customer = new CustomerEntity();
      customer.setUpdatedAt(Instant.now());

      CustomerResponse output = mock(CustomerResponse.class);

      when(repo.findById(anyLong())).thenReturn(Optional.of(mockCustomer));
      doNothing().when(customerUpdater).apply(any(CustomerEntity.class), any(CustomerRequest.class));
      when(repo.saveAndFlush(any(CustomerEntity.class))).thenReturn(customer);
      when(customerFactory.createFromEntity(any())).thenReturn(output);

      final var result = service.update(1L, mockDto);

      assertNotNull(result);

      verify(repo, atMostOnce()).findById(anyLong());
      verify(customerUpdater, atMostOnce()).apply(any(CustomerEntity.class), any(CustomerRequest.class));
      verify(repo, atMostOnce()).saveAndFlush(any(CustomerEntity.class));
      verify(customerFactory, atMostOnce()).createFromEntity(customer);
      verifyNoMoreInteractions(repo, customerUpdater, customerFactory);
   }

   @Test
   @DisplayName("Should do soft delete")
   void softDelete() {
      service.softDelete(1L);

      verify(repo, atMostOnce()).deleteById(anyLong());
      verifyNoMoreInteractions(repo);
   }
}
