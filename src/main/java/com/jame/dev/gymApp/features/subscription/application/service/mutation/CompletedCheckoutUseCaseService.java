package com.jame.dev.gymApp.features.subscription.application.service.mutation;

import com.jame.dev.gymApp.features.customer.domain.exception.CustomerNotFoundException;
import com.jame.dev.gymApp.features.customer.domain.model.CustomerEntity;
import com.jame.dev.gymApp.features.customer.domain.repository.CustomerQueryRepository;
import com.jame.dev.gymApp.features.subscription.api.request.SubscriptionRequest;
import com.jame.dev.gymApp.features.subscription.application.contract.SubscriptionFactory;
import com.jame.dev.gymApp.features.subscription.application.dto.SubscriptionFactoryDtoInput;
import com.jame.dev.gymApp.features.subscription.application.usecases.mutation.CompletedCheckoutUseCase;
import com.jame.dev.gymApp.features.subscription.domain.event.CompletedCheckoutEvent;
import com.jame.dev.gymApp.features.subscription.domain.exception.PricingNotFoundException;
import com.jame.dev.gymApp.features.subscription.domain.model.*;
import com.jame.dev.gymApp.features.subscription.domain.repository.PaymentMutationRepository;
import com.jame.dev.gymApp.features.subscription.domain.repository.SubscriptionMutationRepository;
import com.jame.dev.gymApp.features.subscription.infrastructure.persistence.PricingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static com.jame.dev.gymApp.application.model.CacheValues.SUBSCRIPTIONS;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompletedCheckoutUseCaseService implements CompletedCheckoutUseCase {

    private final CustomerQueryRepository customerQueryRepository;
    private final PricingRepository pricingRepository;
    private final SubscriptionFactory subscriptionFactory;
    private final SubscriptionMutationRepository subscriptionMutationRepository;
    private final PaymentMutationRepository paymentMutationRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    @CacheEvict(value = SUBSCRIPTIONS, allEntries = true)
    public void execute(CompletedCheckoutEvent event) {
        final CustomerEntity customer = customerQueryRepository.findByUserEmail(event.customerEmail())
            .orElseThrow(() -> new CustomerNotFoundException("Customer not found: " + event.customerEmail()));

        final PricingEntity pricing = pricingRepository.findById(event.pricingId())
            .orElseThrow(() -> new PricingNotFoundException("Pricing not found: " + event.pricingId()));

        final SubscriptionEntity subscription = subscriptionFactory.createFromInput(
            new SubscriptionFactoryDtoInput(
                new SubscriptionRequest(event.customerEmail(), pricing.getMemberShipEntity().getMembership()),
                customer, pricing, LocalDate.now()
            )
        );
        final SubscriptionEntity savedSubscription = subscriptionMutationRepository.save(subscription);

        final PaymentEntity payment = PaymentEntity.builder()
            .stripeSessionId(event.stripeSessionId())
            .stripePaymentIntentId(event.stripePaymentIntentId())
            .stripeSubscriptionId(event.stripeSubscriptionId())
            .amount(pricing.getPrice())
            .currency("mx")
            .status(PaymentStatus.COMPLETED)
            .paymentMethod(PaymentMethod.ELECTRONIC)
            .subscription(savedSubscription)
            .customer(customer)
            .build();
        paymentMutationRepository.save(payment);

        //eventPublisher.publishEvent(event);

        log.info("Checkout completed: session={}, subscription={}, customer={}",
            event.stripeSessionId(), savedSubscription.getId(), event.customerEmail());
    }
}
