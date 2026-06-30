package com.jame.dev.gymApp.features.subscription.application.support.resolver;

import com.jame.dev.gymApp.features.subscription.api.response.RetryResponse;
import com.jame.dev.gymApp.features.subscription.application.usecases.mutation.CompletedCheckoutUseCase;
import com.jame.dev.gymApp.features.subscription.domain.event.CompletedCheckoutEvent;
import com.stripe.model.checkout.Session;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentSessionStatusResolver {

   private final CompletedCheckoutUseCase completedCheckoutUseCase;

   public ResponseEntity<RetryResponse> resolvePaymentStatus(final Session session, final String subject) {
      return switch (session.getPaymentStatus()) {
         case "paid" -> {
            log.info("paid");

            final CompletedCheckoutEvent checkoutEvent = CompletedCheckoutEvent.builder()
               .stripeSessionId(session.getId())
               .stripePaymentIntentId(session.getPaymentIntent())
               .stripeSubscriptionId(session.getSubscription())
               .customerEmail(subject)
               .build();

            completedCheckoutUseCase.execute(checkoutEvent);
            yield ResponseEntity.ok().build();
         }
         case "unpaid" -> {
            log.info("unpaid");
            final String status = session.getStatus();

            log.info("status: {}", status);

            yield switch (status) {
               case "expired" -> {
                  String recoveryUrl = session.getAfterExpiration().getRecovery().getUrl();
                  yield ResponseEntity.ok(new RetryResponse(recoveryUrl));
               }
               case "open" -> ResponseEntity.ok(new RetryResponse(session.getUrl()));
               default -> throw new IllegalStateException("Unexpected value: " + status);
            };
         }
         default -> throw new IllegalStateException("Unexpected value: " + session.getPaymentStatus());
      };
   }
}
