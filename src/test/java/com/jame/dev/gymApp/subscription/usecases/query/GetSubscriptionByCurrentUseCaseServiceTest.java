package com.jame.dev.gymApp.subscription.usecases.query;

import com.jame.dev.gymApp.domain.exception.NotFoundException;
import com.jame.dev.gymApp.features.auth.domain.model.AuthPrincipal;
import com.jame.dev.gymApp.features.auth.domain.model.UserPrincipal;
import com.jame.dev.gymApp.features.subscription.api.response.SubscriptionResponse;
import com.jame.dev.gymApp.features.subscription.application.service.query.GetSubscriptionByCurrentUseCaseService;
import com.jame.dev.gymApp.features.subscription.application.support.mapper.SubscriptionMapper;
import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionEntity;
import com.jame.dev.gymApp.features.subscription.domain.repository.SubscriptionQueryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class GetSubscriptionByCurrentUseCaseServiceTest {

    @Mock
    private SubscriptionQueryRepository subscriptionQueryRepository;

    @Mock
    private SubscriptionMapper subscriptionMapper;

    @InjectMocks
    private GetSubscriptionByCurrentUseCaseService service;

    private final String customerEmail = "customer@mail.com";
    private final AuthPrincipal principal = UserPrincipal.builder()
        .id(1L)
        .username(customerEmail)
        .build();

    @Test
    @DisplayName("Should return SubscriptionResponse when current subscription exists")
    void getCurrent_whenSubscriptionExists_returnsSubscriptionResponse() {
        var entity = mock(SubscriptionEntity.class);
        var response = mock(SubscriptionResponse.class);

        given(subscriptionQueryRepository.findByCustomerEmail(anyString())).willReturn(Optional.of(entity));
        given(subscriptionMapper.toDto(any(SubscriptionEntity.class))).willReturn(response);

        var result = service.getCurrent(principal);

        assertNotNull(result);
        verify(subscriptionQueryRepository).findByCustomerEmail(anyString());
        verify(subscriptionMapper).toDto(any(SubscriptionEntity.class));
        verifyNoMoreInteractions(subscriptionQueryRepository, subscriptionMapper);
    }

    @Test
    @DisplayName("Should throw NotFoundException when current subscription not found")
    void getCurrent_whenSubscriptionNotFound_throwsException() {
        given(subscriptionQueryRepository.findByCustomerEmail(anyString())).willReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.getCurrent(principal));

        verify(subscriptionQueryRepository).findByCustomerEmail(anyString());
        verifyNoInteractions(subscriptionMapper);
    }
}
