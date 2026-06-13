package com.jame.dev.gymApp.features.audit.application.support.helper;

import com.jame.dev.gymApp.features.audit.domain.model.ChangesModelCustomer;
import com.jame.dev.gymApp.features.audit.domain.model.ChangesModelSubscription;
import com.jame.dev.gymApp.features.audit.domain.model.ChangesModelUser;
import com.jame.dev.gymApp.features.customer.api.request.CustomerRequest;
import com.jame.dev.gymApp.features.customer.api.response.CustomerResponse;
import com.jame.dev.gymApp.features.subscription.api.request.SubscriptionRequest;
import com.jame.dev.gymApp.features.subscription.api.response.SubscriptionResponse;
import com.jame.dev.gymApp.features.user.api.request.UserRequest;
import com.jame.dev.gymApp.features.user.api.response.UserResponse;
import org.jspecify.annotations.Nullable;

import java.util.Map;

public class ChangeModelResolverHelper {

    static Map<String, ChangesModelUser> buildChangesModelUser(long entityId, @Nullable UserRequest input, UserResponse response) {
      final var changesAfter = new ChangesModelUser(response.id(), response.name(), response.email(), response.authProvider(), response.roles());
      if (input == null) {
         return Map.of(
            "after", changesAfter
         );
      }
      return Map.of(
         "before", new ChangesModelUser(entityId, input.name(), input.email(), input.authProvider(), input.roles()),
         "after", changesAfter
      );
   }

    static Map<String, ChangesModelCustomer> buildChangesModelCustomer(long entityId, @Nullable CustomerRequest input, CustomerResponse response) {
      final var changesAfter = new ChangesModelCustomer(response.id(), response.user().email(), response.contact());
      if (input == null) return Map.of(
         "after", changesAfter
      );
      return Map.of(
         "before", new ChangesModelCustomer(entityId, input.email(), input.contact()),
         "after", changesAfter
      );
   }

    static Map<String, ChangesModelSubscription> buildChangesModelSubscription(long entityId, @Nullable SubscriptionRequest input, SubscriptionResponse response) {
      final var changesAfter = ChangesModelSubscription.builder()
         .id(response.id())
         .customerEmail(response.customerEmail())
         .price(response.price())
         .membership(response.membership())
         .periods(response.periods())
         .finished(response.finished())
         .build();

      if (input == null) return Map.of(
         "after", changesAfter
      );

      return Map.of(
         "before", ChangesModelSubscription.builder()
            .id(entityId)
            .customerEmail(input.customerEmail())
            .price(null)
            .membership(input.membership())
            .periods(null)
            .finished(null)
            .build(),
         "after", changesAfter
      );
   }
}
