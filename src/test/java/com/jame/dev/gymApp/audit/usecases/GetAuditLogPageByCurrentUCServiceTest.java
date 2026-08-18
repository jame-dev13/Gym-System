package com.jame.dev.gymApp.audit.usecases;

import com.jame.dev.gymApp.application.dto.PageDto;
import com.jame.dev.gymApp.features.audit.application.service.GetAuditLogPageByCurrentUCService;
import com.jame.dev.gymApp.features.audit.application.support.factory.AuditLogFactory;
import com.jame.dev.gymApp.features.audit.api.response.AuditLogResponse;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogDocument;
import com.jame.dev.gymApp.features.audit.infrastructure.specification.AuditLogSpecifications;
import com.jame.dev.gymApp.features.auth.domain.exception.AuthenticationNullException;
import com.jame.dev.gymApp.infrastructure.security.principal.IdentityExtractorService;
import com.jame.dev.gymApp.infrastructure.sort.SortPropertyResolver;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class GetAuditLogPageByCurrentUCServiceTest {

   @Mock
   private AuditLogSpecifications auditLogSpecifications;

   @Mock
   private AuditLogFactory auditLogFactory;

   @Mock
   private IdentityExtractorService identityExtractorService;

   @Mock
   private SortPropertyResolver auditLogSortAppResolver;

   @InjectMocks
   private GetAuditLogPageByCurrentUCService service;

   private final Authentication authentication = mock(Authentication.class);
   private final String actor = "jame.dev";
   private final String search = "UPDATE";
   private final Pageable pageable = PageRequest.of(
      0, 10, Sort.by(Sort.Order.desc("createdAt")));
   private final Pageable wrappedPageable = PageRequest.of(
      0, 10, Sort.by(Sort.Order.desc("created_at")));

   @Test
   @DisplayName("Should return page DTO delegating the wrapped pageable, actor and search")
   void getPage_whenDataExists_returnsPageDtoAndDelegatesArgs() {
      final Page<AuditLogDocument> page = new PageImpl<>(
         List.of(mock(AuditLogDocument.class)), pageable, 1L);
      final PageDto<AuditLogResponse> expectedDto = mock(PageDto.class);

      given(auditLogSortAppResolver.resolve(pageable)).willReturn(wrappedPageable);
      given(identityExtractorService.extract(authentication)).willReturn(actor);
      given(auditLogSpecifications.findAllByCurrentActor(
         any(Pageable.class), anyString(), nullable(String.class))).willReturn(page);
      given(auditLogFactory.createPageFrom(page)).willReturn(expectedDto);

      final PageDto<AuditLogResponse> result =
         service.getPage(authentication, pageable, search);

      assertSame(expectedDto, result);

      final ArgumentCaptor<Pageable> resolveCaptor = ArgumentCaptor.forClass(Pageable.class);
      verify(auditLogSortAppResolver).resolve(resolveCaptor.capture());
      assertSame(pageable, resolveCaptor.getValue());

      verify(identityExtractorService).extract(authentication);

      final ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
      final ArgumentCaptor<String> actorCaptor = ArgumentCaptor.forClass(String.class);
      final ArgumentCaptor<String> searchCaptor = ArgumentCaptor.forClass(String.class);
      verify(auditLogSpecifications).findAllByCurrentActor(
         pageableCaptor.capture(), actorCaptor.capture(), searchCaptor.capture());
      assertSame(wrappedPageable, pageableCaptor.getValue());
      assertEquals(actor, actorCaptor.getValue());
      assertEquals(search, searchCaptor.getValue());

      verify(auditLogFactory).createPageFrom(page);
      verifyNoMoreInteractions(
         auditLogSpecifications, auditLogFactory, identityExtractorService, auditLogSortAppResolver);
   }

   @Test
   @DisplayName("Should forward a null search to the specification")
   void getPage_whenSearchIsNull_forwardsNullSearch() {
      final Page<AuditLogDocument> page = new PageImpl<>(List.of(), pageable, 0L);
      final PageDto<AuditLogResponse> expectedDto = mock(PageDto.class);

      given(auditLogSortAppResolver.resolve(pageable)).willReturn(wrappedPageable);
      given(identityExtractorService.extract(authentication)).willReturn(actor);
      given(auditLogSpecifications.findAllByCurrentActor(
         any(Pageable.class), anyString(), nullable(String.class))).willReturn(page);
      given(auditLogFactory.createPageFrom(page)).willReturn(expectedDto);

      service.getPage(authentication, pageable, null);

      final ArgumentCaptor<String> searchCaptor = ArgumentCaptor.forClass(String.class);
      verify(auditLogSpecifications).findAllByCurrentActor(
         any(Pageable.class), anyString(), searchCaptor.capture());
      assertNull(searchCaptor.getValue());
   }

   @Test
   @DisplayName("Should forward a blank search as-is to the specification")
   void getPage_whenSearchIsBlank_forwardsBlankSearch() {
      final Page<AuditLogDocument> page = new PageImpl<>(List.of(), pageable, 0L);
      final PageDto<AuditLogResponse> expectedDto = mock(PageDto.class);

      given(auditLogSortAppResolver.resolve(pageable)).willReturn(wrappedPageable);
      given(identityExtractorService.extract(authentication)).willReturn(actor);
      given(auditLogSpecifications.findAllByCurrentActor(
         any(Pageable.class), anyString(), nullable(String.class))).willReturn(page);
      given(auditLogFactory.createPageFrom(page)).willReturn(expectedDto);

      service.getPage(authentication, pageable, "   ");

      final ArgumentCaptor<String> searchCaptor = ArgumentCaptor.forClass(String.class);
      verify(auditLogSpecifications).findAllByCurrentActor(
         any(Pageable.class), anyString(), searchCaptor.capture());
      assertEquals("   ", searchCaptor.getValue());
   }

   @Test
   @DisplayName("Should return an empty page DTO when the specification returns no data")
   void getPage_whenResultEmpty_returnsEmptyPageDto() {
      final Page<AuditLogDocument> emptyPage = new PageImpl<>(List.of(), pageable, 0L);
      final PageDto<AuditLogResponse> emptyDto = new PageDto<>(
         List.of(), 0, 10, 0L, "created_at: DESC", "DESC");

      given(auditLogSortAppResolver.resolve(pageable)).willReturn(wrappedPageable);
      given(identityExtractorService.extract(authentication)).willReturn(actor);
      given(auditLogSpecifications.findAllByCurrentActor(
         any(Pageable.class), anyString(), nullable(String.class))).willReturn(emptyPage);
      given(auditLogFactory.createPageFrom(emptyPage)).willReturn(emptyDto);

      final PageDto<AuditLogResponse> result =
         service.getPage(authentication, pageable, search);

      assertTrue(result.content().isEmpty());
      assertEquals(0L, result.totalElements());
   }

   @Test
   @DisplayName("Should propagate extractor failure when authentication is null")
   void getPage_whenExtractorFails_propagatesException() {
      given(auditLogSortAppResolver.resolve(pageable)).willReturn(wrappedPageable);
      given(identityExtractorService.extract(authentication))
         .willThrow(new AuthenticationNullException("Null authentication"));

      assertThrows(AuthenticationNullException.class,
         () -> service.getPage(authentication, pageable, search));

      verify(auditLogSortAppResolver).resolve(pageable);
      verify(identityExtractorService).extract(authentication);
      verifyNoInteractions(auditLogSpecifications, auditLogFactory);
   }

   @Test
   @DisplayName("Should propagate specification failure")
   void getPage_whenSpecificationFails_propagatesException() {
      given(auditLogSortAppResolver.resolve(pageable)).willReturn(wrappedPageable);
      given(identityExtractorService.extract(authentication)).willReturn(actor);
      given(auditLogSpecifications.findAllByCurrentActor(
         any(Pageable.class), anyString(), nullable(String.class)))
         .willThrow(new IllegalStateException("mongo down"));

      assertThrows(IllegalStateException.class,
         () -> service.getPage(authentication, pageable, search));

      verify(auditLogSortAppResolver).resolve(pageable);
      verify(identityExtractorService).extract(authentication);
      verifyNoInteractions(auditLogFactory);
   }

   @Test
   @DisplayName("Should propagate factory failure")
   void getPage_whenFactoryFails_propagatesException() {
      final Page<AuditLogDocument> page = new PageImpl<>(
         List.of(mock(AuditLogDocument.class)), pageable, 1L);

      given(auditLogSortAppResolver.resolve(pageable)).willReturn(wrappedPageable);
      given(identityExtractorService.extract(authentication)).willReturn(actor);
      given(auditLogSpecifications.findAllByCurrentActor(
         any(Pageable.class), anyString(), nullable(String.class))).willReturn(page);
      given(auditLogFactory.createPageFrom(page))
         .willThrow(new IllegalStateException("mapping failure"));

      assertThrows(IllegalStateException.class,
         () -> service.getPage(authentication, pageable, search));
   }
}