package com.jame.dev.gymApp.customer.usecases.mutation;

import com.jame.dev.gymApp.features.auth.domain.model.AuthPrincipal;
import com.jame.dev.gymApp.features.auth.domain.model.UserPrincipal;
import com.jame.dev.gymApp.features.customer.application.service.mutation.DeleteCurrentCustomerUseCaseService;
import com.jame.dev.gymApp.features.customer.domain.repository.CustomerMutationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class DeleteCurrentCustomerUseCaseServiceTest {

    @Mock
    private CustomerMutationRepository customerMutationRepository;

    @InjectMocks
    private DeleteCurrentCustomerUseCaseService service;

    private final AuthPrincipal principal = UserPrincipal.builder()
        .id(42L)
        .username("user@mail.com")
        .build();

    @Test
    @DisplayName("Should delete the customer related to the authenticated user id")
    void deleteCurrent_deletesByResolvedUserId() {
        assertDoesNotThrow(() -> service.deleteCurrent(principal));

        verify(customerMutationRepository).deleteById(42L);
        verifyNoMoreInteractions(customerMutationRepository);
    }
}
