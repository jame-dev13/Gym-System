package com.jame.dev.gymApp.subscription.usecases.mutation;

import com.jame.dev.gymApp.features.subscription.application.service.mutation.SoftDeleteSubscriptionByIdUseCaseService;
import com.jame.dev.gymApp.features.subscription.domain.repository.SubscriptionMutationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SoftDeleteSubscriptionByIdUseCaseServiceTest {

    @Mock
    private SubscriptionMutationRepository subscriptionMutationRepository;

    @InjectMocks
    private SoftDeleteSubscriptionByIdUseCaseService service;

    @Test
    @DisplayName("Should call repository deleteById")
    void softDeleteById_callsRepositoryDeleteById() {
        service.softDeleteById(1L);

        verify(subscriptionMutationRepository).deleteById(anyLong());
        verifyNoMoreInteractions(subscriptionMutationRepository);
    }
}
