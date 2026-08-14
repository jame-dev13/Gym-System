package com.jame.dev.gymApp.features.subscription.application.service;

import com.jame.dev.gymApp.features.subscription.api.request.SubscriptionCurrentRequest;
import com.jame.dev.gymApp.features.subscription.api.request.SubscriptionRequest;
import com.jame.dev.gymApp.features.subscription.api.response.SubscriptionCheckoutResponse;
import com.jame.dev.gymApp.features.subscription.application.contract.StripeCheckoutService;
import com.jame.dev.gymApp.features.subscription.domain.exception.PricingNotFoundException;
import com.jame.dev.gymApp.features.subscription.domain.model.PricingEntity;
import com.jame.dev.gymApp.features.subscription.infrastructure.persistence.PricingRepository;
import com.jame.dev.gymApp.features.subscription.infrastructure.stripe.service.StripeCheckoutGateway;
import com.jame.dev.gymApp.features.subscription.infrastructure.stripe.session.utils.SessionParams;
import com.jame.dev.gymApp.infrastructure.security.lock.CheckLockProcess;
import com.jame.dev.gymApp.infrastructure.security.lock.LockKeys;
import com.jame.dev.gymApp.infrastructure.security.principal.IdentityExtractorService;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@CheckLockProcess(keys = {LockKeys.PG_RESTORE})
@Transactional(readOnly = true)
public class StripeCheckoutApplicationService implements StripeCheckoutService {

   private final PricingRepository pricingRepository;
   private final StripeCheckoutGateway stripeCheckoutGateway;
   private final SessionParams sessionParams;
   private final IdentityExtractorService identityExtractorService;

   @Override
   public SubscriptionCheckoutResponse createCheckoutSessionFrom(SubscriptionRequest request) {
      final PricingEntity pricingEntity = pricingRepository.findByMemberShipEntity_Membership(request.membership())
         .orElseThrow(() -> new PricingNotFoundException("Pricing not found for membership: " + request.membership()));

      final SessionCreateParams params = sessionParams.getParams(
         pricingEntity,
         pricingEntity.getMemberShipEntity().getMembership(),
         request.customerEmail()
      );

      final Session session = stripeCheckoutGateway.createSession(params);

      return SubscriptionCheckoutResponse.builder()
         .sessionUrl(session.getUrl())
         .sessionId(session.getId())
         .paymentIndent(session.getPaymentIntent())
         .paymentSubscription(session.getSubscription())
         .build();
   }

   @Override
   public SubscriptionCheckoutResponse createCheckoutSessionFrom(Authentication authentication, SubscriptionCurrentRequest request) {
      final PricingEntity pricingEntity = pricingRepository.findByMemberShipEntity_Membership(request.membership())
         .orElseThrow(() -> new PricingNotFoundException("Pricing not found for membership: " + request.membership()));

      final SessionCreateParams params = sessionParams.getParams(
         pricingEntity,
         pricingEntity.getMemberShipEntity().getMembership(),
         identityExtractorService.extract(authentication)
      );

      final Session session = stripeCheckoutGateway.createSession(params);

      return SubscriptionCheckoutResponse.builder()
         .sessionUrl(session.getUrl())
         .sessionId(session.getId())
         .paymentIndent(session.getPaymentIntent())
         .paymentSubscription(session.getSubscription())
         .build();
   }
}
