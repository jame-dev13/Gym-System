package com.jame.dev.gymApp.subscription.usecases.mutation;

import com.jame.dev.gymApp.features.auth.domain.model.AuthPrincipal;
import com.jame.dev.gymApp.features.auth.domain.model.UserPrincipal;
import com.jame.dev.gymApp.features.subscription.application.service.mutation.current.DeleteCurrentSubscriptionUseCaseService;
import com.jame.dev.gymApp.features.subscription.domain.repository.SubscriptionMutationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class DeleteCurrentSubscriptionUseCaseServiceTest {

    @Mock
    private SubscriptionMutationRepository subscriptionMutationRepository;

    @InjectMocks
    private DeleteCurrentSubscriptionUseCaseService service;

    private final String customerEmail = "customer@mail.com";
    private final AuthPrincipal principal = UserPrincipal.builder()
        .id(1L)
        .username(customerEmail)
        .build();

    @Test
    @DisplayName("Should delete current subscription by authenticated customer email")
    void delete_deletesSubscriptionByEmail() {
        service.delete(principal);

        verify(subscriptionMutationRepository).deleteByCustomerEmail(customerEmail);
        verifyNoMoreInteractions(subscriptionMutationRepository);
    }
}
