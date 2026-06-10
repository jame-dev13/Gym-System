package com.jame.dev.gymApp.features.subscription.application.service.mutation;

import com.jame.dev.gymApp.features.audit.domain.model.AuditLogAction;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogEntityType;
import com.jame.dev.gymApp.features.audit.infrastructure.annotation.AuditLog;
import com.jame.dev.gymApp.features.auth.domain.exception.AlreadyExistsException;
import com.jame.dev.gymApp.features.customer.domain.exception.CustomerNotFoundException;
import com.jame.dev.gymApp.features.customer.domain.model.CustomerEntity;
import com.jame.dev.gymApp.features.customer.domain.repository.CustomerQueryRepository;
import com.jame.dev.gymApp.features.subscription.api.request.SubscriptionRequest;
import com.jame.dev.gymApp.features.subscription.api.response.SubscriptionResponse;
import com.jame.dev.gymApp.features.subscription.application.contract.SubscriptionFactory;
import com.jame.dev.gymApp.features.subscription.application.dto.SubscriptionFactoryDtoInput;
import com.jame.dev.gymApp.features.subscription.application.usecases.mutation.CreateSubscriptionUseCase;
import com.jame.dev.gymApp.features.subscription.domain.exception.PricingNotFoundException;
import com.jame.dev.gymApp.features.subscription.domain.model.PricingEntity;
import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionEntity;
import com.jame.dev.gymApp.features.subscription.domain.repository.SubscriptionMutationRepository;
import com.jame.dev.gymApp.features.subscription.domain.repository.SubscriptionValidationRepository;
import com.jame.dev.gymApp.features.subscription.infrastructure.persistence.PricingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static com.jame.dev.gymApp.application.model.CacheValues.SUBSCRIPTIONS;

@Service
@RequiredArgsConstructor
public class CreateSubscriptionUseCaseService implements CreateSubscriptionUseCase {
    private final SubscriptionMutationRepository subscriptionMutationRepository;
    private final CustomerQueryRepository customerQueryRepository;
    private final PricingRepository pricingRepository;
    private final SubscriptionValidationRepository subscriptionValidationRepository;
    private final SubscriptionFactory subscriptionFactory;

    @Override
    @Transactional
    @CacheEvict(value = SUBSCRIPTIONS, allEntries = true)
    @AuditLog(
        action = AuditLogAction.INSERT,
        entityType = AuditLogEntityType.SUBSCRIPTION,
        input = "#request",
        entityId = "#result.id",
        result = "#result"
    )
    public SubscriptionResponse create(SubscriptionRequest request) {
        final CustomerEntity customer = customerQueryRepository.findByUserEmail(request.customerEmail())
            .orElseThrow(() -> new CustomerNotFoundException("Customer Not Found."));

        if (subscriptionValidationRepository.existsByCustomer(customer)) {
            throw new AlreadyExistsException("There's a subscription linked to the customer with: " + request.customerEmail());
        }

        final PricingEntity pricing = pricingRepository.findByMemberShipEntity_Membership(request.membership())
            .orElseThrow(() -> new PricingNotFoundException("Pricing Not Found."));

        final SubscriptionEntity subscriptionEntity = subscriptionFactory.createFromInput(
            new SubscriptionFactoryDtoInput(request, customer, pricing, LocalDate.now()));

        final SubscriptionEntity subscriptionSaved = subscriptionMutationRepository.save(subscriptionEntity);

        return subscriptionFactory.createFromEntity(subscriptionSaved);
    }
}
