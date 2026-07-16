package com.jame.dev.gymApp.user.usecases.mutation;

import com.jame.dev.gymApp.features.user.api.request.UserUpdateRequest;
import com.jame.dev.gymApp.features.user.api.response.UserResponse;
import com.jame.dev.gymApp.features.user.application.contract.UserFactory;
import com.jame.dev.gymApp.features.user.application.contract.UserUpdater;
import com.jame.dev.gymApp.features.user.application.service.mutation.UpdateUserUseCaseService;
import com.jame.dev.gymApp.features.user.domain.exception.UserEntityNotFoundException;
import com.jame.dev.gymApp.features.user.domain.model.Role;
import com.jame.dev.gymApp.features.user.domain.model.UserEntity;
import com.jame.dev.gymApp.features.user.domain.repository.UserMutationRepository;
import com.jame.dev.gymApp.features.user.domain.repository.UserQueryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateUserUseCaseServiceTest {

    @Mock
    private UserMutationRepository userMutationRepository;

    @Mock
    private UserQueryRepository userQueryRepository;

    @Mock
    private UserFactory userFactory;

    @Mock
    private UserUpdater userUpdater;

    @InjectMocks
    private UpdateUserUseCaseService service;

    @Captor
    private ArgumentCaptor<UserEntity> userEntityCaptor;

    private final UserUpdateRequest request = UserUpdateRequest.builder()
            .name("John Updated")
            .email("john@mail.com")
            .roles(Set.of(Role.USER))
            .build();

    @Test
    @DisplayName("Should update and return UserResponse when user exists")
    void update_whenUserExists_updatesAndReturnsResponse() {
        var entity = new UserEntity();
        var savedEntity = new UserEntity();
        var response = mock(UserResponse.class);

        given(userQueryRepository.findById(anyLong())).willReturn(Optional.of(entity));
        willDoNothing().given(userUpdater).apply(any(UserEntity.class), any(UserUpdateRequest.class));
        given(userMutationRepository.save(any(UserEntity.class))).willReturn(savedEntity);
        given(userFactory.createFromEntity(any(UserEntity.class))).willReturn(response);

        var result = service.update(1L, request);

        assertNotNull(result);
        verify(userQueryRepository).findById(anyLong());
        verify(userUpdater).apply(any(UserEntity.class), any(UserUpdateRequest.class));
        verify(userMutationRepository).save(userEntityCaptor.capture());
        verify(userFactory).createFromEntity(any(UserEntity.class));
        assertSame(entity, userEntityCaptor.getValue());
        verifyNoMoreInteractions(userMutationRepository, userQueryRepository, userFactory, userUpdater);
    }

    @Test
    @DisplayName("Should throw UserEntityNotFoundException when user not found")
    void update_whenUserNotFound_throwsException() {
        given(userQueryRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThrows(UserEntityNotFoundException.class, () -> service.update(1L, request));

        verify(userQueryRepository).findById(anyLong());
        verifyNoInteractions(userUpdater, userMutationRepository, userFactory);
    }
}
