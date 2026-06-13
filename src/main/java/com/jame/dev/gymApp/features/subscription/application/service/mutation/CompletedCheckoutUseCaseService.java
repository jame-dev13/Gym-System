package com.jame.dev.gymApp.features.subscription.application.service.mutation;

import com.jame.dev.gymApp.features.subscription.application.support.factory.PaymentFactory;
import com.jame.dev.gymApp.features.subscription.application.usecases.mutation.CompletedCheckoutUseCase;
import com.jame.dev.gymApp.features.subscription.domain.event.CompletedCheckoutEvent;
import com.jame.dev.gymApp.features.subscription.domain.event.StripeSessionPaymentEvent;
import com.jame.dev.gymApp.features.subscription.domain.exception.SubscriptionNotFoundException;
import com.jame.dev.gymApp.features.subscription.domain.model.PaymentEntity;
import com.jame.dev.gymApp.features.subscription.domain.model.PaymentStatus;
import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionEntity;
import com.jame.dev.gymApp.features.subscription.domain.repository.PaymentMutationRepository;
import com.jame.dev.gymApp.features.subscription.domain.repository.SubscriptionQueryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.jame.dev.gymApp.application.model.CacheValues.SUBSCRIPTIONS;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompletedCheckoutUseCaseService implements CompletedCheckoutUseCase {
    private final SubscriptionQueryRepository subscriptionQueryRepository;
    private final PaymentMutationRepository paymentMutationRepository;
    private final PaymentFactory paymentFactory;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    @CacheEvict(value = SUBSCRIPTIONS, allEntries = true)
    public void execute(CompletedCheckoutEvent event) {
        final SubscriptionEntity subscription = subscriptionQueryRepository.findByCustomerEmail(event.customerEmail())
           .orElseThrow(() -> new SubscriptionNotFoundException("Subscription Not found for: " + event.customerEmail()));

        final StripeSessionPaymentEvent paymentEvent = StripeSessionPaymentEvent.builder()
           .sessionId(event.stripeSessionId())
           .intentId(event.stripePaymentIntentId())
           .subscriptionId(event.stripeSubscriptionId())
           .subscriptionEntity(subscription)
           .isPhysicSession(false)
           .build();

        final PaymentEntity payment = paymentFactory.from(paymentEvent);

        final PaymentEntity paymentEntity = paymentMutationRepository.save(payment);

        subscription.setPaid(paymentEntity.getStatus() == PaymentStatus.COMPLETED);
        //eventPublisher.publishEvent(event);

        log.info("Checkout completed: session={}, subscription={}, customer={}",
            event.stripeSessionId(), subscription.getId(), event.customerEmail());
    }
}
