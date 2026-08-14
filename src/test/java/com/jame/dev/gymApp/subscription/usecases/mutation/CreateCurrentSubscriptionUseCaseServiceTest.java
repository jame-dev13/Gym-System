package com.jame.dev.gymApp.subscription.usecases.mutation;

import com.jame.dev.gymApp.domain.exception.NotFoundException;
import com.jame.dev.gymApp.features.auth.domain.exception.AlreadyExistsException;
import com.jame.dev.gymApp.features.customer.domain.model.CustomerEntity;
import com.jame.dev.gymApp.features.customer.domain.repository.CustomerQueryRepository;
import com.jame.dev.gymApp.features.subscription.api.request.SubscriptionCurrentRequest;
import com.jame.dev.gymApp.features.subscription.api.response.SubscriptionResponse;
import com.jame.dev.gymApp.features.subscription.application.contract.SubscriptionFactory;
import com.jame.dev.gymApp.features.subscription.application.dto.SubscriptionFactoryDtoInput;
import com.jame.dev.gymApp.features.subscription.application.service.mutation.current.CreateCurrentSubscriptionUseCaseService;
import com.jame.dev.gymApp.features.subscription.domain.exception.PricingNotFoundException;
import com.jame.dev.gymApp.features.subscription.domain.model.Membership;
import com.jame.dev.gymApp.features.subscription.domain.model.PricingEntity;
import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionEntity;
import com.jame.dev.gymApp.features.subscription.domain.repository.SubscriptionMutationRepository;
import com.jame.dev.gymApp.features.subscription.domain.repository.SubscriptionValidationRepository;
import com.jame.dev.gymApp.features.subscription.infrastructure.persistence.PricingRepository;
import com.jame.dev.gymApp.infrastructure.security.principal.IdentityExtractorService;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class CreateCurrentSubscriptionUseCaseServiceTest {

    @Mock
    private SubscriptionMutationRepository subscriptionMutationRepository;

    @Mock
    private CustomerQueryRepository customerQueryRepository;

    @Mock
    private PricingRepository pricingRepository;

    @Mock
    private SubscriptionValidationRepository subscriptionValidationRepository;

    @Mock
    private SubscriptionFactory subscriptionFactory;

    @Mock
    private IdentityExtractorService identityExtractorService;

    @InjectMocks
    private CreateCurrentSubscriptionUseCaseService service;

    private final String customerEmail = "customer@mail.com";
    private final SubscriptionCurrentRequest request = new SubscriptionCurrentRequest(Membership.MONTHLY);
    private final Authentication authentication = mock(Authentication.class);

    @Test
    @DisplayName("Should create current subscription and return SubscriptionResponse")
    void create_createsAndReturnsResponse() {
        var customer = mock(CustomerEntity.class);
        var pricing = mock(PricingEntity.class);
        var entity = mock(SubscriptionEntity.class);
        var savedEntity = mock(SubscriptionEntity.class);
        var response = mock(SubscriptionResponse.class);

        given(identityExtractorService.extract(any())).willReturn(customerEmail);
        given(customerQueryRepository.findByUserEmail(anyString())).willReturn(Optional.of(customer));
        given(subscriptionValidationRepository.existsByCustomer(any(CustomerEntity.class))).willReturn(false);
        given(pricingRepository.findByMemberShipEntity_Membership(any(Membership.class))).willReturn(Optional.of(pricing));
        given(subscriptionFactory.createFromInput(any(SubscriptionFactoryDtoInput.class))).willReturn(entity);
        given(subscriptionMutationRepository.save(any(SubscriptionEntity.class))).willReturn(savedEntity);
        given(subscriptionFactory.createFromEntity(any(SubscriptionEntity.class))).willReturn(response);

        var result = service.create(authentication, request);

        assertNotNull(result);
        verify(identityExtractorService).extract(any());
        verify(customerQueryRepository).findByUserEmail(anyString());
        verify(subscriptionValidationRepository).existsByCustomer(any(CustomerEntity.class));
        verify(pricingRepository).findByMemberShipEntity_Membership(any(Membership.class));
        verify(subscriptionFactory).createFromInput(any(SubscriptionFactoryDtoInput.class));
        verify(subscriptionMutationRepository).save(any(SubscriptionEntity.class));
        verify(subscriptionFactory).createFromEntity(any(SubscriptionEntity.class));
        verifyNoMoreInteractions(subscriptionMutationRepository, customerQueryRepository, pricingRepository,
                subscriptionValidationRepository, subscriptionFactory, identityExtractorService);
    }

    @Test
    @DisplayName("Should throw NotFoundException when customer not found")
    void create_whenCustomerNotFound_throwsException() {
        given(identityExtractorService.extract(any())).willReturn(customerEmail);
        given(customerQueryRepository.findByUserEmail(anyString())).willReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.create(authentication, request));

        verify(identityExtractorService).extract(any());
        verify(customerQueryRepository).findByUserEmail(anyString());
        verifyNoInteractions(subscriptionMutationRepository, pricingRepository,
                subscriptionValidationRepository, subscriptionFactory);
    }

    @Test
    @DisplayName("Should throw AlreadyExistsException when subscription already exists")
    void create_whenAlreadyExists_throwsException() {
        var customer = mock(CustomerEntity.class);

        given(identityExtractorService.extract(any())).willReturn(customerEmail);
        given(customerQueryRepository.findByUserEmail(anyString())).willReturn(Optional.of(customer));
        given(subscriptionValidationRepository.existsByCustomer(any(CustomerEntity.class))).willReturn(true);

        assertThrows(AlreadyExistsException.class, () -> service.create(authentication, request));

        verify(identityExtractorService).extract(any());
        verify(customerQueryRepository).findByUserEmail(anyString());
        verify(subscriptionValidationRepository).existsByCustomer(any(CustomerEntity.class));
        verifyNoInteractions(subscriptionMutationRepository, pricingRepository, subscriptionFactory);
    }

    @Test
    @DisplayName("Should throw PricingNotFoundException when pricing not found")
    void create_whenPricingNotFound_throwsException() {
        var customer = mock(CustomerEntity.class);

        given(identityExtractorService.extract(any())).willReturn(customerEmail);
        given(customerQueryRepository.findByUserEmail(anyString())).willReturn(Optional.of(customer));
        given(subscriptionValidationRepository.existsByCustomer(any(CustomerEntity.class))).willReturn(false);
        given(pricingRepository.findByMemberShipEntity_Membership(any(Membership.class))).willReturn(Optional.empty());

        assertThrows(PricingNotFoundException.class, () -> service.create(authentication, request));

        verify(identityExtractorService).extract(any());
        verify(customerQueryRepository).findByUserEmail(anyString());
        verify(subscriptionValidationRepository).existsByCustomer(any(CustomerEntity.class));
        verify(pricingRepository).findByMemberShipEntity_Membership(any(Membership.class));
        verifyNoInteractions(subscriptionMutationRepository, subscriptionFactory);
    }
}