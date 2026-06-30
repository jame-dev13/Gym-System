package com.jame.dev.gymApp.features.subscription.infrastructure.stripe.handler;

import com.jame.dev.gymApp.features.subscription.domain.exception.PayloadException;
import com.stripe.exception.EventDataObjectDeserializationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EventSessionDeserializerHandler {

   public static Session handleDeserializeEvent(final Event event) {
      final EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();
      final StripeObject stripeObject;
      try {
         stripeObject = deserializer.deserializeUnsafe();
      } catch (EventDataObjectDeserializationException e) {
         log.warn(e.getMessage());
         throw new PayloadException("Cannot deserialize stripe object");
      }

      if(!(stripeObject instanceof final Session session)){
         throw new PayloadException("Stripe object malformed.");
      }
      return session;
   }
}
