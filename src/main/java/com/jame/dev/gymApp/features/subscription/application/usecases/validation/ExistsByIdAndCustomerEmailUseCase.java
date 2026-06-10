package com.jame.dev.gymApp.features.subscription.application.usecases.validation;

public interface ExistsByIdAndCustomerEmailUseCase {
    boolean existsByIdAndCustomerEmail(final long id, final String email);
}
