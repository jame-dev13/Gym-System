package com.jame.dev.gymApp.customer.usecases.mutation;

import com.jame.dev.gymApp.features.auth.domain.exception.AuthenticationNullException;
import com.jame.dev.gymApp.features.auth.infrastructure.auth.AuthenticationUserResolver;
import com.jame.dev.gymApp.features.customer.application.service.mutation.DeleteCurrentCustomerUseCaseService;
import com.jame.dev.gymApp.features.customer.domain.repository.CustomerMutationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteCurrentCustomerUseCaseServiceTest {

    @Mock
    private CustomerMutationRepository customerMutationRepository;

    @Mock
    private AuthenticationUserResolver authenticationUserResolver;

    @InjectMocks
    private DeleteCurrentCustomerUseCaseService service;

    private final Authentication authentication = mock(Authentication.class);

    @Test
    @DisplayName("Should delete the customer related to the resolved authenticated user id")
    void deleteCurrent_deletesByResolvedUserId() {
        given(authenticationUserResolver.resolveUserId(any(Authentication.class))).willReturn(42L);

        assertDoesNotThrow(() -> service.deleteCurrent(authentication));

        verify(authenticationUserResolver).resolveUserId(authentication);
        verify(customerMutationRepository).deleteByUserId(42L);
        verifyNoMoreInteractions(authenticationUserResolver, customerMutationRepository);
    }

    @Test
    @DisplayName("Should throw AuthenticationNullException when no authenticated user is found")
    void deleteCurrent_whenAuthenticationIsNull_throwsAuthenticationNullException() {
        given(authenticationUserResolver.resolveUserId(any(Authentication.class)))
            .willThrow(new AuthenticationNullException("No Authenticated user were found."));

        assertThrows(AuthenticationNullException.class, () -> service.deleteCurrent(authentication));

        verify(authenticationUserResolver).resolveUserId(any(Authentication.class));
        verifyNoInteractions(customerMutationRepository);
    }
}