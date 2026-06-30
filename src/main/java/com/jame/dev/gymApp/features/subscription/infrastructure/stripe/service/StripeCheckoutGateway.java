package com.jame.dev.gymApp.features.subscription.infrastructure.stripe.service;

import com.jame.dev.gymApp.features.subscription.domain.exception.StripeSessionException;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.stereotype.Service;

@Service
public class StripeCheckoutGateway {

   public Session createSession(final SessionCreateParams params) {
      try {
         return Session.create(params);
      } catch (StripeException e) {
         throw new StripeSessionException("Cannot create Stripe session now.", e);
      }
   }

   public Session retrieveSession(final String sessionId) {
      try {
         return Session.retrieve(sessionId);
      } catch (StripeException e) {
         throw new StripeSessionException("Cannot retrieve stripe session now.", e);
      }
   }

}
