package com.jame.dev.gymApp.service;

import com.jame.dev.gymApp.features.user.domain.model.RoleEntity;
import com.jame.dev.gymApp.features.user.domain.model.UserEntity;
import com.jame.dev.gymApp.features.auth.domain.exception.AlreadyExistsException;
import com.jame.dev.gymApp.domain.exception.NoActiveException;
import com.jame.dev.gymApp.features.user.application.contract.UserFactory;
import com.jame.dev.gymApp.features.user.api.request.UserRequest;
import com.jame.dev.gymApp.application.dto.PageDto;
import com.jame.dev.gymApp.features.user.api.response.UserResponse;
import com.jame.dev.gymApp.features.user.domain.repository.UserRepository;
import com.jame.dev.gymApp.features.user.application.service.UserServiceImplementation;
import com.jame.dev.gymApp.features.auth.application.model.AuthProvider;
import com.jame.dev.gymApp.features.user.domain.model.Role;
import com.jame.dev.gymApp.features.user.application.contract.UserUpdater;
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
   private UserFactory userFactory;
   @Mock
   private UserUpdater userUpdater;

   @InjectMocks
   private UserServiceImplementation service;

   private final Sort sort = Sort.sort(UserEntity.class).by(UserEntity::getEmail).descending();

   private final UserEntity mockUser = new UserEntity();

   private final UserRequest mockDto = new UserRequest("name", "email", "pass", AuthProvider.LOCAL, Set.of(Role.USER));

   private final List<UserEntity> testUserList = IntStream.range(0, 10)
           .mapToObj(i -> UserEntity.builder()
                   .name("userTest" + (i + 1))
                   .email("test" + (i + 1) + "@mail.com")
                   .password("testSecret123")
                   .roles(Set.of(new RoleEntity(null, Role.ADMIN)))
                   .build())
           .sorted(Comparator.comparing(UserEntity::getEmail).reversed())
           .toList();

   @Test
   @DisplayName("Should return an UserEntity Page.")
   void getUsersByPages() {
      final String search = "id=10";
      final Pageable pageable = PageRequest.of(0, 5, sort);
      final List<UserEntity> subList = testUserList.subList(0, 5);
      final PageDto<UserResponse> output = mock();

      when(repo.findAll(any(Pageable.class)))
              .thenReturn(new PageImpl<>(subList));
      when(userFactory.createPageFrom(any())).thenReturn(output);

      final var page = service.getPage(pageable, search);
      final var pageList = page.content();

      assertEquals(page.content().size(), pageList.size(), "Page content size should be equals.");

      verify(repo, atLeastOnce()).findAll(any(Pageable.class));
      verify(userFactory, atLeastOnce()).createPageFrom(any());
      verifyNoMoreInteractions(repo);
   }

   @Test
   @DisplayName("Should get user by id.")
   void getUserById() {
      UserResponse output = mock();
      when(repo.findById(anyLong())).thenReturn(Optional.of(mockUser));
      when(userFactory.createFromEntity(any(UserEntity.class)))
              .thenReturn(output);
      final var result = assertDoesNotThrow(() -> service.getById(1L));

      assertNotNull(result, "Result should not be null");

      verify(repo, atLeastOnce()).findById(anyLong());
      verify(userFactory, atLeastOnce()).createFromEntity(any());
      verifyNoMoreInteractions(repo, userFactory);
   }

   @Test
   @DisplayName("Should get user by email.")
   void getUserByEmail() {
      when(repo.findByEmail(anyString())).thenReturn(Optional.of(mockUser));

      final var result = service.getUserByEmail("user@mail.com");

      assertTrue(result.isPresent(), "Result optional should be present.");
      assertNotNull(result.get(), "Result should not be null");

      verify(repo, atLeastOnce()).findByEmail(anyString());
      verifyNoMoreInteractions(repo);
   }


   @Test
   @DisplayName("Should Add User")
   void addUser() {
      UserResponse output = mock(UserResponse.class);
      when(repo.findByEmail(anyString())).thenReturn(Optional.empty());
      when(userFactory.createFromInput(mockDto)).thenReturn(mockUser);
      when(repo.saveAndFlush(mockUser)).thenReturn(mockUser);
      when(userFactory.createFromEntity(mockUser)).thenReturn(output);

      final var result = service.save(mockDto);
      assertNotNull(result, "Result should not be null");

      verify(repo).findByEmail(anyString());
      verify(userFactory).createFromInput(mockDto);
      verify(repo).saveAndFlush(mockUser);
      verify(userFactory).createFromEntity(mockUser);
      verifyNoMoreInteractions(repo);
   }

   @Test
   @DisplayName("User is deactivated")
   void userDeactivated() {
      UserEntity inactiveUser = new UserEntity();
      inactiveUser.setActive(false);
      when(repo.findByEmail(mockDto.email())).thenReturn(Optional.of(inactiveUser));

      assertThrows(NoActiveException.class, () -> service.save(mockDto));

      verify(repo, atMostOnce()).findByEmail(mockDto.email());
   }

   @Test
   @DisplayName("User already exists.")
   void userAlreadyExists() {
      when(repo.findByEmail(mockDto.email())).thenReturn(Optional.of(mockUser));

      assertThrows(AlreadyExistsException.class, () -> service.save(mockDto));

      verify(repo, atMostOnce()).findByEmail(mockDto.email());
   }

   @Test
   @DisplayName("Should Update User")
   void updateUser() {
      UserResponse output = mock(UserResponse.class);

      UserEntity user = mock();
      user.setUpdatedAt(Instant.now());

      when(repo.findById(anyLong())).
              thenReturn(Optional.of(mockUser));
      doNothing().when(userUpdater).apply(any(UserEntity.class), any(UserRequest.class));
      when(repo.saveAndFlush(any(UserEntity.class)))
              .thenReturn(new UserEntity());
      when(userFactory.createFromEntity(any(UserEntity.class)))
              .thenReturn(output);

      var result = assertDoesNotThrow(() -> service.update(1L, mockDto));

      assertNotNull(result, "User should not be null");

      verify(repo, atMostOnce()).findById(anyLong());
      verify(userUpdater, atMostOnce()).apply(any(UserEntity.class), any(UserRequest.class));
      verify(repo, atMostOnce()).saveAndFlush(any(UserEntity.class));
      verify(userFactory, atMostOnce()).createFromEntity(any(UserEntity.class));
      verifyNoMoreInteractions(repo, userUpdater, userFactory);
   }

   @Test
   @DisplayName("[SOFT_DELETE]: Should DeleteById.")
   void softDelete() {
      service.softDelete(1L);

      verify(repo, atMostOnce()).deleteById(anyLong());
      verifyNoMoreInteractions(repo);
   }
}
