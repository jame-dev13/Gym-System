package com.jame.dev.gymApp.subscription.usecases.mutation;

import com.jame.dev.gymApp.domain.exception.NotFoundException;
import com.jame.dev.gymApp.features.subscription.api.request.SubscriptionCurrentRequest;
import com.jame.dev.gymApp.features.subscription.api.response.SubscriptionResponse;
import com.jame.dev.gymApp.features.subscription.application.contract.SubscriptionUpdater;
import com.jame.dev.gymApp.features.subscription.application.service.mutation.current.RenewCurrentSubscriptionUseCaseService;
import com.jame.dev.gymApp.features.subscription.application.support.mapper.SubscriptionMapper;
import com.jame.dev.gymApp.features.subscription.application.support.validator.SubscriptionValidator;
import com.jame.dev.gymApp.features.subscription.domain.exception.RenewSubscriptionException;
import com.jame.dev.gymApp.features.subscription.domain.model.Membership;
import com.jame.dev.gymApp.features.subscription.domain.model.PricingEntity;
import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionEntity;
import com.jame.dev.gymApp.features.subscription.domain.repository.SubscriptionMutationRepository;
import com.jame.dev.gymApp.features.subscription.domain.repository.SubscriptionQueryRepository;
import com.jame.dev.gymApp.features.subscription.infrastructure.persistence.PricingRepository;
import com.jame.dev.gymApp.features.auth.infrastructure.security.identity.IdentityExtractorService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

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
    private PricingRepository pricingRepository;

    @Mock
    private SubscriptionUpdater subscriptionUpdater;

    @Mock
    private SubscriptionMutationRepository subscriptionMutationRepository;

    @Mock
    private SubscriptionMapper subscriptionMapper;

    @Mock
    private SubscriptionQueryRepository subscriptionQueryRepository;

    @Mock
    private IdentityExtractorService identityExtractorService;

    @InjectMocks
    private RenewCurrentSubscriptionUseCaseService service;

    private final String customerEmail = "customer@mail.com";
    private final SubscriptionCurrentRequest request = new SubscriptionCurrentRequest(Membership.MONTHLY);
    private final Authentication authentication = mock(Authentication.class);

    @Test
    @DisplayName("Should renew current subscription and return SubscriptionResponse")
    void renew_renewsAndReturnsResponse() {
        var subscription = mock(SubscriptionEntity.class);
        var pricing = mock(PricingEntity.class);
        var savedEntity = mock(SubscriptionEntity.class);
        var response = mock(SubscriptionResponse.class);

        given(identityExtractorService.extract(any())).willReturn(customerEmail);
        given(subscriptionQueryRepository.findByCustomerEmail(anyString())).willReturn(Optional.of(subscription));
        given(validator.canRenewSubscription(any(SubscriptionEntity.class))).willReturn(true);
        given(pricingRepository.findByMemberShipEntity_Membership(any(Membership.class))).willReturn(Optional.of(pricing));
        willDoNothing().given(subscriptionUpdater).applyRenew(any(SubscriptionEntity.class), any(PricingEntity.class));
        given(subscriptionMutationRepository.save(any(SubscriptionEntity.class))).willReturn(savedEntity);
        given(subscriptionMapper.toDto(any(SubscriptionEntity.class))).willReturn(response);

        var result = service.renew(authentication, request);

        assertNotNull(result);
        verify(identityExtractorService).extract(any());
        verify(subscriptionQueryRepository).findByCustomerEmail(anyString());
        verify(validator).canRenewSubscription(any(SubscriptionEntity.class));
        verify(pricingRepository).findByMemberShipEntity_Membership(any(Membership.class));
        verify(subscriptionUpdater).applyRenew(any(SubscriptionEntity.class), any(PricingEntity.class));
        verify(subscriptionMutationRepository).save(any(SubscriptionEntity.class));
        verify(subscriptionMapper).toDto(any(SubscriptionEntity.class));
        verifyNoMoreInteractions(validator, pricingRepository, subscriptionUpdater, subscriptionMutationRepository,
                subscriptionMapper, subscriptionQueryRepository, identityExtractorService);
    }

    @Test
    @DisplayName("Should throw NotFoundException when subscription not found")
    void renew_whenSubscriptionNotFound_throwsException() {
        given(identityExtractorService.extract(any())).willReturn(customerEmail);
        given(subscriptionQueryRepository.findByCustomerEmail(anyString())).willReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.renew(authentication, request));

        verify(identityExtractorService).extract(any());
        verify(subscriptionQueryRepository).findByCustomerEmail(anyString());
        verifyNoInteractions(validator, pricingRepository, subscriptionUpdater, subscriptionMutationRepository, subscriptionMapper);
    }

    @Test
    @DisplayName("Should throw RenewSubscriptionException when subscription cannot be renewed")
    void renew_whenCannotRenew_throwsException() {
        var subscription = mock(SubscriptionEntity.class);

        given(identityExtractorService.extract(any())).willReturn(customerEmail);
        given(subscriptionQueryRepository.findByCustomerEmail(anyString())).willReturn(Optional.of(subscription));
        given(validator.canRenewSubscription(any(SubscriptionEntity.class))).willReturn(false);

        assertThrows(RenewSubscriptionException.class, () -> service.renew(authentication, request));

        verify(identityExtractorService).extract(any());
        verify(subscriptionQueryRepository).findByCustomerEmail(anyString());
        verify(validator).canRenewSubscription(any(SubscriptionEntity.class));
        verifyNoInteractions(pricingRepository, subscriptionUpdater, subscriptionMutationRepository, subscriptionMapper);
    }

    @Test
    @DisplayName("Should throw NotFoundException when pricing not found")
    void renew_whenPricingNotFound_throwsException() {
        var subscription = mock(SubscriptionEntity.class);

        given(identityExtractorService.extract(any())).willReturn(customerEmail);
        given(subscriptionQueryRepository.findByCustomerEmail(anyString())).willReturn(Optional.of(subscription));
        given(validator.canRenewSubscription(any(SubscriptionEntity.class))).willReturn(true);
        given(pricingRepository.findByMemberShipEntity_Membership(any(Membership.class))).willReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.renew(authentication, request));

        verify(identityExtractorService).extract(any());
        verify(subscriptionQueryRepository).findByCustomerEmail(anyString());
        verify(validator).canRenewSubscription(any(SubscriptionEntity.class));
        verify(pricingRepository).findByMemberShipEntity_Membership(any(Membership.class));
        verifyNoInteractions(subscriptionUpdater, subscriptionMutationRepository, subscriptionMapper);
    }
}