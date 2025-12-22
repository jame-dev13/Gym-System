package com.jame.dev.gymApp.service;

import com.jame.dev.gymApp.entity.CustomerEntity;
import com.jame.dev.gymApp.entity.RoleEntity;
import com.jame.dev.gymApp.entity.UserEntity;
import com.jame.dev.gymApp.mapper.RoleMapper;
import com.jame.dev.gymApp.mapper.UserMapper;
import com.jame.dev.gymApp.model.dto.in.UserDtoInput;
import com.jame.dev.gymApp.repository.CustomerRepository;
import com.jame.dev.gymApp.repository.UserRepository;
import com.jame.dev.gymApp.service.in.RoleService;
import com.jame.dev.gymApp.service.out.UserServiceImplementation;
import com.jame.dev.gymApp.shared.enums.AuthProvider;
import com.jame.dev.gymApp.shared.enums.Role;
import lombok.NonNull;
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
   @DisplayName("Should Gets page of UserEntity.")
   void getUsersByPages(){
      final Pageable pageable = PageRequest.of(0, 5, sort);
      final List<UserEntity> subList = testUserList.subList(0, 5);
      when(repo.findAllByActiveTrue(pageable))
              .thenReturn(new PageImpl<>(subList));
      final Page<@NonNull UserEntity> page = service.getPage(pageable);
      final List<UserEntity> pageList = page.getContent();

      assertNotNull(page, "Page should not be null.");
      assertFalse(page.isEmpty(), "Page should not be empty.");
      assertEquals(subList, pageList, "Should be the same list.");
      assertSame(subList.getFirst(), pageList.getFirst(), "Should contain the same first object.");
      assertSame(subList.getLast(), pageList.getLast(), "Should contain the same first object.");
   }

   @Test
   @DisplayName("Should Add User")
   void addUser() {
      final UserDtoInput userDtoInput = UserDtoInput
              .builder()
              .name("userAdded")
              .email("userAdded@mail.com")
              .password("1234456")
              .roles(Set.of(Role.USER, Role.ADMIN))
              .authProvider(AuthProvider.LOCAL)
              .build();

      when(repo.existsByEmail(userDtoInput.email())).thenReturn(false);
      when(userMapper.toEntity(any(UserDtoInput.class), anySet())).thenCallRealMethod();
      when(repo.save(any(UserEntity.class)))
              .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

      final UserEntity userAdded = service.save(userDtoInput);

      verify(repo, atLeastOnce()).existsByEmail(userDtoInput.email());
      verify(userMapper, atLeastOnce()).toEntity(any(UserDtoInput.class), anySet());
      verify(repo, atLeastOnce()).save(entityCaptor.capture());
      verifyNoMoreInteractions(repo);

      final UserEntity userSaved = entityCaptor.getValue();

      assertNotNull(userSaved, "The entity returned should not be null.");
      assertEquals(userAdded, userSaved, "The returned object should be the same one that has been given to the repo.");
      assertTrue(userAdded.isActive(), "The active field should be true after every insertion.");
   }

   @Test
   @DisplayName("Should Get User By Id")
   void getUserById() {
      final long id = this.testUser.getId();
      when(repo.findById(id)).thenReturn(Optional.of(this.testUser));

      final Optional<UserEntity> optionalUser = service.getById(id);
      verify(repo, atLeastOnce()).findById(id);
      verifyNoMoreInteractions(repo);

      assertNotEquals(Optional.empty(), optionalUser, "The User Optional should not be empty.");
      assertDoesNotThrow(optionalUser::get, "Should doesn't throw any Exception.");
   }

   @Test
   @DisplayName("Should Get User by Email.")
   void getUserByEmail() {
      final String EMAIL = this.testUser.getEmail();
      when(repo.findByEmail(EMAIL)).thenReturn(Optional.of(testUser));

      final Optional<UserEntity> optionalUser = service.getUserByEmail(EMAIL);
      verify(repo, atLeastOnce()).findByEmail(EMAIL);
      verifyNoMoreInteractions(repo);
      assertNotEquals(Optional.empty(), optionalUser, "The User Optional should not be empty.");
      assertDoesNotThrow(optionalUser::get, "Should doesn't throw any Exception.");
   }

   @Test
   @DisplayName("Should Update User")
   void updateUser() {
      final long id = this.testUser.getId();
      final UserDtoInput dto = UserDtoInput.builder()
              .name("nameChanged")
              .email("emailChanged@mail.com")
              .password("passwordChangedToo")
              .roles(Set.of(Role.USER))
              .authProvider(AuthProvider.LOCAL)
              .build();

      final String oldName = this.testUser.getName();
      final String oldEmail = this.testUser.getEmail();
      final String oldPassword = this.testUser.getPassword();

      when(repo.findById(id)).thenReturn(Optional.of(this.testUser));
      when(repo.save(any(UserEntity.class)))
              .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

      final UserEntity userUpdated = service.update(id, dto);

      assertNotEquals(oldName, userUpdated.getName(), "Name should not be equal anymore.");
      assertNotEquals(oldEmail, userUpdated.getEmail(), "Email should not be the equal anymore.");
      assertNotEquals(oldPassword, userUpdated.getPassword(), "Password should not be the equal anymore.");

      verify(repo, atLeastOnce()).findById(id);
      verify(repo, atLeastOnce()).save(any(UserEntity.class));
      verifyNoMoreInteractions(repo);
   }

   @Test
   @DisplayName("[SOFT_DELETE]: Should DeleteById.")
   void softDelete() {
      final long id = this.testUser.getId();

      when(customerRepo.findById(id)).thenReturn(Optional.of(new CustomerEntity(1L, new UserEntity(), "2147252", true)));

      service.softDelete(id);

      verify(repo, times(1)).softDelete(id);
      verify(repo, never()).deleteById(anyLong());
      verifyNoMoreInteractions(repo);
   }

//   @Test
//   @DisplayName("[REAL_DELETE]: Should DeleteById.")
//   void delete() {
//      final long id = this.testUser.getId();
//      when(customerRepo.findUserAssociatedByIdUser(id)).thenReturn(Optional.empty());
//
//      service.softDelete(id);
//
//      verify(repo, times(1)).deleteById(id);
//      verify(repo, never()).softDelete(id);
//      verifyNoMoreInteractions(repo);
//   }
}
