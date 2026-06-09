package com.jame.dev.gymApp.user.usecases.query;

import com.jame.dev.gymApp.features.user.api.response.UserResponse;
import com.jame.dev.gymApp.features.user.application.contract.UserFactory;
import com.jame.dev.gymApp.features.user.application.service.query.GetUserByIdUseCaseService;
import com.jame.dev.gymApp.features.user.domain.exception.UserEntityNotFoundException;
import com.jame.dev.gymApp.features.user.domain.model.UserEntity;
import com.jame.dev.gymApp.features.user.domain.repository.UserQueryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetUserByIdUseCaseServiceTest {

    @Mock
    private UserQueryRepository userQueryRepository;

    @Mock
    private UserFactory userFactory;

    @InjectMocks
    private GetUserByIdUseCaseService service;

    @Test
    @DisplayName("Should return UserResponse when user exists")
    void getById_whenUserExists_returnsUserResponse() {
        var entity = new UserEntity();
        var response = mock(UserResponse.class);
        given(userQueryRepository.findById(anyLong())).willReturn(Optional.of(entity));
        given(userFactory.createFromEntity(any(UserEntity.class))).willReturn(response);

        var result = service.getById(1L);

        assertNotNull(result);
        verify(userQueryRepository).findById(anyLong());
        verify(userFactory).createFromEntity(any(UserEntity.class));
        verifyNoMoreInteractions(userQueryRepository, userFactory);
    }

    @Test
    @DisplayName("Should throw UserEntityNotFoundException when user not found")
    void getById_whenUserNotFound_throwsException() {
        given(userQueryRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThrows(UserEntityNotFoundException.class, () -> service.getById(1L));

        verify(userQueryRepository).findById(anyLong());
        verifyNoInteractions(userFactory);
    }
}
