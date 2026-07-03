package com.jame.dev.gymApp.features.subscription.infrastructure.sort.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentSortProperty {

   ID("id", "id"),
   STATUS("status", "status"),
   PAYMENT_METHOD("method", "payment_method"),
   AMOUNT("amount", "amount"),
   CREATED_AT("createdAt", "created_at"),
   ;

   private final String apiProperty;
   private final String entityProperty;
}
