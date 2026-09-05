package com.jame.dev.gymApp.features.customer.infrastructure.audit.model;

public record CustomerInputStateModel(
   long customerId,
   boolean isSub,
   boolean hasAddressInfoSettled
) {
}
