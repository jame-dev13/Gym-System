package com.jame.dev.gymApp.subscription.usecases.mutation;

import com.jame.dev.gymApp.features.subscription.application.service.mutation.current.DeleteCurrentSubscriptionUseCaseService;
import com.jame.dev.gymApp.features.subscription.domain.repository.SubscriptionMutationRepository;
import com.jame.dev.gymApp.infrastructure.security.principal.IdentityExtractorService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class DeleteCurrentSubscriptionUseCaseServiceTest {

    @Mock
    private SubscriptionMutationRepository subscriptionMutationRepository;

    @Mock
    private IdentityExtractorService identityExtractorService;

    @InjectMocks
    private DeleteCurrentSubscriptionUseCaseService service;

    private final String customerEmail = "customer@mail.com";
    private final Authentication authentication = mock(Authentication.class);

    @Test
    @DisplayName("Should delete current subscription by authenticated customer email")
    void delete_deletesSubscriptionByEmail() {
        given(identityExtractorService.extract(any())).willReturn(customerEmail);

        service.delete(authentication);

        verify(identityExtractorService).extract(any());
        verify(subscriptionMutationRepository).deleteByCustomerEmail(customerEmail);
        verifyNoMoreInteractions(subscriptionMutationRepository, identityExtractorService);
    }
}