package com.jame.dev.gymApp.audit.controller;

import com.jame.dev.gymApp.application.dto.PageDto;
import com.jame.dev.gymApp.features.audit.api.AuditLogCurrentController;
import com.jame.dev.gymApp.features.audit.api.response.AuditLogResponse;
import com.jame.dev.gymApp.features.audit.application.dto.AuditLogActor;
import com.jame.dev.gymApp.features.audit.application.dto.AuditLogEntity;
import com.jame.dev.gymApp.features.audit.application.dto.AuditLogMetadata;
import com.jame.dev.gymApp.features.audit.application.usecases.GetAuditLogPageByCurrentUseCase;
import com.jame.dev.gymApp.features.audit.domain.exception.AuditLogNotFoundException;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogAction;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogCrudPayload;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogEntityType;
import com.jame.dev.gymApp.features.auth.domain.exception.AuthenticationNullException;
import com.jame.dev.gymApp.features.auth.infrastructure.security.CustomAuthorizationFilter;
import com.jame.dev.gymApp.infrastructure.page.mapper.PageResponseMapper;
import com.jame.dev.gymApp.presentation.exception.ApiErrorResponseFactory;
import com.jame.dev.gymApp.presentation.exception.GlobalExceptionHandler;
import config.TestConfig;
import config.TestValidationConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.validation.autoconfigure.ValidationAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
   controllers = AuditLogCurrentController.class,
   excludeFilters = {
      @ComponentScan.Filter(
         type = FilterType.ASSIGNABLE_TYPE,
         classes = CustomAuthorizationFilter.class
      )}
)
@AutoConfigureMockMvc(addFilters = false)
@Import({
   GlobalExceptionHandler.class,
   TestValidationConfig.class,
   TestConfig.class})
@ImportAutoConfiguration({ValidationAutoConfiguration.class})
class AuditLogCurrentControllerTest {

   @Autowired
   private MockMvc mockMvc;

   @Autowired
   private ApiErrorResponseFactory responseFactory;

   @MockitoBean
   private GetAuditLogPageByCurrentUseCase getAuditLogPageByCurrentUseCase;

   @MockitoBean
   private PageResponseMapper pageResponseMapper;

   private final String URI_TEMPLATE = "/app/v1/logs/current";

   private final AuditLogResponse auditLogResponse = new AuditLogResponse(
      new AuditLogEntity(AuditLogEntityType.CUSTOMER, 1L),
      AuditLogAction.UPDATE,
      new AuditLogActor(1L, "jame.dev"),
      new AuditLogCrudPayload(Map.of("name", "old name"), Map.of("name", "new name")),
      true,
      new AuditLogMetadata("127.0.0.1", "curl/8.0"),
      Instant.parse("2026-01-01T10:00:00Z")
   );

   private final PageDto<AuditLogResponse> pageDto = new PageDto<>(
      List.of(auditLogResponse), 0, 10, 1L, "createdAt", "DESC");

   private final PageDto<AuditLogResponse> emptyPageDto = new PageDto<>(
      List.of(), 0, 10, 0L, "", "DESC");

   private final Page<AuditLogResponse> page = new PageImpl<>(
      List.of(auditLogResponse), PageRequest.of(0, 10), 1L);

   private final Page<AuditLogResponse> emptyPage = new PageImpl<>(
      List.of(), PageRequest.of(0, 10), 0L);

   @Nested
   @DisplayName("GET /app/v1/logs/current")
   class AuditLogCurrentControllerGetTests {

      @Test
      @DisplayName("GET[200] OK: Returns current actor audit logs page")
      void getCurrentLogTrace_whenDataExists_returnsPage() throws Exception {
         given(getAuditLogPageByCurrentUseCase.getPage(
            any(), any(Pageable.class), nullable(String.class))).willReturn(pageDto);
         given(pageResponseMapper.from(any(PageDto.class), any(Pageable.class))).willReturn(page);

         mockMvc.perform(get(URI_TEMPLATE).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content[0].action").value("UPDATE"))
            .andExpect(jsonPath("$.content[0].entity.type").value("CUSTOMER"))
            .andExpect(jsonPath("$.content[0].actor.username").value("jame.dev"))
            .andExpect(jsonPath("$.content[0].success").value(true))
            .andExpect(jsonPath("$.totalElements").value(1));

         verify(getAuditLogPageByCurrentUseCase, times(1))
            .getPage(any(), any(Pageable.class), nullable(String.class));
         verify(pageResponseMapper, times(1)).from(any(PageDto.class), any(Pageable.class));
         verifyNoMoreInteractions(getAuditLogPageByCurrentUseCase, pageResponseMapper);
      }

      @Test
      @DisplayName("GET[200] OK: Returns empty page when no audit logs for current actor")
      void getCurrentLogTrace_whenNoData_returnsEmptyPage() throws Exception {
         given(getAuditLogPageByCurrentUseCase.getPage(
            any(), any(Pageable.class), nullable(String.class))).willReturn(emptyPageDto);
         given(pageResponseMapper.from(any(PageDto.class), any(Pageable.class))).willReturn(emptyPage);

         mockMvc.perform(get(URI_TEMPLATE).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isEmpty())
            .andExpect(jsonPath("$.totalElements").value(0));

         verify(getAuditLogPageByCurrentUseCase, times(1))
            .getPage(any(), any(Pageable.class), nullable(String.class));
         verify(pageResponseMapper, times(1)).from(any(PageDto.class), any(Pageable.class));
      }

      @Test
      @DisplayName("GET[200] OK: Forwards the search query param to the use case")
      void getCurrentLogTrace_withSearchParam_forwardsSearch() throws Exception {
         given(getAuditLogPageByCurrentUseCase.getPage(
            any(), any(Pageable.class), nullable(String.class))).willReturn(pageDto);
         given(pageResponseMapper.from(any(PageDto.class), any(Pageable.class))).willReturn(page);

         mockMvc.perform(get(URI_TEMPLATE)
               .param("search", "UPDATE")
               .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

         final var searchCaptor = ArgumentCaptor.forClass(String.class);
         verify(getAuditLogPageByCurrentUseCase, times(1))
            .getPage(any(), any(Pageable.class), searchCaptor.capture());
         assertEquals("UPDATE", searchCaptor.getValue());
      }

      @Test
      @DisplayName("GET[200] OK: Forwards a null search when the param is absent")
      void getCurrentLogTrace_withoutSearchParam_forwardsNull() throws Exception {
         given(getAuditLogPageByCurrentUseCase.getPage(
            any(), any(Pageable.class), nullable(String.class))).willReturn(pageDto);
         given(pageResponseMapper.from(any(PageDto.class), any(Pageable.class))).willReturn(page);

         mockMvc.perform(get(URI_TEMPLATE).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

         final var searchCaptor = ArgumentCaptor.forClass(String.class);
         verify(getAuditLogPageByCurrentUseCase, times(1))
            .getPage(any(), any(Pageable.class), searchCaptor.capture());
         assertNull(searchCaptor.getValue());
      }

      @Test
      @DisplayName("GET[200] OK: Forwards pagination and sort params to the use case and mapper")
      void getCurrentLogTrace_withPagination_forwardsPageable() throws Exception {
         given(getAuditLogPageByCurrentUseCase.getPage(
            any(), any(Pageable.class), nullable(String.class))).willReturn(pageDto);
         given(pageResponseMapper.from(any(PageDto.class), any(Pageable.class))).willReturn(page);

         mockMvc.perform(get(URI_TEMPLATE)
               .param("page", "1")
               .param("size", "5")
               .param("sort", "createdAt,desc")
               .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

         final var pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
         verify(getAuditLogPageByCurrentUseCase, times(1))
            .getPage(any(), pageableCaptor.capture(), nullable(String.class));

         assertEquals(1, pageableCaptor.getValue().getPageNumber());
         assertEquals(5, pageableCaptor.getValue().getPageSize());
         assertEquals(Sort.Direction.DESC,
            pageableCaptor.getValue().getSort().getOrderFor("createdAt").getDirection());

         final var mapperPageableCaptor = ArgumentCaptor.forClass(Pageable.class);
         verify(pageResponseMapper, times(1)).from(any(PageDto.class), mapperPageableCaptor.capture());
         assertSame(pageableCaptor.getValue(), mapperPageableCaptor.getValue());
      }

      @Test
      @DisplayName("GET[404] Not Found: No audit logs related to the authenticated user")
      void getCurrentLogTrace_whenLogNotFound_returns404() throws Exception {
         given(getAuditLogPageByCurrentUseCase.getPage(
            any(), any(Pageable.class), nullable(String.class)))
            .willThrow(AuditLogNotFoundException.class);

         mockMvc.perform(get(URI_TEMPLATE).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.code").value("NOT_FOUND_OPERATION"));

         verify(getAuditLogPageByCurrentUseCase, times(1))
            .getPage(any(), any(Pageable.class), nullable(String.class));
         verifyNoInteractions(pageResponseMapper);
      }

      @Test
      @DisplayName("GET[401] Unauthorized: No authenticated user in session")
      void getCurrentLogTrace_whenAuthenticationNull_returns401() throws Exception {
         given(getAuditLogPageByCurrentUseCase.getPage(
            any(), any(Pageable.class), nullable(String.class)))
            .willThrow(AuthenticationNullException.class);

         mockMvc.perform(get(URI_TEMPLATE).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.status").value(401))
            .andExpect(jsonPath("$.code").value("AUTHENTICATION_OPERATION"));

         verify(getAuditLogPageByCurrentUseCase, times(1))
            .getPage(any(), any(Pageable.class), nullable(String.class));
         verifyNoInteractions(pageResponseMapper);
      }

      @Test
      @DisplayName("GET[500] Internal Server Error: Unexpected failure inside the use case")
      void getCurrentLogTrace_whenUnexpectedError_returns500() throws Exception {
         given(getAuditLogPageByCurrentUseCase.getPage(
            any(), any(Pageable.class), nullable(String.class)))
            .willThrow(new NullPointerException("unexpected"));

         mockMvc.perform(get(URI_TEMPLATE).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.status").value(500))
            .andExpect(jsonPath("$.code").value("INTERNAL_OPERATION"));

         verify(getAuditLogPageByCurrentUseCase, times(1))
            .getPage(any(), any(Pageable.class), nullable(String.class));
         verifyNoInteractions(pageResponseMapper);
      }

      @ParameterizedTest
      @CsvSource(value = {"0|letter", "NULL|10"}, delimiter = '|')
      @DisplayName("GET[200] OK: Invalid pagination params fall back to Spring defaults")
      void getCurrentLogTrace_invalidPagination_fallsBackToDefaults(String pageValue, String sizeValue) throws Exception {
         given(getAuditLogPageByCurrentUseCase.getPage(
            any(), any(Pageable.class), nullable(String.class))).willReturn(pageDto);
         given(pageResponseMapper.from(any(PageDto.class), any(Pageable.class))).willReturn(page);

         mockMvc.perform(get(URI_TEMPLATE)
               .param("page", pageValue)
               .param("size", sizeValue)
               .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

         final var pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
         verify(getAuditLogPageByCurrentUseCase, times(1))
            .getPage(any(), pageableCaptor.capture(), nullable(String.class));
         assertEquals(0, pageableCaptor.getValue().getPageNumber());
         assertEquals(10, pageableCaptor.getValue().getPageSize());
      }
   }
}