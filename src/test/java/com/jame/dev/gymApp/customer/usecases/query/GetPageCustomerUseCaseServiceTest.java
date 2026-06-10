package com.jame.dev.gymApp.customer.usecases.query;

import com.jame.dev.gymApp.application.dto.PageDto;
import com.jame.dev.gymApp.features.customer.api.response.CustomerResponse;
import com.jame.dev.gymApp.features.customer.application.contract.CustomerFactory;
import com.jame.dev.gymApp.features.customer.application.service.query.GetPageCustomerUseCaseService;
import com.jame.dev.gymApp.features.customer.domain.repository.CustomerQueryRepository;
import com.jame.dev.gymApp.infrastructure.sort.SortPropertyResolver;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetPageCustomerUseCaseServiceTest {

    @Mock
    private CustomerQueryRepository customerQueryRepository;

    @Mock
    private CustomerFactory customerFactory;

    @Mock
    private SortPropertyResolver customerSortAppResolver;

    @InjectMocks
    private GetPageCustomerUseCaseService service;

    @Captor
    private ArgumentCaptor<Pageable> pageableCaptor;

    private final Pageable inputPageable = PageRequest.of(0, 10);

    @Test
    @DisplayName("Should return paginated customers with search")
    void getPage_withSearch_returnsPageDto() {
        var resolvedPageable = PageRequest.of(0, 10);
        var entityPage = mock(Page.class);
        var pageDto = new PageDto<CustomerResponse>(List.of(), 0, 10, 0, "id", "desc");

        given(customerSortAppResolver.resolve(any(Pageable.class))).willReturn(resolvedPageable);
        given(customerQueryRepository.findAll(any(Specification.class), any(Pageable.class))).willReturn(entityPage);
        given(customerFactory.createPageFrom(any())).willReturn(pageDto);

        var result = service.getPage(inputPageable, "test");

        assertNotNull(result);
        assertEquals(pageDto, result);

        verify(customerSortAppResolver).resolve(pageableCaptor.capture());
        assertEquals(inputPageable, pageableCaptor.getValue());
        verify(customerQueryRepository).findAll(any(Specification.class), eq(resolvedPageable));
        verify(customerFactory).createPageFrom(any());
        verifyNoMoreInteractions(customerQueryRepository, customerFactory, customerSortAppResolver);
    }
}
