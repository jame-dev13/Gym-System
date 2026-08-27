package com.jame.dev.gymApp.subscription.usecases.mutation;

import com.jame.dev.gymApp.features.subscription.api.request.SubscriptionRequest;
import com.jame.dev.gymApp.features.subscription.api.response.SubscriptionResponse;
import com.jame.dev.gymApp.features.subscription.application.contract.SubscriptionFactory;
import com.jame.dev.gymApp.features.subscription.application.contract.SubscriptionUpdater;
import com.jame.dev.gymApp.features.subscription.application.service.mutation.RenewSubscriptionUseCaseService;
import com.jame.dev.gymApp.features.subscription.application.support.validator.SubscriptionValidator;
import com.jame.dev.gymApp.features.subscription.domain.model.Membership;
import com.jame.dev.gymApp.features.subscription.domain.model.MembershipEntity;
import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionEntity;
import com.jame.dev.gymApp.features.subscription.domain.repository.SubscriptionMutationRepository;
import com.jame.dev.gymApp.features.subscription.infrastructure.persistence.MembershipRepository;
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
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RenewSubscriptionUseCaseServiceTest {

    @Mock
    private SubscriptionValidator validator;

    @Mock
    private MembershipRepository membershipRepository;

    @Mock
    private SubscriptionUpdater subscriptionUpdater;

    @Mock
    private SubscriptionMutationRepository subscriptionMutationRepository;

    @Mock
    private SubscriptionFactory subscriptionFactory;

    @InjectMocks
    private RenewSubscriptionUseCaseService service;

    private final SubscriptionRequest input = new SubscriptionRequest("customer@mail.com", Membership.MONTHLY);

    @Test
    @DisplayName("Should renew subscription and return SubscriptionResponse")
    void renew_renewsAndReturnsResponse() {
        var subscription = mock(SubscriptionEntity.class);
        var membership = mock(MembershipEntity.class);
        var renewedEntity = mock(SubscriptionEntity.class);
        var response = mock(SubscriptionResponse.class);

        given(validator.validateOnRenew(anyLong(), any(SubscriptionRequest.class))).willReturn(subscription);
        given(membershipRepository.findByMembership(any(Membership.class))).willReturn(Optional.of(membership));
        willDoNothing().given(subscriptionUpdater).applyRenew(any(SubscriptionEntity.class), any(MembershipEntity.class));
        given(subscriptionMutationRepository.save(any(SubscriptionEntity.class))).willReturn(renewedEntity);
        given(subscriptionFactory.createFromEntity(any(SubscriptionEntity.class))).willReturn(response);

        var result = service.renew(1L, input);

        assertNotNull(result);
        verify(validator).validateOnRenew(anyLong(), any(SubscriptionRequest.class));
        verify(membershipRepository).findByMembership(any(Membership.class));
        verify(subscriptionUpdater).applyRenew(any(SubscriptionEntity.class), any(MembershipEntity.class));
        verify(subscriptionMutationRepository).save(any(SubscriptionEntity.class));
        verify(subscriptionFactory).createFromEntity(any(SubscriptionEntity.class));
        verifyNoMoreInteractions(validator, membershipRepository, subscriptionUpdater, subscriptionMutationRepository, subscriptionFactory);
    }
}
