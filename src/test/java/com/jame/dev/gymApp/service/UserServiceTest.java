package com.jame.dev.gymApp.service;

import com.jame.dev.gymApp.entity.RoleEntity;
import com.jame.dev.gymApp.entity.UserEntity;
import com.jame.dev.gymApp.exception.AlreadyExistsException;
import com.jame.dev.gymApp.exception.NoActiveException;
import com.jame.dev.gymApp.factories.UserFactory;
import com.jame.dev.gymApp.model.dto.in.UserDtoInput;
import com.jame.dev.gymApp.model.dto.out.CacheMutated;
import com.jame.dev.gymApp.repository.UserRepository;
import com.jame.dev.gymApp.service.out.UserServiceImplementation;
import com.jame.dev.gymApp.shared.enums.AuthProvider;
import com.jame.dev.gymApp.shared.enums.Role;
import com.jame.dev.gymApp.updaters.UserUpdater;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
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
   @Mock
   private ApplicationEventPublisher eventPublisher;

   @InjectMocks
   private UserServiceImplementation service;

   private final Sort sort = Sort.sort(UserEntity.class).by(UserEntity::getEmail).descending();

   private final UserEntity mockUser = new UserEntity();

   private final UserDtoInput mockDto = new UserDtoInput("name", "email", "pass", AuthProvider.LOCAL, Set.of(Role.USER));

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
      final Pageable pageable = PageRequest.of(0, 5, sort);
      final List<UserEntity> subList = testUserList.subList(0, 5);

      when(repo.findAllByActiveTrue(any(Pageable.class)))
              .thenReturn(new PageImpl<>(subList));

      final var page = service.getPage(pageable);
      final var pageList = page.getContent();

      assertEquals(page.getContent().size(), pageList.size(), "Page content size should be equals.");

      verify(repo, atLeastOnce()).findAllByActiveTrue(any(Pageable.class));
      verifyNoMoreInteractions(repo);
   }

   @Test
   @DisplayName("Should get user by id.")
   void getUserById() {
      when(repo.findById(anyLong())).thenReturn(Optional.of(mockUser));

      final Optional<UserEntity> result = service.getById(1L);

      assertTrue(result.isPresent(), "Result optional should be present.");
      assertNotNull(result.get(), "Result should not be null");

      verify(repo, atLeastOnce()).findById(anyLong());
      verifyNoMoreInteractions(repo);
   }

   @Test
   @DisplayName("Should get user by email.")
   void getUserByEmail() {
      when(repo.findByEmail(anyString())).thenReturn(Optional.of(mockUser));

      final Optional<UserEntity> result = service.getUserByEmail("user@mail.com");

      assertTrue(result.isPresent(), "Result optional should be present.");
      assertNotNull(result.get(), "Result should not be null");

      verify(repo, atLeastOnce()).findByEmail(anyString());
      verifyNoMoreInteractions(repo);
   }


   @Test
   @DisplayName("Should Add User")
   void addUser() {
      when(repo.findByEmail(anyString())).thenReturn(Optional.empty());
      when(userFactory.createFrom(mockDto)).thenReturn(mockUser);
      when(repo.saveAndFlush(mockUser)).thenReturn(mockUser);

      final UserEntity result = service.save(mockDto);
      assertNotNull(result, "Result should not be null");

      verify(repo).findByEmail(anyString());
      verify(userFactory).createFrom(mockDto);
      verify(eventPublisher).publishEvent(any(CacheMutated.class));
      verify(repo).saveAndFlush(mockUser);
      verifyNoMoreInteractions(repo);
      verifyNoMoreInteractions(eventPublisher);
   }

   @Test
   @DisplayName("User is deactivated")
   void userDeactivated(){
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
      UserEntity user = new UserEntity();
      user.setUpdatedAt(Instant.now());

      when(repo.findById(1L)).thenReturn(Optional.of(mockUser));
      when(userUpdater.apply(mockUser, mockDto)).thenReturn(user);
      when(repo.saveAndFlush(user)).thenReturn(user);

      final UserEntity result = service.update(1L, mockDto);

      assertNotNull(result, "User should not be null");

      verify(repo, atMostOnce()).findById(anyLong());
      verify(userUpdater, atMostOnce()).apply(mockUser, mockDto);
      verify(repo, atMostOnce()).saveAndFlush(user);
      verify(eventPublisher, atMostOnce()).publishEvent(any(CacheMutated.class));
      verifyNoMoreInteractions(repo);
      verifyNoMoreInteractions(eventPublisher);
   }

   @Test
   @DisplayName("[SOFT_DELETE]: Should DeleteById.")
   void softDelete() {
      service.softDelete(1L);

      verify(repo, atMostOnce()).deleteById(anyLong());
      verifyNoMoreInteractions(repo);
   }
}
