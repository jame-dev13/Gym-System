package com.jame.dev.gymApp.subscription.usecases.mutation;

import com.jame.dev.gymApp.domain.exception.NotFoundException;
import com.jame.dev.gymApp.features.subscription.api.response.SubscriptionResponse;
import com.jame.dev.gymApp.features.subscription.application.contract.SubscriptionFactory;
import com.jame.dev.gymApp.features.subscription.application.service.mutation.current.FinalizeCurrentSubscriptionUseCaseService;
import com.jame.dev.gymApp.features.subscription.domain.model.PaymentEntity;
import com.jame.dev.gymApp.features.subscription.domain.model.PaymentStatus;
import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionEntity;
import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionStatus;
import com.jame.dev.gymApp.features.subscription.domain.repository.SubscriptionMutationRepository;
import com.jame.dev.gymApp.features.subscription.domain.repository.SubscriptionQueryRepository;
import com.jame.dev.gymApp.features.subscription.infrastructure.publisher.SubscriptionMutationEventPublisher;
import com.jame.dev.gymApp.infrastructure.security.principal.IdentityExtractorService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class FinalizeCurrentSubscriptionUseCaseServiceTest {

    @Mock
    private SubscriptionQueryRepository subscriptionQueryRepository;

    @Mock
    private SubscriptionMutationRepository subscriptionMutationRepository;

    @Mock
    private SubscriptionFactory subscriptionFactory;

    @Mock
    private SubscriptionMutationEventPublisher subscriptionMutationEventPublisher;

    @Mock
    private IdentityExtractorService identityExtractorService;

    @InjectMocks
    private FinalizeCurrentSubscriptionUseCaseService service;

    @Captor
    private ArgumentCaptor<SubscriptionEntity> subscriptionCaptor;

    private final String customerEmail = "customer@mail.com";
    private final Authentication authentication = mock(Authentication.class);

    @Test
    @DisplayName("Should finalizeCurrent current subscription with payment and return SubscriptionResponse")
    void finalize_Current_finalizesLastPaymentAndReturnsResponse() {
        var payment = mock(PaymentEntity.class);
        var subscription = new SubscriptionEntity();
        subscription.setPayments(List.of(payment));
        var savedEntity = mock(SubscriptionEntity.class);
        var response = mock(SubscriptionResponse.class);

        given(identityExtractorService.extract(any())).willReturn(customerEmail);
        given(subscriptionQueryRepository.findByCustomerEmail(anyString())).willReturn(Optional.of(subscription));
        given(subscriptionMutationRepository.save(any(SubscriptionEntity.class))).willReturn(savedEntity);
        given(subscriptionFactory.createFromEntity(any(SubscriptionEntity.class))).willReturn(response);

        var result = service.finalizeCurrent(authentication);

        assertNotNull(result);
        assertEquals(SubscriptionStatus.FINALIZED, subscription.getStatus());

        verify(identityExtractorService).extract(any());
        verify(subscriptionQueryRepository).findByCustomerEmail(anyString());
        verify(payment).setStatus(PaymentStatus.FINALIZED);
        verify(subscriptionMutationRepository).save(subscriptionCaptor.capture());
        verify(subscriptionFactory).createFromEntity(any(SubscriptionEntity.class));
        verify(subscriptionMutationEventPublisher).publishSubscriptionFinalized(savedEntity);

        var captured = subscriptionCaptor.getValue();
        assertSame(subscription, captured);
        assertEquals(SubscriptionStatus.FINALIZED, captured.getStatus());
        verifyNoMoreInteractions(subscriptionQueryRepository, subscriptionMutationRepository, subscriptionFactory,
                subscriptionMutationEventPublisher, identityExtractorService);
    }

    @Test
    @DisplayName("Should finalizeCurrent current subscription without payments and return SubscriptionResponse")
    void finalize_Current_withoutPayments_finalizesAndReturnsResponse() {
        var subscription = new SubscriptionEntity();
        var savedEntity = mock(SubscriptionEntity.class);
        var response = mock(SubscriptionResponse.class);

        given(identityExtractorService.extract(any())).willReturn(customerEmail);
        given(subscriptionQueryRepository.findByCustomerEmail(anyString())).willReturn(Optional.of(subscription));
        given(subscriptionMutationRepository.save(any(SubscriptionEntity.class))).willReturn(savedEntity);
        given(subscriptionFactory.createFromEntity(any(SubscriptionEntity.class))).willReturn(response);

        var result = service.finalizeCurrent(authentication);

        assertNotNull(result);
        assertEquals(SubscriptionStatus.FINALIZED, subscription.getStatus());

        verify(identityExtractorService).extract(any());
        verify(subscriptionQueryRepository).findByCustomerEmail(anyString());
        verify(subscriptionMutationRepository).save(subscriptionCaptor.capture());
        verify(subscriptionFactory).createFromEntity(any(SubscriptionEntity.class));
        verify(subscriptionMutationEventPublisher).publishSubscriptionFinalized(savedEntity);

        var captured = subscriptionCaptor.getValue();
        assertSame(subscription, captured);
        assertEquals(SubscriptionStatus.FINALIZED, captured.getStatus());
        verifyNoMoreInteractions(subscriptionQueryRepository, subscriptionMutationRepository, subscriptionFactory,
                subscriptionMutationEventPublisher, identityExtractorService);
    }

    @Test
    @DisplayName("Should throw NotFoundException when subscription not found")
    void finalize_Current_whenSubscriptionNotFound_throwsException() {
        given(identityExtractorService.extract(any())).willReturn(customerEmail);
        given(subscriptionQueryRepository.findByCustomerEmail(anyString())).willReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.finalizeCurrent(authentication));

        verify(identityExtractorService).extract(any());
        verify(subscriptionQueryRepository).findByCustomerEmail(anyString());
        verifyNoInteractions(subscriptionMutationRepository, subscriptionFactory, subscriptionMutationEventPublisher);
    }
}