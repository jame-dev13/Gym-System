package com.jame.dev.gymApp.service;

import com.jame.dev.gymApp.entity.CustomerEntity;
import com.jame.dev.gymApp.entity.RoleEntity;
import com.jame.dev.gymApp.entity.UserEntity;
import com.jame.dev.gymApp.exception.UserNotFoundException;
import com.jame.dev.gymApp.mapper.CustomerMapper;
import com.jame.dev.gymApp.model.dto.in.CustomerDtoInput;
import com.jame.dev.gymApp.repository.CustomerRepository;
import com.jame.dev.gymApp.repository.UserRepository;
import com.jame.dev.gymApp.service.out.CustomerServiceImplementation;
import com.jame.dev.gymApp.shared.enums.Role;
import lombok.NonNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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

   private final UserEntity testUser = UserEntity.builder()
           .id(1L)
           .name("userTest")
           .email("test@mail.com")
           .password("testSecret123")
           .roles(Set.of(new RoleEntity(null, Role.ADMIN)))
           .active(true)
           .build();
   private final CustomerEntity customerTest = CustomerEntity.builder()
           .id(1L)
           .user(this.testUser)
           .phoneContact("123456789")
           .active(true)
           .build();

   private final Sort sort = Sort.sort(CustomerEntity.class).by(CustomerEntity::getId).descending();
   private final List<CustomerEntity> testCustomerList = IntStream.range(0, 10)
           .mapToObj(i -> {
              UserEntity user = UserEntity.builder()
                      .id((long) (i + 1))
                      .name("userTest" + i)
                      .email("test" + i + "@mail.com")
                      .password("testSecret123" + i)
                      .roles(Set.of(new RoleEntity(null, Role.ADMIN)))
                      .active(true)
                      .build();
              return new CustomerEntity((long) (i + 1), user, "312434" + i, true);
           })
           .sorted(Comparator.comparing(CustomerEntity::getId).reversed())
           .toList();

   private final Predicate<CustomerEntity> allMatch = c -> c.getUser() != null && c.isActive();

   @Test
   @DisplayName("Should get a page of CustomerEntity.")
   void getByPageable() {
      final Pageable pageable = PageRequest.of(0, 5, sort);
      final List<CustomerEntity> subList = testCustomerList.subList(0, 5);

      when(repo.findAllByActiveTrue(pageable)).thenReturn(new PageImpl<>(subList));

      final Page<@NonNull CustomerEntity> page = service.getPage(pageable);
      final List<CustomerEntity> pageContent = page.getContent();
      System.out.println(pageContent);

      assertNotNull(page, "Should not be null");
      assertFalse(page.isEmpty(), "Should not be empty");
      assertTrue(page.stream().allMatch(allMatch), "All customer should have a user associated and should be actives");
      assertSame(pageContent.getFirst(), subList.getFirst(), "First list object should be the same.");
      assertSame(pageContent.getLast(), subList.getLast(), "Last list object should be the same.");

      verify(repo, atLeastOnce()).findAllByActiveTrue(pageable);
      verifyNoMoreInteractions(repo);
   }

   @Test
   @DisplayName("Should gets next page.")
   void getNext() {
      final Pageable pageable = PageRequest.of(1, 5, sort);
      final List<CustomerEntity> subList = testCustomerList.subList(5, 9);

      when(repo.findAllByActiveTrue(pageable)).thenReturn(new PageImpl<>(subList));

      final Page<@NonNull CustomerEntity> page = service.getPage(pageable);
      final List<CustomerEntity> pageContent = page.getContent();
      System.out.println(pageContent);

      assertNotNull(page, "Should not be null");
      assertFalse(page.isEmpty(), "Should not be empty");
      assertTrue(page.stream().allMatch(allMatch), "All customer should have a user associated and should be actives");
      assertSame(pageContent.getFirst(), subList.getFirst(), "First list object should be the same.");
      assertSame(pageContent.getLast(), subList.getLast(), "Last list object should be the same.");

      verify(repo, atLeastOnce()).findAllByActiveTrue(pageable);
      verifyNoMoreInteractions(repo);
   }

   @Test
   @DisplayName("Should Gets Customers By Id")
   void getById() {
      final long id = this.customerTest.getId();
      when(repo.findById(id)).thenReturn(Optional.of(this.customerTest));

      final Optional<CustomerEntity> optionalCustomer = service.getById(id);
      verify(repo, times(1)).findById(id);
      verifyNoMoreInteractions(repo);

      assertNotEquals(Optional.empty(), optionalCustomer, "The 'optionalCustomer' should not be empty");
      assertDoesNotThrow(optionalCustomer::get, "Should doesn't throws any Exception.");
   }

   @Test
   @DisplayName("Should save a Customer")
   void saveCustomer() {
      final Long idUser = this.testUser.getId();
      final CustomerDtoInput dto = new CustomerDtoInput(idUser, "4270143");
      when(repo.existsByUser_IdAndActiveTrue(dto.userId())).thenReturn(false);
      when(userRepo.findById(idUser)).thenReturn(Optional.of(this.testUser));
      when(customerMapper.toEntity(dto, testUser)).thenReturn(customerTest);
      when(repo.save(any(CustomerEntity.class)))
              .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

      final CustomerEntity customerAdded = service.save(dto);
      final ArgumentCaptor<CustomerEntity> captor = ArgumentCaptor.forClass(CustomerEntity.class);

      verify(repo, times(1)).existsByUser_IdAndActiveTrue(dto.userId());
      verify(userRepo, times(1)).findById(idUser);
      verify(customerMapper, times(1)).toEntity(dto, testUser);
      verify(repo, times(1)).save(captor.capture());

      final CustomerEntity customerSaved = captor.getValue();

      assertDoesNotThrow(() -> new UserNotFoundException("Not found"), "Should not throws any Exception.");
      assertNotNull(customerSaved, "The saved Object should not be null.");
      assertNotNull(customerSaved.getUser(), "The Customer should always have a user associated.");
      assertEquals(customerAdded, customerSaved, "The returned Object should be the same as the gave one to the repo.");
   }

   @Test
   @DisplayName("Should update a customer entity")
   void updateCustomer() {
      final CustomerDtoInput dto = new CustomerDtoInput(1L, "484943");
      final String oldContact = this.customerTest.getPhoneContact();
      final CustomerEntity change = this.customerTest;
      change.setPhoneContact(dto.contact());

      when(repo.findById(eq(1L))).thenReturn(Optional.of(this.customerTest));
      when(repo.save(any(CustomerEntity.class))).thenReturn(change);

      final CustomerEntity changed = service.update(1L, dto);

      verify(repo, times(1)).findById(eq(1L));
      verify(repo, times(1)).save(change);
      verifyNoMoreInteractions(repo);

      assertNotNull(changed, "Entity result should not be null.");
      assertEquals(change.getPhoneContact(), changed.getPhoneContact(), "Should have the same phone contact.");
      assertNotEquals(oldContact, changed.getPhoneContact(), "PhoneContact should not be equals.");
   }

   @Test
   @DisplayName("Should do soft delete")
   void softDelete() {
      Long id = this.customerTest.getId();
      when(repo.findById(id))
              .thenReturn(Optional.of(this.customerTest));
      Optional<CustomerEntity> optionalCustomer = service.getById(id);

      CustomerEntity customerGotten = optionalCustomer.orElseThrow();

      assertNotEquals(Optional.empty(), optionalCustomer,"The optional value should not be empty.");
      assertNotNull(customerGotten, "Should not be null");
      assertEquals(customerGotten, this.customerTest, "Should be the same object.");

      service.softDelete(id);

      verify(repo, times(1)).softDelete(id);
      verifyNoMoreInteractions(repo);
   }
}
