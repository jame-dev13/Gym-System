package com.jame.dev.gymApp.features.subscription.infrastructure.stripe.session.utils;

import com.jame.dev.gymApp.features.subscription.domain.model.Membership;
import com.stripe.param.checkout.SessionCreateParams;

public class SessionInterval {

   public static SessionCreateParams.LineItem.PriceData.Recurring.Interval toStripeInterval(final Membership membership) {
      return switch (membership) {
         case BIWEEKLY -> SessionCreateParams.LineItem.PriceData.Recurring.Interval.WEEK;
         case MONTHLY, QUARTERLY -> SessionCreateParams.LineItem.PriceData.Recurring.Interval.MONTH;
         case ANNUAL -> SessionCreateParams.LineItem.PriceData.Recurring.Interval.YEAR;
      };
   }

   public static long toIntervalCount(final Membership membership) {
      return switch (membership) {
         case BIWEEKLY -> 2;
         case MONTHLY, ANNUAL -> 1;
         case QUARTERLY -> 3;
      };
   }
}
