package com.jame.dev.gymApp.service;

import com.jame.dev.gymApp.entity.CustomerEntity;
import com.jame.dev.gymApp.entity.RoleEntity;
import com.jame.dev.gymApp.entity.UserEntity;
import com.jame.dev.gymApp.exception.NoOperationException;
import com.jame.dev.gymApp.exception.UserNotFoundException;
import com.jame.dev.gymApp.mapper.CustomerMapper;
import com.jame.dev.gymApp.model.dto.in.CustomerDtoInput;
import com.jame.dev.gymApp.repository.CustomerRepository;
import com.jame.dev.gymApp.repository.UserRepository;
import com.jame.dev.gymApp.service.out.CustomerServiceImplementation;
import com.jame.dev.gymApp.shared.enums.Role;
import lombok.NonNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

   private final Predicate<CustomerEntity> allMatch = c -> c.getUser() != null && c.getActive();

   @Test
   @DisplayName("Get all customer")
   void getAll() {
      when(repo.findAll()).thenReturn(testCustomerList);
      List<CustomerEntity> customerEntityList = service.getAll();
      assertAll("Test to validate non-nullity or emptiness, matching and equality-same objects.",
              () -> assertNotNull(customerEntityList, "List should not be null."),
              () -> assertFalse(customerEntityList.isEmpty(), "List should not be empty"),
              () -> assertTrue(customerEntityList.stream().map(CustomerEntity::getUser).allMatch(Objects::nonNull)),
              () -> assertSame(customerEntityList.getFirst(), testCustomerList.getFirst(), "First list object should be the same."),
              () -> assertSame(customerEntityList.getLast(), testCustomerList.getLast(), "Last list object should be the same.")
      );
      verify(repo).findAll();
   }

   @Test
   @DisplayName("Get All actives")
   void getAllActives() {
      when(repo.findAllByActiveTrue()).thenReturn(testCustomerList);
      List<CustomerEntity> customers = service.getActives();
      assertAll("Test to validate non-nullity or emptiness, matching and equality-same objects.",
              () -> assertNotNull(customers, "Should not be null"),
              () -> assertFalse(customers.isEmpty(), "Should not be empty"),
              () -> assertTrue(customers.stream().allMatch(allMatch), "All customer should have a user associated and should be actives"),
              () -> assertSame(customers.getFirst(), testCustomerList.getFirst(), "First list object should be the same."),
              () -> assertSame(customers.getLast(), testCustomerList.getLast(), "Last list object should be the same.")
      );
      verify(repo).findAllByActiveTrue();
   }

   @Test
   @DisplayName("Get all by pageable")
   void getByPageable(){
      Pageable pageable = PageRequest.of(0, 5, sort);
      List<CustomerEntity> subList = testCustomerList.subList(0, 5);
      when(repo.findAllByActiveTrue(pageable)).thenReturn(new PageImpl<>(subList));

      Page<@NonNull CustomerEntity> page = service.getPageOfActives(pageable);
      List<CustomerEntity> pageContent = page.getContent();
      assertAll("Test to validate non-nullity or emptiness, matching and equality-same objects.",
              () -> assertNotNull(page, "Should not be null"),
              () -> assertFalse(page.isEmpty(), "Should not be empty"),
              () -> assertTrue(page.stream().allMatch(allMatch), "All customer should have a user associated and should be actives"),
              () -> assertSame(pageContent.getFirst(), subList.getFirst(), "First list object should be the same."),
              () -> assertSame(pageContent.getLast(), subList.getLast(), "Last list object should be the same.")
      );
      verify(repo).findAllByActiveTrue(pageable);
   }

   @Test
   @DisplayName("Get Customer By Id")
   void getById() {
      Long id = this.customerTest.getId();
      when(repo.findById(id)).thenReturn(Optional.ofNullable(this.customerTest));

      Optional<CustomerEntity> optionalCustomer = service.getById(id);
      verify(repo).findById(id);

      Assertions.assertNotEquals(Optional.empty(), optionalCustomer, "The 'optionalCustomer' should not be empty");
      Assertions.assertDoesNotThrow(optionalCustomer::get, "Should doesn't throws any Exception.");
   }

   @Test
   @DisplayName("Save Customer")
   void saveCustomer() {
      Long idUser = this.testUser.getId();
      CustomerDtoInput dto = new CustomerDtoInput(idUser, "4270143");
      when(repo.existsByUser_IdAndActiveTrue(dto.userId())).thenReturn(false);
      when(userRepo.findById(idUser))
              .thenReturn(Optional.ofNullable(this.testUser));
      when(customerMapper.toEntity(dto, testUser))
              .thenReturn(customerTest);
      when(repo.save(any(CustomerEntity.class)))
              .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

      CustomerEntity customerAdded = service.save(dto);
      ArgumentCaptor<CustomerEntity> captor = ArgumentCaptor.forClass(CustomerEntity.class);
      verify(repo).existsByUser_IdAndActiveTrue(dto.userId());
      verify(userRepo).findById(idUser);
      verify(customerMapper).toEntity(dto, testUser);
      verify(repo).save(captor.capture());

      CustomerEntity customerSaved = captor.getValue();

      Assertions.assertDoesNotThrow(() -> new UserNotFoundException(""), "Should not throws any Exception.");
      assertNotNull(customerSaved, "The saved Object should not be null.");
      assertNotNull(customerSaved.getUser(), "The Customer should always have a user associated.");
      Assertions.assertEquals(customerAdded, customerSaved, "The returned Object should be the same as the gave one to the repo.");
   }

   @Test
   @DisplayName("Update customer")
   void updateCustomer() {
      Assertions.assertThrows(NoOperationException.class,
              () -> service.update(1L, new CustomerDtoInput(1L, "13794234")),
              "Should throws an 'NoOperationException' cause update method is not supported in CustomerService.");
   }

   @Test
   @DisplayName("Update contact.")
   void updateCustomerContact() {
      final Long id = this.customerTest.getId();
      final CustomerDtoInput dto = new CustomerDtoInput(this.testUser.getId(), "1236482");
      final String oldPhoneContact = this.customerTest.getPhoneContact();
      when(repo.findById(id))
              .thenReturn(Optional.of(customerTest));
      when(repo.save(this.customerTest)).thenReturn(this.customerTest);

      CustomerEntity customerUpdated = Assertions.assertDoesNotThrow(() -> service.updateContact(id, dto), "Should not throws exceptions.");

      assertNotNull(customerUpdated, "Should not be null.");
      Assertions.assertNotEquals(oldPhoneContact, customerUpdated.getPhoneContact(), "Should not be the same value.");

      verify(repo).findById(id);
      verify(repo).save(this.customerTest);
   }

   @Test
   @DisplayName("Get User associated")
   void getUserAssociated() {
      Long idUser = this.testUser.getId();
      when(repo.findUserAssociatedByIdUser(idUser))
              .thenReturn(Optional.of(this.testUser));

      Optional<UserEntity> optionalUser = service
              .getUserAssociatedById(idUser);
      UserEntity userGotten = optionalUser.orElseThrow();

      Assertions.assertNotEquals(optionalUser, Optional.empty(), "The optionalUser should not be empty in this case.");
      assertNotNull(userGotten, "The Object should not be null.");
      Assertions.assertEquals(userGotten, this.testUser, "The UserEntity objects should be the same.");
   }

   @Test
   @DisplayName("Soft delete")
   void softDelete() {
      Long id = this.customerTest.getId();
      when(repo.findById(id))
              .thenReturn(Optional.of(this.customerTest));
      var optionalCustomer = service.getById(id);
      var customerGotten = optionalCustomer.orElseThrow();

      Assertions.assertNotEquals(optionalCustomer, Optional.empty(), "The optional value should not be empty.");
      assertNotNull(customerGotten, "Should not be null");
      Assertions.assertEquals(customerGotten, this.customerTest, "Should be the same object.");

      service.softDeleteById(id);

      verify(repo).softDelete(id);
   }
}
