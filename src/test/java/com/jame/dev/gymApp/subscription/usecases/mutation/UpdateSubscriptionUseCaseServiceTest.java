package com.jame.dev.gymApp.subscription.usecases.mutation;

import com.jame.dev.gymApp.features.subscription.api.request.SubscriptionRequest;
import com.jame.dev.gymApp.features.subscription.api.response.SubscriptionResponse;
import com.jame.dev.gymApp.features.subscription.application.contract.SubscriptionFactory;
import com.jame.dev.gymApp.features.subscription.application.contract.SubscriptionUpdater;
import com.jame.dev.gymApp.features.subscription.application.service.mutation.UpdateSubscriptionUseCaseService;
import com.jame.dev.gymApp.features.subscription.domain.exception.PricingNotFoundException;
import com.jame.dev.gymApp.features.subscription.domain.exception.SubscriptionNotFoundException;
import com.jame.dev.gymApp.features.subscription.domain.model.Membership;
import com.jame.dev.gymApp.features.subscription.domain.model.PricingEntity;
import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionEntity;
import com.jame.dev.gymApp.features.subscription.domain.repository.SubscriptionMutationRepository;
import com.jame.dev.gymApp.features.subscription.domain.repository.SubscriptionQueryRepository;
import com.jame.dev.gymApp.features.subscription.infrastructure.persistence.PricingRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateSubscriptionUseCaseServiceTest {

    @Mock
    private SubscriptionQueryRepository subscriptionQueryRepository;

    @Mock
    private PricingRepository pricingRepository;

    @Mock
    private SubscriptionUpdater subscriptionUpdater;

    @Mock
    private SubscriptionMutationRepository subscriptionMutationRepository;

    @Mock
    private SubscriptionFactory subscriptionFactory;

    @InjectMocks
    private UpdateSubscriptionUseCaseService service;

    private final SubscriptionRequest request = new SubscriptionRequest("customer@mail.com", Membership.MONTHLY);

    @Test
    @DisplayName("Should update and return SubscriptionResponse when subscription exists")
    void update_updatesAndReturnsResponse() {
        var subscriptionEntity = new SubscriptionEntity();
        subscriptionEntity.setUpdatedAt(Instant.now());
        var pricing = new PricingEntity();
        var modifiedEntity = new SubscriptionEntity();
        var response = mock(SubscriptionResponse.class);

        given(subscriptionQueryRepository.findById(anyLong())).willReturn(Optional.of(subscriptionEntity));
        given(pricingRepository.findByMemberShipEntity_Membership(any(Membership.class))).willReturn(Optional.of(pricing));
        willDoNothing().given(subscriptionUpdater).apply(any(SubscriptionEntity.class), any(PricingEntity.class));
        given(subscriptionMutationRepository.save(any(SubscriptionEntity.class))).willReturn(modifiedEntity);
        given(subscriptionFactory.createFromEntity(any(SubscriptionEntity.class))).willReturn(response);

        var result = service.update(1L, request);

        assertNotNull(result);
        verify(subscriptionQueryRepository).findById(anyLong());
        verify(pricingRepository).findByMemberShipEntity_Membership(any(Membership.class));
        verify(subscriptionUpdater).apply(any(SubscriptionEntity.class), any(PricingEntity.class));
        verify(subscriptionMutationRepository).save(any(SubscriptionEntity.class));
        verify(subscriptionFactory).createFromEntity(any(SubscriptionEntity.class));
        verifyNoMoreInteractions(subscriptionQueryRepository, pricingRepository, subscriptionUpdater, subscriptionMutationRepository, subscriptionFactory);
    }

    @Test
    @DisplayName("Should throw SubscriptionNotFoundException when subscription not found")
    void update_whenSubscriptionNotFound_throwsException() {
        given(subscriptionQueryRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThrows(SubscriptionNotFoundException.class, () -> service.update(1L, request));

        verify(subscriptionQueryRepository).findById(anyLong());
        verifyNoInteractions(pricingRepository, subscriptionUpdater, subscriptionMutationRepository, subscriptionFactory);
    }

    @Test
    @DisplayName("Should throw PricingNotFoundException when pricing not found")
    void update_whenPricingNotFound_throwsException() {
        var subscriptionEntity = new SubscriptionEntity();
        given(subscriptionQueryRepository.findById(anyLong())).willReturn(Optional.of(subscriptionEntity));
        given(pricingRepository.findByMemberShipEntity_Membership(any(Membership.class))).willReturn(Optional.empty());

        assertThrows(PricingNotFoundException.class, () -> service.update(1L, request));

        verify(subscriptionQueryRepository).findById(anyLong());
        verify(pricingRepository).findByMemberShipEntity_Membership(any(Membership.class));
        verifyNoInteractions(subscriptionUpdater, subscriptionMutationRepository, subscriptionFactory);
    }
}
