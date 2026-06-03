package com.jame.dev.gymApp.features.customer.domain.model;

import lombok.Getter;

@Getter
public enum CustomerSortProperty {
   ID("id", "id"),
   EMAIL("userEmail", "user.email");

   private final String apiProperty;
   private final String entityProperty;

   CustomerSortProperty(String apiProperty, String entityProperty) {
      this.apiProperty = apiProperty;
      this.entityProperty = entityProperty;
   }
}
