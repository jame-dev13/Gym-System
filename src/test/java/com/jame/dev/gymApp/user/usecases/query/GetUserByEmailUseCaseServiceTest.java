package com.jame.dev.gymApp.user.usecases.query;

import com.jame.dev.gymApp.features.user.api.response.UserResponse;
import com.jame.dev.gymApp.features.user.application.contract.UserFactory;
import com.jame.dev.gymApp.features.user.application.service.query.GetUserByEmailUseCaseService;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetUserByEmailUseCaseServiceTest {

    @Mock
    private UserQueryRepository userQueryRepository;

    @Mock
    private UserFactory userFactory;

    @InjectMocks
    private GetUserByEmailUseCaseService service;

    @Test
    @DisplayName("Should return UserResponse when user exists")
    void getByEmail_whenUserExists_returnsUserResponse() {
        var entity = new UserEntity();
        var response = mock(UserResponse.class);
        given(userQueryRepository.findByEmail(anyString())).willReturn(Optional.of(entity));
        given(userFactory.createFromEntity(any(UserEntity.class))).willReturn(response);

        var result = service.getByEmail("test@mail.com");

        assertNotNull(result);
        verify(userQueryRepository).findByEmail(anyString());
        verify(userFactory).createFromEntity(any(UserEntity.class));
        verifyNoMoreInteractions(userQueryRepository, userFactory);
    }

    @Test
    @DisplayName("Should throw UserEntityNotFoundException when user not found")
    void getByEmail_whenUserNotFound_throwsException() {
        given(userQueryRepository.findByEmail(anyString())).willReturn(Optional.empty());

        assertThrows(UserEntityNotFoundException.class, () -> service.getByEmail("test@mail.com"));

        verify(userQueryRepository).findByEmail(anyString());
        verifyNoInteractions(userFactory);
    }
}
