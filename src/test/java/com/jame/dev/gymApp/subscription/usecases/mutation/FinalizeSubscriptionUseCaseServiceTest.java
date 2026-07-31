package com.jame.dev.gymApp.subscription.usecases.mutation;

import com.jame.dev.gymApp.features.subscription.api.response.SubscriptionResponse;
import com.jame.dev.gymApp.features.subscription.application.contract.SubscriptionFactory;
import com.jame.dev.gymApp.features.subscription.application.service.mutation.FinalizeSubscriptionUseCaseService;
import com.jame.dev.gymApp.features.subscription.domain.exception.SubscriptionNotFoundException;
import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionEntity;
import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionStatus;
import com.jame.dev.gymApp.features.subscription.domain.repository.SubscriptionMutationRepository;
import com.jame.dev.gymApp.features.subscription.domain.repository.SubscriptionQueryRepository;
import com.jame.dev.gymApp.features.subscription.infrastructure.publisher.SubscriptionMutationEventPublisher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
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
class FinalizeSubscriptionUseCaseServiceTest {

    @Mock
    private SubscriptionQueryRepository subscriptionQueryRepository;

    @Mock
    private SubscriptionMutationRepository subscriptionMutationRepository;

    @Mock
    private SubscriptionFactory subscriptionFactory;

    @Mock
    private SubscriptionMutationEventPublisher subscriptionMutationEventPublisher;

    @InjectMocks
    private FinalizeSubscriptionUseCaseService service;

    @Captor
    private ArgumentCaptor<SubscriptionEntity> subscriptionCaptor;

    @Test
    @DisplayName("Should finalize subscription and return SubscriptionResponse")
    void finalize_finalizesAndReturnsResponse() {
        var entity = new SubscriptionEntity();
        var savedEntity = new SubscriptionEntity();
        var response = mock(SubscriptionResponse.class);

        given(subscriptionQueryRepository.findById(anyLong())).willReturn(Optional.of(entity));
        given(subscriptionMutationRepository.save(any(SubscriptionEntity.class))).willReturn(savedEntity);
        given(subscriptionFactory.createFromEntity(any(SubscriptionEntity.class))).willReturn(response);

        var result = service.finalize(1L);

        assertNotNull(result);
        assertEquals(SubscriptionStatus.FINALIZED, entity.getStatus());

        verify(subscriptionQueryRepository).findById(anyLong());
        verify(subscriptionMutationRepository).save(subscriptionCaptor.capture());
        verify(subscriptionFactory).createFromEntity(any(SubscriptionEntity.class));

        var captured = subscriptionCaptor.getValue();
        assertSame(entity, captured);
        assertEquals(SubscriptionStatus.FINALIZED, captured.getStatus());

        verify(subscriptionMutationEventPublisher).publishSubscriptionFinalized(savedEntity);

        verifyNoMoreInteractions(subscriptionQueryRepository, subscriptionMutationRepository, subscriptionFactory, subscriptionMutationEventPublisher);
    }

    @Test
    @DisplayName("Should throw SubscriptionNotFoundException when subscription not found")
    void finalize_whenSubscriptionNotFound_throwsException() {
        given(subscriptionQueryRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThrows(SubscriptionNotFoundException.class, () -> service.finalize(1L));

        verify(subscriptionQueryRepository).findById(anyLong());
        verifyNoInteractions(subscriptionMutationRepository, subscriptionFactory, subscriptionMutationEventPublisher);
    }
}
