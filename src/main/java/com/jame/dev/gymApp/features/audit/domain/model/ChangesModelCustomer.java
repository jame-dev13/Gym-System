package com.jame.dev.gymApp.features.audit.domain.model;

import org.jspecify.annotations.Nullable;

public record ChangesModelCustomer(
   @Nullable Long id,
   String userEmail,
   String phoneContact
) {
}
