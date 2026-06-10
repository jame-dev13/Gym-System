package com.jame.dev.gymApp.subscription.usecases.query;

import com.jame.dev.gymApp.features.subscription.api.response.SubscriptionResponse;
import com.jame.dev.gymApp.features.subscription.application.contract.SubscriptionFactory;
import com.jame.dev.gymApp.features.subscription.application.service.query.GetByEmailSubscriptionUseCaseService;
import com.jame.dev.gymApp.features.subscription.domain.exception.SubscriptionNotFoundException;
import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionEntity;
import com.jame.dev.gymApp.features.subscription.domain.repository.SubscriptionQueryRepository;
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
class GetByEmailSubscriptionUseCaseServiceTest {

    @Mock
    private SubscriptionQueryRepository subscriptionQueryRepository;

    @Mock
    private SubscriptionFactory subscriptionFactory;

    @InjectMocks
    private GetByEmailSubscriptionUseCaseService service;

    @Test
    @DisplayName("Should return SubscriptionResponse when subscription exists by email")
    void getByEmail_whenSubscriptionExists_returnsSubscriptionResponse() {
        var entity = new SubscriptionEntity();
        var response = mock(SubscriptionResponse.class);
        given(subscriptionQueryRepository.findByCustomerEmail(anyString())).willReturn(Optional.of(entity));
        given(subscriptionFactory.createFromEntity(any(SubscriptionEntity.class))).willReturn(response);

        var result = service.getByEmail("test@mail.com");

        assertNotNull(result);
        verify(subscriptionQueryRepository).findByCustomerEmail(anyString());
        verify(subscriptionFactory).createFromEntity(any(SubscriptionEntity.class));
        verifyNoMoreInteractions(subscriptionQueryRepository, subscriptionFactory);
    }

    @Test
    @DisplayName("Should throw SubscriptionNotFoundException when subscription not found by email")
    void getByEmail_whenSubscriptionNotFound_throwsException() {
        given(subscriptionQueryRepository.findByCustomerEmail(anyString())).willReturn(Optional.empty());

        assertThrows(SubscriptionNotFoundException.class, () -> service.getByEmail("unknown@mail.com"));

        verify(subscriptionQueryRepository).findByCustomerEmail(anyString());
        verifyNoInteractions(subscriptionFactory);
    }
}
