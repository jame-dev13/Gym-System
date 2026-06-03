package com.jame.dev.gymApp.features.user.domain.model;

import lombok.Getter;

@Getter
public enum UserSortProperty {

   ID("id", "id"),
   EMAIL("email", "email"),
   ROLE("role", "roles.role");

   private final String apiProperty;
   private final String entityProperty;

   UserSortProperty(String apiProperty, String entityProperty) {
      this.apiProperty = apiProperty;
      this.entityProperty = entityProperty;
   }
}
