package com.jame.dev.gymApp.user.usecases.query;

import com.jame.dev.gymApp.application.dto.PageDto;
import com.jame.dev.gymApp.features.user.api.response.UserMinimalInfoResponse;
import com.jame.dev.gymApp.features.user.api.response.UserResponse;
import com.jame.dev.gymApp.features.user.application.contract.UserFactory;
import com.jame.dev.gymApp.features.user.application.service.query.GetPageUserUseCaseService;
import com.jame.dev.gymApp.features.user.domain.repository.UserQueryRepository;
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
class GetPageUserUseCaseServiceTest {

    @Mock
    private UserQueryRepository userQueryRepository;

    @Mock
    private UserFactory userFactory;

    @Mock
    private SortPropertyResolver userSortApplicationResolver;

    @InjectMocks
    private GetPageUserUseCaseService service;

    @Captor
    private ArgumentCaptor<Pageable> pageableCaptor;

    private final Pageable inputPageable = PageRequest.of(0, 10);

    @Test
    @DisplayName("Should return paginated active users with search")
    void getPage_withSearch_returnsPageDto() {
        var resolvedPageable = PageRequest.of(0, 10);
        var entityPage = mock(Page.class);
        var pageDto = new PageDto<UserResponse>(List.of(), 0, 10, 0, "name", "asc");

        given(userSortApplicationResolver.resolve(any(Pageable.class))).willReturn(resolvedPageable);
        given(userQueryRepository.findAll(any(Pageable.class), any(Specification.class))).willReturn(entityPage);
        given(userFactory.createPageFrom(any())).willReturn(pageDto);

        var result = service.getPage(inputPageable, "test");

        assertNotNull(result);
        assertEquals(pageDto, result);

        verify(userSortApplicationResolver).resolve(pageableCaptor.capture());
        assertEquals(inputPageable, pageableCaptor.getValue());
        verify(userQueryRepository).findAll(eq(resolvedPageable), any());
        verify(userFactory).createPageFrom(any());
        verifyNoMoreInteractions(userQueryRepository, userFactory, userSortApplicationResolver);
    }

    @Test
    @DisplayName("Should return paginated inactive users with search")
    void getInactivePage_withSearch_returnsMinimalInfoPageDto() {
        var resolvedPageable = PageRequest.of(0, 10);
        var minimalPage = mock(Page.class);
        var pageDto = new PageDto<UserMinimalInfoResponse>(List.of(), 0, 10, 0, "name", "asc");

        given(userSortApplicationResolver.resolve(any(Pageable.class))).willReturn(resolvedPageable);
        given(userQueryRepository.findAllDeactivated(any(Pageable.class), anyString())).willReturn(minimalPage);
        given(userFactory.createMinimalInfoPage(any())).willReturn(pageDto);

        var result = service.getInactivePage(inputPageable, "test");

        assertNotNull(result);
        assertEquals(pageDto, result);

        verify(userSortApplicationResolver).resolve(pageableCaptor.capture());
        assertEquals(inputPageable, pageableCaptor.getValue());
        verify(userQueryRepository).findAllDeactivated(eq(resolvedPageable), anyString());
        verify(userFactory).createMinimalInfoPage(any());
        verifyNoMoreInteractions(userQueryRepository, userFactory, userSortApplicationResolver);
    }
}
