package com.jame.dev.gymApp.service;

import com.jame.dev.gymApp.entity.RoleEntity;
import com.jame.dev.gymApp.entity.UserEntity;
import com.jame.dev.gymApp.mapper.RoleMapper;
import com.jame.dev.gymApp.mapper.UserMapper;
import com.jame.dev.gymApp.model.dto.in.UserDtoInput;
import com.jame.dev.gymApp.repository.CustomerRepository;
import com.jame.dev.gymApp.repository.UserRepository;
import com.jame.dev.gymApp.service.in.RoleService;
import com.jame.dev.gymApp.service.out.UserServiceImplementation;
import com.jame.dev.gymApp.shared.enums.Role;
import lombok.NonNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

   @Mock
   private UserRepository repo;

   @Mock
   private RoleService roleService;

   @Mock
   private CustomerRepository customerRepo;

   @Mock
   private PasswordEncoder passwordEncoder;

   @Mock
   private UserMapper userMapper;

   @Mock
   private RoleMapper roleMapper;

   @InjectMocks
   private UserServiceImplementation service;

   @Captor
   private ArgumentCaptor<UserEntity> entityCaptor;

   private final Sort sort = Sort.sort(UserEntity.class).by(UserEntity::getEmail).descending();

   private final UserEntity testUser = UserEntity.builder()
           .id(1L)
           .name("userTest")
           .email("test@mail.com")
           .password("testSecret123")
           .roles(Set.of(new RoleEntity(null, Role.ADMIN)))
           .active(true)
           .build();

   private final List<UserEntity> testUserList = IntStream.range(0, 10)
           .mapToObj(i -> UserEntity.builder()
                   .id((long) (i + 1))
                   .name("userTest" + (i + 1))
                   .email("test" + (i + 1) + "@mail.com")
                   .password("testSecret123")
                   .roles(Set.of(new RoleEntity(null, Role.ADMIN)))
                   .active(true)
                   .build())
           .sorted(Comparator.comparing(UserEntity::getEmail).reversed())
           .toList();

   @Test
   @DisplayName("Get all Users")
   void getAllUsers(){
      when(repo.findAll()).thenReturn(testUserList);
      List<UserEntity> users = service.getAll();
      UserEntity first = users.getFirst();
      UserEntity last = users.getLast();
      assertNotNull(users, "List should not be null");
      assertTrue(users.contains(first), "Should contain the first object.");
      assertSame(testUserList.getFirst(), first, "Should be the same first object.");
      assertTrue(users.contains(last), "Should contain the last object.");
      assertSame(testUserList.getLast(), last, "Should be the same last object.");

      verify(repo).findAll();
   }

   @Test
   @DisplayName("Get Users actives")
   void getAllUsersByActiveField() {
      when(repo.findAllByActiveTrue()).thenReturn(testUserList);
      List<UserEntity> users = service.getActives();
      UserEntity first = users.getFirst();

      assertNotNull(users, "List should not be null.");
      assertTrue(users.contains(testUser), "Should contain the test object.");
      assertSame(first, testUserList.getFirst(), "Should be the same first object.");
      assertTrue(users.stream().allMatch(UserEntity::isActive), "The users should be actives.");

      verify(repo).findAllByActiveTrue();
   }

   @Test
   @DisplayName("Get Users by page")
   void getUsersByPages(){
      Pageable pageable = PageRequest.of(0, 5, sort);
      List<UserEntity> subList = testUserList.subList(0, 5);
      when(repo.findAllByActiveTrue(pageable))
              .thenReturn(new PageImpl<>(subList));
      Page<@NonNull UserEntity> page = service.getPageOfActives(pageable);
      List<UserEntity> pageList = page.getContent();

      assertNotNull(page, "Page should not be null.");
      assertFalse(page.isEmpty(), "Page should not be empty.");
      assertEquals(subList, pageList, "Should be the same list.");
      assertSame(subList.getFirst(), pageList.getFirst(), "Should contain the same first object.");
      assertSame(subList.getLast(), pageList.getLast(), "Should contain the same first object.");
   }

   @Test
   @DisplayName("Next page")
   void nextPage(){
      Pageable pageable = PageRequest.of(1, 5, sort);
      List<UserEntity> subList = testUserList.subList(5, testUserList.size() - 1);
      when(repo.findAllByActiveTrue(pageable))
              .thenReturn(new PageImpl<>(subList));
      Page<@NonNull UserEntity> page = service.getPageOfActives(pageable);
      List<UserEntity> pageList = page.getContent();
      System.out.println(pageList);

      assertNotNull(page, "Page should not be null.");
      assertFalse(page.isEmpty(), "Page should not be empty.");
      assertEquals(subList, pageList, "Should be the same list.");
      assertSame(subList.getFirst(), pageList.getFirst(), "Should contain the same first object.");
      assertSame(subList.getLast(), pageList.getLast(), "Should contain the same first object.");
   }

   @Test
   @DisplayName("Add User")
   void addUser() {
      UserDtoInput userDtoInput = UserDtoInput
              .builder()
              .name("userAdded")
              .email("userAdded@mail.com")
              .password("1234456")
              .roles(Set.of(Role.USER, Role.ADMIN))
              .build();

      when(repo.existsByEmail(userDtoInput.email())).thenReturn(false);
      when(userMapper.toEntity(any(UserDtoInput.class), anySet())).thenCallRealMethod();
      when(repo.save(any(UserEntity.class)))
              .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

      UserEntity userAdded = service.save(userDtoInput);

      verify(repo).existsByEmail(userDtoInput.email());
      verify(userMapper).toEntity(any(UserDtoInput.class), anySet());
      verify(repo).save(entityCaptor.capture());

      UserEntity userSaved = entityCaptor.getValue();

      assertNotNull(userSaved, "The entity returned should not be null.");
      assertEquals(userAdded, userSaved, "The returned object should be the same one that has been given to the repo.");
      assertTrue(userAdded.isActive(), "The active field should be true after every insertion.");
   }

   @Test
   @DisplayName("Get User By Id")
   void getUserById() {
      Long id = this.testUser.getId();
      when(repo.findById(id)).thenReturn(Optional.of(this.testUser));

      Optional<UserEntity> optionalUser = service.getById(id);
      verify(repo).findById(id);

      Assertions.assertNotEquals(Optional.empty(), optionalUser, "The User Optional should not be empty.");
      Assertions.assertDoesNotThrow(optionalUser::get, "Should doesn't throw any Exception.");
   }

   @Test
   @DisplayName("Get User by Email.")
   void getUserByEmail() {
      final String EMAIL = this.testUser.getEmail();
      when(repo.findByEmail(EMAIL)).thenReturn(Optional.of(testUser));
      Optional<UserEntity> optionalUser = service.getUserByEmail(EMAIL);
      verify(repo).findByEmail(EMAIL);

      Assertions.assertNotEquals(Optional.empty(), optionalUser, "The User Optional should not be empty.");
      Assertions.assertDoesNotThrow(optionalUser::get, "Should doesn't throw any Exception.");
   }

   @Test
   @DisplayName("Update User")
   void updateUser() {
      Long id = this.testUser.getId();
      UserDtoInput dto = UserDtoInput.builder()
              .name("nameChanged")
              .email("emailChanged@mail.com")
              .password("passwordChangedToo")
              .roles(Set.of(Role.USER))
              .build();

      String oldName = this.testUser.getName();
      String oldEmail = this.testUser.getEmail();
      String oldPassword = this.testUser.getPassword();

      when(repo.findById(id)).thenReturn(Optional.of(this.testUser));
      when(repo.save(any(UserEntity.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
      UserEntity userUpdated = service.update(id, dto);
      //Not match with the values that it had before
      Assertions.assertNotEquals(userUpdated.getName(), oldName, "Name should not be equal anymore.");
      Assertions.assertNotEquals(userUpdated.getEmail(), oldEmail, "Email should not be the equal anymore.");
      Assertions.assertNotEquals(userUpdated.getPassword(), oldPassword, "Password should not be the equal anymore.");

      verify(repo).findById(id);
      verify(repo).save(any(UserEntity.class));
   }

   @Test
   @DisplayName("DeleteById: 'SoftDelete case'")
   void softDelete() {
      Long id = this.testUser.getId();
      //simulate association with one Customer.
      when(customerRepo.findUserAssociatedByIdUser(id)).thenReturn(Optional.of(this.testUser));

      service.softDeleteById(id);
      verify(repo).softDelete(id);
      //When there's a User referenced by a Customer then, the pure delete does not take over.
      verify(repo, never()).deleteById(anyLong());
   }

   @Test
   @DisplayName("DeleteById: 'Delete case'")
   void delete() {
      Long id = this.testUser.getId();
      //There's no association
      when(customerRepo.findUserAssociatedByIdUser(id)).thenReturn(Optional.empty());
      service.softDeleteById(id);
      verify(repo).deleteById(id);
      verify(repo, never()).softDelete(id);
   }
}
