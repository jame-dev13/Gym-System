package com.jame.dev.gymApp.subscription.usecases.mutation;

import com.jame.dev.gymApp.domain.exception.NotFoundException;
import com.jame.dev.gymApp.features.auth.domain.model.AuthPrincipal;
import com.jame.dev.gymApp.features.auth.domain.model.UserPrincipal;
import com.jame.dev.gymApp.features.subscription.api.request.SubscriptionCurrentRequest;
import com.jame.dev.gymApp.features.subscription.api.response.SubscriptionResponse;
import com.jame.dev.gymApp.features.subscription.application.contract.SubscriptionUpdater;
import com.jame.dev.gymApp.features.subscription.application.service.mutation.current.RenewCurrentSubscriptionUseCaseService;
import com.jame.dev.gymApp.features.subscription.application.support.mapper.SubscriptionMapper;
import com.jame.dev.gymApp.features.subscription.application.support.validator.SubscriptionValidator;
import com.jame.dev.gymApp.features.subscription.domain.exception.RenewSubscriptionException;
import com.jame.dev.gymApp.features.subscription.domain.model.Membership;
import com.jame.dev.gymApp.features.subscription.domain.model.MembershipEntity;
import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionEntity;
import com.jame.dev.gymApp.features.subscription.domain.repository.SubscriptionMutationRepository;
import com.jame.dev.gymApp.features.subscription.domain.repository.SubscriptionQueryRepository;
import com.jame.dev.gymApp.features.subscription.infrastructure.persistence.MembershipRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class RenewCurrentSubscriptionUseCaseServiceTest {

    @Mock
    private SubscriptionValidator validator;

    @Mock
    private MembershipRepository membershipRepository;

    @Mock
    private SubscriptionUpdater subscriptionUpdater;

    @Mock
    private SubscriptionMutationRepository subscriptionMutationRepository;

    @Mock
    private SubscriptionMapper subscriptionMapper;

    @Mock
    private SubscriptionQueryRepository subscriptionQueryRepository;

    @InjectMocks
    private RenewCurrentSubscriptionUseCaseService service;

    private final String customerEmail = "customer@mail.com";
    private final SubscriptionCurrentRequest request = new SubscriptionCurrentRequest(Membership.MONTHLY);
    private final AuthPrincipal principal = UserPrincipal.builder()
        .id(1L)
        .username(customerEmail)
        .build();

    @Test
    @DisplayName("Should renew current subscription and return SubscriptionResponse")
    void renew_renewsAndReturnsResponse() {
        var subscription = mock(SubscriptionEntity.class);
        var membership = mock(MembershipEntity.class);
        var savedEntity = mock(SubscriptionEntity.class);
        var response = mock(SubscriptionResponse.class);

        given(subscriptionQueryRepository.findByCustomerEmail(anyString())).willReturn(Optional.of(subscription));
        given(validator.canRenewSubscription(any(SubscriptionEntity.class))).willReturn(true);
        given(membershipRepository.findByMembership(any(Membership.class))).willReturn(Optional.of(membership));
        willDoNothing().given(subscriptionUpdater).applyRenew(any(SubscriptionEntity.class), any(MembershipEntity.class));
        given(subscriptionMutationRepository.save(any(SubscriptionEntity.class))).willReturn(savedEntity);
        given(subscriptionMapper.toDto(any(SubscriptionEntity.class))).willReturn(response);

        var result = service.renew(principal, request);

        assertNotNull(result);
        verify(subscriptionQueryRepository).findByCustomerEmail(anyString());
        verify(validator).canRenewSubscription(any(SubscriptionEntity.class));
        verify(membershipRepository).findByMembership(any(Membership.class));
        verify(subscriptionUpdater).applyRenew(any(SubscriptionEntity.class), any(MembershipEntity.class));
        verify(subscriptionMutationRepository).save(any(SubscriptionEntity.class));
        verify(subscriptionMapper).toDto(any(SubscriptionEntity.class));
        verifyNoMoreInteractions(validator, membershipRepository, subscriptionUpdater, subscriptionMutationRepository,
                subscriptionMapper, subscriptionQueryRepository);
    }

    @Test
    @DisplayName("Should throw NotFoundException when subscription not found")
    void renew_whenSubscriptionNotFound_throwsException() {
        given(subscriptionQueryRepository.findByCustomerEmail(anyString())).willReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.renew(principal, request));

        verify(subscriptionQueryRepository).findByCustomerEmail(anyString());
        verifyNoInteractions(validator, membershipRepository, subscriptionUpdater, subscriptionMutationRepository, subscriptionMapper);
    }

    @Test
    @DisplayName("Should throw RenewSubscriptionException when subscription cannot be renewed")
    void renew_whenCannotRenew_throwsException() {
        var subscription = mock(SubscriptionEntity.class);

        given(subscriptionQueryRepository.findByCustomerEmail(anyString())).willReturn(Optional.of(subscription));
        given(validator.canRenewSubscription(any(SubscriptionEntity.class))).willReturn(false);

        assertThrows(RenewSubscriptionException.class, () -> service.renew(principal, request));

        verify(subscriptionQueryRepository).findByCustomerEmail(anyString());
        verify(validator).canRenewSubscription(any(SubscriptionEntity.class));
        verifyNoInteractions(membershipRepository, subscriptionUpdater, subscriptionMutationRepository, subscriptionMapper);
    }

    @Test
    @DisplayName("Should throw NotFoundException when membership not found")
    void renew_whenMembershipNotFound_throwsException() {
        var subscription = mock(SubscriptionEntity.class);

        given(subscriptionQueryRepository.findByCustomerEmail(anyString())).willReturn(Optional.of(subscription));
        given(validator.canRenewSubscription(any(SubscriptionEntity.class))).willReturn(true);
        given(membershipRepository.findByMembership(any(Membership.class))).willReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.renew(principal, request));

        verify(subscriptionQueryRepository).findByCustomerEmail(anyString());
        verify(validator).canRenewSubscription(any(SubscriptionEntity.class));
        verify(membershipRepository).findByMembership(any(Membership.class));
        verifyNoInteractions(subscriptionUpdater, subscriptionMutationRepository, subscriptionMapper);
    }
}
