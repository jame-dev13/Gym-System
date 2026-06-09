package com.jame.dev.gymApp.user.usecases.mutation;

import com.jame.dev.gymApp.domain.exception.NoActiveException;
import com.jame.dev.gymApp.features.auth.application.model.AuthProvider;
import com.jame.dev.gymApp.features.auth.domain.exception.AlreadyExistsException;
import com.jame.dev.gymApp.features.user.api.request.UserRequest;
import com.jame.dev.gymApp.features.user.api.response.UserResponse;
import com.jame.dev.gymApp.features.user.application.contract.UserFactory;
import com.jame.dev.gymApp.features.user.application.service.mutation.CreateUserUseCaseService;
import com.jame.dev.gymApp.features.user.domain.model.Role;
import com.jame.dev.gymApp.features.user.domain.model.UserEntity;
import com.jame.dev.gymApp.features.user.domain.repository.UserMutationRepository;
import com.jame.dev.gymApp.features.user.domain.repository.UserValidationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateUserUseCaseServiceTest {

    @Mock
    private UserMutationRepository userMutationRepository;

    @Mock
    private UserValidationRepository userValidationRepository;

    @Mock
    private UserFactory userFactory;

    @InjectMocks
    private CreateUserUseCaseService service;

    @Captor
    private ArgumentCaptor<UserEntity> userEntityCaptor;

    private final UserRequest request = UserRequest.builder()
            .name("John")
            .email("john@mail.com")
            .password("secret")
            .authProvider(AuthProvider.LOCAL)
            .roles(Set.of(Role.USER))
            .build();

    @Test
    @DisplayName("Should create user when email does not exist and is not deactivated")
    void create_whenUserDoesNotExist_createsAndReturnsResponse() {
        var entity = new UserEntity();
        var savedEntity = new UserEntity();
        var response = mock(UserResponse.class);

        given(userValidationRepository.existsAndIsDeactivatedByEmail(anyString())).willReturn(false);
        given(userValidationRepository.existsByEmail(anyString())).willReturn(false);
        given(userFactory.createFromInput(any(UserRequest.class))).willReturn(entity);
        given(userMutationRepository.save(any(UserEntity.class))).willReturn(savedEntity);
        given(userFactory.createFromEntity(any(UserEntity.class))).willReturn(response);

        var result = service.create(request);

        assertNotNull(result);

        verify(userValidationRepository).existsAndIsDeactivatedByEmail(request.email());
        verify(userValidationRepository).existsByEmail(request.email());
        verify(userFactory).createFromInput(any(UserRequest.class));
        verify(userMutationRepository).save(userEntityCaptor.capture());
        verify(userFactory).createFromEntity(any(UserEntity.class));
        assertSame(entity, userEntityCaptor.getValue());
        verifyNoMoreInteractions(userMutationRepository, userValidationRepository, userFactory);
    }

    @Test
    @DisplayName("Should throw NoActiveException when email is deactivated")
    void create_whenEmailIsDeactivated_throwsNoActiveException() {
        given(userValidationRepository.existsAndIsDeactivatedByEmail(anyString())).willReturn(true);

        assertThrows(NoActiveException.class, () -> service.create(request));

        verify(userValidationRepository).existsAndIsDeactivatedByEmail(request.email());
        verifyNoMoreInteractions(userValidationRepository);
        verifyNoInteractions(userFactory, userMutationRepository);
    }

    @Test
    @DisplayName("Should throw AlreadyExistsException when email already exists")
    void create_whenEmailAlreadyExists_throwsAlreadyExistsException() {
        given(userValidationRepository.existsAndIsDeactivatedByEmail(anyString())).willReturn(false);
        given(userValidationRepository.existsByEmail(anyString())).willReturn(true);

        assertThrows(AlreadyExistsException.class, () -> service.create(request));

        verify(userValidationRepository).existsAndIsDeactivatedByEmail(request.email());
        verify(userValidationRepository).existsByEmail(request.email());
        verifyNoMoreInteractions(userValidationRepository);
        verifyNoInteractions(userFactory, userMutationRepository);
    }
}
