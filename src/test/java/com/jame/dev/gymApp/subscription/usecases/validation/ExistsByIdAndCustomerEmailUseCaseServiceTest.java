package com.jame.dev.gymApp.subscription.usecases.validation;

import com.jame.dev.gymApp.features.subscription.application.service.validation.ExistsByIdAndCustomerEmailUseCaseService;
import com.jame.dev.gymApp.features.subscription.domain.repository.SubscriptionValidationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExistsByIdAndCustomerEmailUseCaseServiceTest {

    @Mock
    private SubscriptionValidationRepository subscriptionValidationRepository;

    @InjectMocks
    private ExistsByIdAndCustomerEmailUseCaseService service;

    @Test
    @DisplayName("Should return true when subscription exists for the given id and email")
    void existsByIdAndCustomerEmail_returnsTrue() {
        given(subscriptionValidationRepository.existsByIdAndCustomerEmail(anyLong(), anyString())).willReturn(true);

        var result = service.existsByIdAndCustomerEmail(1L, "test@mail.com");

        assertTrue(result);
        verify(subscriptionValidationRepository).existsByIdAndCustomerEmail(anyLong(), anyString());
        verifyNoMoreInteractions(subscriptionValidationRepository);
    }

    @Test
    @DisplayName("Should return false when subscription does not exist for the given id and email")
    void existsByIdAndCustomerEmail_returnsFalse() {
        given(subscriptionValidationRepository.existsByIdAndCustomerEmail(anyLong(), anyString())).willReturn(false);

        var result = service.existsByIdAndCustomerEmail(1L, "unknown@mail.com");

        assertFalse(result);
        verify(subscriptionValidationRepository).existsByIdAndCustomerEmail(anyLong(), anyString());
        verifyNoMoreInteractions(subscriptionValidationRepository);
    }
}
