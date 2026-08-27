package com.jame.dev.gymApp.features.subscription.infrastructure.stripe.session.utils;

import com.jame.dev.gymApp.features.subscription.domain.model.MembershipEntity;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
public class SessionParams {

   @Value("${stripe.success-url}")
   private String successUrl;

   @Value("${stripe.cancel-url}")
   private String cancelUrl;

   public SessionCreateParams getParams(MembershipEntity membershipEntity, String customerEmail) {
      final long unitAmount = membershipEntity.getPrice().multiply(BigDecimal.valueOf(100)).longValue();
      final var membership = membershipEntity.getMembership();
      final SessionCreateParams.LineItem.PriceData.ProductData productData =
         SessionCreateParams.LineItem.PriceData.ProductData.builder()
            .setName("Gym membership - " + membership.name())
            .build();

      final SessionCreateParams.LineItem.PriceData.Recurring recurring =
         SessionCreateParams.LineItem.PriceData.Recurring.builder()
            .setInterval(SessionInterval.toStripeInterval(membership))
            .setIntervalCount(SessionInterval.toIntervalCount(membership))
            .build();

      final SessionCreateParams.LineItem.PriceData priceData =
         SessionCreateParams.LineItem.PriceData.builder()
            .setCurrency("mxn")
            .setUnitAmount(unitAmount)
            .setProductData(productData)
            .setRecurring(recurring)
            .build();

      final SessionCreateParams.LineItem lineItem =
         SessionCreateParams.LineItem.builder()
            .setQuantity(1L)
            .setPriceData(priceData)
            .build();

      return SessionCreateParams.builder()
         .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
         .setSuccessUrl(successUrl)
         .setCancelUrl(cancelUrl)
         .addLineItem(lineItem)
         .setExpiresAt(System.currentTimeMillis() / 1000L + (30 * 60))
         .setAfterExpiration(
            SessionCreateParams.AfterExpiration.builder()
               .setRecovery(SessionCreateParams.AfterExpiration.Recovery.builder()
                  .setEnabled(true)
                  .build())
               .build()
         )
         .setCustomerEmail(customerEmail)
         .putMetadata("pricingId", membershipEntity.getId().toString())
         .putMetadata("customerEmail", customerEmail)
         .build();
   }
}
