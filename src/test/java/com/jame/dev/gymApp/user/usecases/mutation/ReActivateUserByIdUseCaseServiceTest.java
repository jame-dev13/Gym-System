package com.jame.dev.gymApp.user.usecases.mutation;

import com.jame.dev.gymApp.features.auth.application.model.AuthProvider;
import com.jame.dev.gymApp.features.user.api.request.UserRequest;
import com.jame.dev.gymApp.features.user.application.contract.UserUpdater;
import com.jame.dev.gymApp.features.user.application.service.mutation.ReActivateUserByIdUseCaseService;
import com.jame.dev.gymApp.features.user.domain.exception.UserEntityNotFoundException;
import com.jame.dev.gymApp.features.user.domain.model.Role;
import com.jame.dev.gymApp.features.user.domain.model.RoleEntity;
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
class ReActivateUserByIdUseCaseServiceTest {

    @Mock
    private UserMutationRepository userMutationRepository;

    @Mock
    private UserQueryRepository userQueryRepository;

    @Mock
    private UserUpdater userUpdater;

    @InjectMocks
    private ReActivateUserByIdUseCaseService service;

    @Captor
    private ArgumentCaptor<UserEntity> userEntityCaptor;

    @Test
    @DisplayName("Should reactivate user when found")
    void reActivateById_whenUserFound_reactivatesAndSaves() {
        var deactivatedEntity = UserEntity.builder()
                .name("John")
                .email("john@mail.com")
                .password("secret")
                .provider(AuthProvider.LOCAL)
                .roles(Set.of(new RoleEntity(null, Role.USER)))
                .build();
        deactivatedEntity.setActive(false);

        given(userQueryRepository.findDeactivatedById(anyLong())).willReturn(Optional.of(deactivatedEntity));
        willDoNothing().given(userUpdater).apply(any(UserEntity.class), any(UserRequest.class));
        given(userMutationRepository.save(any(UserEntity.class))).willReturn(deactivatedEntity);

        service.reActivateById(1L);

        verify(userQueryRepository).findDeactivatedById(anyLong());
        verify(userUpdater).apply(any(UserEntity.class), any(UserRequest.class));
        verify(userMutationRepository).save(userEntityCaptor.capture());

        var saved = userEntityCaptor.getValue();
        assertTrue(saved.isActive());
        assertSame(deactivatedEntity, saved);
        verifyNoMoreInteractions(userMutationRepository, userQueryRepository, userUpdater);
    }

    @Test
    @DisplayName("Should throw UserEntityNotFoundException when user not found")
    void reActivateById_whenUserNotFound_throwsException() {
        given(userQueryRepository.findDeactivatedById(anyLong())).willReturn(Optional.empty());

        assertThrows(UserEntityNotFoundException.class, () -> service.reActivateById(1L));

        verify(userQueryRepository).findDeactivatedById(anyLong());
        verifyNoInteractions(userUpdater, userMutationRepository);
    }
}
