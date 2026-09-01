package com.jame.dev.gymApp.backup.usecases.query;

import com.jame.dev.gymApp.application.dto.PageDto;
import com.jame.dev.gymApp.features.backup.api.response.BackupResponse;
import com.jame.dev.gymApp.features.backup.application.service.GetBackupPageUseCaseService;
import com.jame.dev.gymApp.features.backup.application.support.factory.BackupFactory;
import com.jame.dev.gymApp.features.backup.domain.model.BackupDocument;
import com.jame.dev.gymApp.features.backup.domain.repository.BackupQueryRepository;
import com.jame.dev.gymApp.infrastructure.sort.SortPropertyResolver;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class GetBackupPageUseCaseServiceTest {

   @Mock
   private BackupFactory backupFactory;

   @Mock
   private BackupQueryRepository backupQueryRepository;

   @Mock
   private SortPropertyResolver backupSortPropertyResolver;

   @InjectMocks
   private GetBackupPageUseCaseService service;

   private final Pageable pageable = PageRequest.of(0, 5);

   @Test
   @DisplayName("Should return PageDto of BackupResponse when repository returns data")
   void getBackupPage_returnsPage() {
      Page<BackupDocument> rawPage = new PageImpl<>(List.of(mock(BackupDocument.class)));
      PageDto<BackupResponse> expectedPage = mock(PageDto.class);

      given(backupSortPropertyResolver.resolve(any(Pageable.class))).willReturn(pageable);
      given(backupQueryRepository.findAll(any(Pageable.class), nullable(String.class))).willReturn(rawPage);
      given(backupFactory.createPageFrom(any(Page.class))).willReturn(expectedPage);

      PageDto<BackupResponse> result = service.getBackupPage(pageable, "search");

      assertNotNull(result);
      assertSame(expectedPage, result);
      verify(backupSortPropertyResolver).resolve(any(Pageable.class));
      verify(backupQueryRepository).findAll(any(Pageable.class), nullable(String.class));
      verify(backupFactory).createPageFrom(any(Page.class));
      verifyNoMoreInteractions(backupSortPropertyResolver, backupQueryRepository, backupFactory);
   }

   @Test
   @DisplayName("Should resolve sort property and search parameter before querying repository")
   void getBackupPage_resolvesSortPropertyAndSearch() {
      Page<BackupDocument> rawPage = new PageImpl<>(List.of());
      PageDto<BackupResponse> expectedPage = mock(PageDto.class);

      given(backupSortPropertyResolver.resolve(any(Pageable.class))).willReturn(pageable);
      given(backupQueryRepository.findAll(any(Pageable.class), nullable(String.class))).willReturn(rawPage);
      given(backupFactory.createPageFrom(any(Page.class))).willReturn(expectedPage);

      service.getBackupPage(pageable, null);

      verify(backupSortPropertyResolver).resolve(any(Pageable.class));
      verify(backupQueryRepository).findAll(any(Pageable.class), nullable(String.class));
   }

   @Test
   @DisplayName("Should return whatever the factory builds even for an empty page")
   void getBackupPage_emptyPage() {
      Page<BackupDocument> rawPage = new PageImpl<>(List.of());
      PageDto<BackupResponse> expectedPage = mock(PageDto.class);

      given(backupSortPropertyResolver.resolve(any(Pageable.class))).willReturn(pageable);
      given(backupQueryRepository.findAll(any(Pageable.class), nullable(String.class))).willReturn(rawPage);
      given(backupFactory.createPageFrom(any(Page.class))).willReturn(expectedPage);

      PageDto<BackupResponse> result = service.getBackupPage(pageable, null);

      assertNotNull(result);
      assertSame(expectedPage, result);
      verifyNoMoreInteractions(backupSortPropertyResolver, backupQueryRepository, backupFactory);
   }
}
