package com.jame.dev.gymApp.features.subscription.domain.model;

import lombok.Getter;

@Getter
public enum SubscriptionSortProperty {
   ID("id", "id"),
   EMAIL("customerEmail", "customer.user.email"),
   PRICING("price", "pricing")
   ;

   private final String apiProperty;
   private final String entityProperty;

   SubscriptionSortProperty(String apiProperty, String entityProperty) {
      this.apiProperty = apiProperty;
      this.entityProperty = entityProperty;
   }
}
