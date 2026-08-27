package com.jame.dev.gymApp.features.subscription.application.service;

import com.jame.dev.gymApp.domain.exception.NotFoundException;
import com.jame.dev.gymApp.features.auth.infrastructure.security.identity.IdentityExtractorService;
import com.jame.dev.gymApp.features.subscription.api.request.SubscriptionCurrentRequest;
import com.jame.dev.gymApp.features.subscription.api.request.SubscriptionRequest;
import com.jame.dev.gymApp.features.subscription.api.response.SubscriptionCheckoutResponse;
import com.jame.dev.gymApp.features.subscription.application.contract.StripeCheckoutService;
import com.jame.dev.gymApp.features.subscription.domain.model.MembershipEntity;
import com.jame.dev.gymApp.features.subscription.infrastructure.persistence.MembershipRepository;
import com.jame.dev.gymApp.features.subscription.infrastructure.stripe.service.StripeCheckoutGateway;
import com.jame.dev.gymApp.features.subscription.infrastructure.stripe.session.utils.SessionParams;
import com.jame.dev.gymApp.infrastructure.security.lock.CheckLockProcess;
import com.jame.dev.gymApp.infrastructure.security.lock.LockKeys;
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

   private final MembershipRepository membershipRepository;
   private final StripeCheckoutGateway stripeCheckoutGateway;
   private final SessionParams sessionParams;
   private final IdentityExtractorService identityExtractorService;

   @Override
   public SubscriptionCheckoutResponse createCheckoutSessionFrom(SubscriptionRequest request) {
      final MembershipEntity membershipEntity = membershipRepository.findByMembership(request.membership())
         .orElseThrow(() -> new NotFoundException("Membership not found for membership given: " + request.membership()));

      final SessionCreateParams params = sessionParams.getParams(
         membershipEntity,
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
      final MembershipEntity membershipEntity = membershipRepository.findByMembership(request.membership())
         .orElseThrow(() -> new NotFoundException("Membership not found for membership given: " + request.membership()));

      final SessionCreateParams params = sessionParams.getParams(
         membershipEntity,
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
