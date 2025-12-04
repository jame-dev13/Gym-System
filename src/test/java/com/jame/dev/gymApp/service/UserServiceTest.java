package com.jame.dev.gymApp.service;

import com.jame.dev.gymApp.entity.RoleEntity;
import com.jame.dev.gymApp.entity.UserEntity;
import com.jame.dev.gymApp.mapper.RoleMapper;
import com.jame.dev.gymApp.mapper.UserMapper;
import com.jame.dev.gymApp.model.dto.in.UserDtoInput;
import com.jame.dev.gymApp.repository.CustomerRepository;
import com.jame.dev.gymApp.repository.RoleRepository;
import com.jame.dev.gymApp.repository.UserRepository;
import com.jame.dev.gymApp.service.out.UserServiceImplementation;
import com.jame.dev.gymApp.shared.enums.Role;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

   @Mock
   private UserRepository repo;

   @Mock
   private CustomerRepository customerRepo;

   @Mock
   private PasswordEncoder passwordEncoder;

   @Mock
   private RoleRepository roleRepository;

   @Mock
   private UserMapper userMapper;

   @Mock
   private RoleMapper roleMapper;

   @InjectMocks
   private UserServiceImplementation service;

   private UserEntity testUser;

   @BeforeEach
   public void setUp() {
      testUser = UserEntity.builder()
              .id(1L)
              .name("userTest")
              .email("test@mail.com")
              .password("testSecret123")
              .roles(Set.of(new RoleEntity(null, Role.ADMIN)))
              .active(true)
              .build();
   }

   @Test
   @DisplayName("Get Users actives")
   void getAllUsersByActiveField() {
      when(repo.findByActiveTrue()).thenReturn(List.of(this.testUser));
      List<UserEntity> getAll = service.getAll();

      Assertions.assertTrue(getAll.getFirst().getActive(),
              () -> "The user should be active");

      verify(repo).findByActiveTrue();
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
              .active(true)
              .build();

      when(repo.existsByEmail(userDtoInput.email())).thenReturn(false);
      when(userMapper.toEntity(any(UserDtoInput.class), anySet())).thenCallRealMethod();
      when(repo.save(any(UserEntity.class)))
              .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
      UserEntity userAdded = service.save(userDtoInput);

      ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
      verify(repo).existsByEmail(userDtoInput.email());
      verify(userMapper).toEntity(any(UserDtoInput.class), anySet());
      verify(repo).save(captor.capture());
      UserEntity userSaved = captor.getValue();

      Assertions.assertNotNull(userSaved, "The entity returned should not be null.");
      Assertions.assertEquals(userAdded, userSaved, "The returned object should be the same one that has been given to the repo.");
      Assertions.assertTrue(userAdded.getActive(), "The active field should be true after every insertion.");
   }

   @Test
   @DisplayName("Get User By Id")
   void getUserById() {
      Long id = this.testUser.getId();
      when(repo.findById(id)).thenReturn(Optional.ofNullable(this.testUser));

      Optional<UserEntity> optionalUser = service.getById(id);
      verify(repo).findById(id);

      Assertions.assertNotEquals(Optional.empty(), optionalUser, "The User Optional should not be empty.");
      Assertions.assertDoesNotThrow(optionalUser::get, "Should doesn't throw any Exception.");
   }

   @Test
   @DisplayName("Get User by Email.")
   void getUserByEmail(){
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
              .active(true)
              .build();

      String oldName = this.testUser.getName();
      String oldEmail = this.testUser.getEmail();
      String oldPassword = this.testUser.getPassword();

      when(repo.findById(id)).thenReturn(Optional.ofNullable(this.testUser));
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
   void softDelete(){
      Long id = this.testUser.getId();
      //simulate association with one Customer.
      when(customerRepo.findUserAssociatedByIdUser(id)).thenReturn(Optional.ofNullable(this.testUser));

      service.softDeleteById(id);
      verify(repo).softDelete(id);
      //When there's a User referenced by a Customer then, the pure delete does not take over.
      verify(repo, never()).deleteById(anyLong());
   }

   @Test
   @DisplayName("DeleteById: 'Delete case'")
   void delete(){
      Long id = this.testUser.getId();
      //There's no association
      when(customerRepo.findUserAssociatedByIdUser(id)).thenReturn(Optional.empty());
      service.softDeleteById(id);
      verify(repo).deleteById(id);
      verify(repo, never()).softDelete(id);
   }

   private UserEntity buildUser(Long id, String name, String email) {
      return UserEntity.builder()
              .id(id)
              .name(name)
              .email(email)
              .password("12345")
              .roles(Set.of(new RoleEntity(null, Role.USER)))
              .active(true)
              .build();
   }
}
