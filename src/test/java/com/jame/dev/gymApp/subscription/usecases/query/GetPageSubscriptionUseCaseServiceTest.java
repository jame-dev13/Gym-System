package com.jame.dev.gymApp.subscription.usecases.query;

import com.jame.dev.gymApp.application.dto.PageDto;
import com.jame.dev.gymApp.features.subscription.api.response.SubscriptionResponse;
import com.jame.dev.gymApp.features.subscription.application.contract.SubscriptionFactory;
import com.jame.dev.gymApp.features.subscription.application.service.query.GetPageSubscriptionUseCaseService;
import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionEntity;
import com.jame.dev.gymApp.features.subscription.domain.repository.SubscriptionQueryRepository;
import com.jame.dev.gymApp.infrastructure.sort.SortPropertyResolver;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetPageSubscriptionUseCaseServiceTest {

    @Mock
    private SubscriptionQueryRepository subscriptionQueryRepository;

    @Mock
    private SubscriptionFactory subscriptionFactory;

    @Mock
    private SortPropertyResolver subSortAppResolver;

    @InjectMocks
    private GetPageSubscriptionUseCaseService service;

    @Test
    @DisplayName("Should return page of subscriptions")
    void getPage_returnsPageDto() {
        var pageable = PageRequest.of(0, 5);
        var entities = List.of(new SubscriptionEntity());
        var page = new PageImpl<>(entities);
        var pageDto = mock(PageDto.class);

        given(subSortAppResolver.resolve(any(Pageable.class))).willReturn(pageable);
        given(subscriptionQueryRepository.findAll(any(Specification.class), any(Pageable.class))).willReturn(page);
        given(subscriptionFactory.createPageFrom(any())).willReturn(pageDto);

        var result = service.getPage(pageable, "search");

        assertNotNull(result);
        verify(subSortAppResolver).resolve(any(Pageable.class));
        verify(subscriptionQueryRepository).findAll(any(Specification.class), any(Pageable.class));
        verify(subscriptionFactory).createPageFrom(any());
        verifyNoMoreInteractions(subscriptionQueryRepository, subscriptionFactory, subSortAppResolver);
    }
}
