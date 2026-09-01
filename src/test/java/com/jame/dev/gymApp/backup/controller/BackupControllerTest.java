package com.jame.dev.gymApp.backup.controller;

import com.jame.dev.gymApp.application.dto.PageDto;
import com.jame.dev.gymApp.domain.exception.MissMatchException;
import com.jame.dev.gymApp.domain.exception.NotFoundException;
import com.jame.dev.gymApp.features.auth.domain.exception.TemporaryBlockedException;
import com.jame.dev.gymApp.features.auth.domain.exception.TooManyRequestsException;
import com.jame.dev.gymApp.features.auth.infrastructure.security.CustomAuthorizationFilter;
import com.jame.dev.gymApp.features.backup.api.BackupController;
import com.jame.dev.gymApp.features.backup.api.response.BackupResponse;
import com.jame.dev.gymApp.features.backup.application.usecases.CreateDatabaseBackupUseCase;
import com.jame.dev.gymApp.features.backup.application.usecases.DeleteBackupByIdUseCase;
import com.jame.dev.gymApp.features.backup.application.usecases.GetBackupByIdUseCase;
import com.jame.dev.gymApp.features.backup.application.usecases.GetBackupDumpFileUseCase;
import com.jame.dev.gymApp.features.backup.application.usecases.GetBackupPageUseCase;
import com.jame.dev.gymApp.features.backup.application.usecases.RestoreBackupDatabaseUseCase;
import com.jame.dev.gymApp.features.backup.domain.model.BackupStatus;
import com.jame.dev.gymApp.features.backup.infrastructure.rate_limiting.BackupRateLimiter;
import com.jame.dev.gymApp.presentation.exception.GlobalExceptionHandler;
import config.TestConfig;
import config.TestValidationConfig;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.validation.autoconfigure.ValidationAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.times;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
   controllers = BackupController.class,
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
class BackupControllerTest {

   @Autowired
   private MockMvc mockMvc;

   @MockitoBean
   private CreateDatabaseBackupUseCase createDatabaseBackupUseCase;

   @MockitoBean
   private GetBackupPageUseCase getBackupPageUseCase;

   @MockitoBean
   private GetBackupByIdUseCase getBackupByIdUseCase;

   @MockitoBean
   private RestoreBackupDatabaseUseCase restoreBackupDatabaseUseCase;

   @MockitoBean
   private GetBackupDumpFileUseCase getBackupDumpFileUseCase;

   @MockitoBean
   private DeleteBackupByIdUseCase deleteBackupByIdUseCase;

   @MockitoBean
   private BackupRateLimiter backupRateLimiter;

   private final String URI_TEMPLATE = "/app/v1/administration/backups";

   private final UUID backupId = UUID.randomUUID();

   private final BackupResponse backupResponse = new BackupResponse(
      backupId,
      "database_20240101_120000.dump",
      1024L,
      BackupStatus.SUCCESS,
      "admin@mail.com",
      "Tuesday January, 1 2024 12:00:00"
   );

   private final PageDto<BackupResponse> pageDto = new PageDto<>(
      List.of(backupResponse), 0, 5, 1L, "id", "DESC"
   );

   private static final String UUID_PATH_VARIABLE_ERRORS = """
      VALUE,     ERROR_CODE
      0,         TYPE_MISMATCH
      -100,      TYPE_MISMATCH
      letters,   TYPE_MISMATCH
      NULL,      TYPE_MISMATCH
      """;

   @Nested
   @DisplayName("GET: /app/v1/administration/backups")
   class GetBackupPageTests {

      @Test
      @DisplayName("GET[200] OK: get page /backups?page=0&size=5")
      void getBackupPage() throws Exception {
         given(getBackupPageUseCase.getBackupPage(any(Pageable.class), nullable(String.class)))
            .willReturn(pageDto);

         mockMvc.perform(get(URI_TEMPLATE)
               .param("page", "0")
               .param("size", "5")
               .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray());

         then(getBackupPageUseCase).should(times(1)).getBackupPage(any(Pageable.class), nullable(String.class));
      }

      @Test
      @DisplayName("GET[200] OK: get page /backups?search=database")
      void getBackupPageWithSearch() throws Exception {
         given(getBackupPageUseCase.getBackupPage(any(Pageable.class), nullable(String.class)))
            .willReturn(pageDto);

         mockMvc.perform(get(URI_TEMPLATE)
               .param("page", "0")
               .param("size", "5")
               .param("search", "database")
               .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray());

         then(getBackupPageUseCase).should(times(1)).getBackupPage(any(Pageable.class), nullable(String.class));
      }

      @ParameterizedTest
      @CsvSource(value = {"0|letter", "NULL|10"}, delimiter = '|')
      @DisplayName("GET[200] OK: invalid pagination params fall back to defaults")
      void invalidPaginationFallsBackToDefaults(String pageValue, String sizeValue) throws Exception {
         given(getBackupPageUseCase.getBackupPage(any(Pageable.class), nullable(String.class)))
            .willReturn(pageDto);

         mockMvc.perform(get(URI_TEMPLATE)
               .param("page", pageValue)
               .param("size", sizeValue)
               .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray());

         then(getBackupPageUseCase).should(times(1)).getBackupPage(any(Pageable.class), nullable(String.class));
      }

      @Nested
      @DisplayName("GET: /app/v1/administration/backups/{id}")
      class GetBackupByIdTests {

         @Test
         @DisplayName("GET[200] OK: /backups/{id}")
         void getBackup() throws Exception {
            given(getBackupByIdUseCase.getById(any(UUID.class))).willReturn(backupResponse);

            mockMvc.perform(get(URI_TEMPLATE + "/" + backupId)
                  .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.*").exists())
               .andExpect(jsonPath("$.id").isNotEmpty());

            then(getBackupByIdUseCase).should(times(1)).getById(any(UUID.class));
         }

         @Test
         @DisplayName("GET[404] Not Found: /backups/{id}")
         void backupNotFound() throws Exception {
            given(getBackupByIdUseCase.getById(any(UUID.class)))
               .willThrow(NotFoundException.class);

            mockMvc.perform(get(URI_TEMPLATE + "/" + backupId)
                  .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.*").exists())
               .andExpect(jsonPath("$.status").value(404));

            then(getBackupByIdUseCase).should(times(1)).getById(any(UUID.class));
         }

         @ParameterizedTest
         @CsvSource(useHeadersInDisplayName = true,
            textBlock = UUID_PATH_VARIABLE_ERRORS,
            nullValues = "NULL")
         @DisplayName("GET[400] Bad Request: /backups/{invalidPath}")
         void badRequestInvalidPathVariable(String value, String expectedCode) throws Exception {
            mockMvc.perform(get(URI_TEMPLATE + "/" + value)
                  .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.*").exists())
               .andExpect(jsonPath("$.status").value(400))
               .andExpect(jsonPath("$.code").value(expectedCode));

            then(getBackupByIdUseCase).shouldHaveNoInteractions();
         }
      }

      @Nested
      @DisplayName("GET: /app/v1/administration/backups/{id}/download")
      class DownloadBackupTests {

         @Test
         @DisplayName("GET[200] OK: /backups/{id}/download")
         void downloadBackup() throws Exception {
            Path temp = Files.createTempFile("backup", ".dump");
            Files.write(temp, "dump-content".getBytes(StandardCharsets.UTF_8));
            Resource file = new FileSystemResource(temp);
            given(getBackupDumpFileUseCase.getResourceDumpFile(any(UUID.class))).willReturn(file);

            mockMvc.perform(get(URI_TEMPLATE + "/" + backupId + "/download")
                  .accept(MediaType.APPLICATION_OCTET_STREAM))
               .andExpect(status().isOk())
               .andExpect(header().exists(CONTENT_DISPOSITION))
               .andExpect(header().string(CONTENT_DISPOSITION, containsString("attachment")));

            then(getBackupDumpFileUseCase).should(times(1)).getResourceDumpFile(any(UUID.class));
            Files.deleteIfExists(temp);
         }

         @Test
         @DisplayName("GET[404] Not Found: /backups/{id}/download")
         void backupNotFoundForDownload() throws Exception {
            given(getBackupDumpFileUseCase.getResourceDumpFile(any(UUID.class)))
               .willThrow(NotFoundException.class);

            mockMvc.perform(get(URI_TEMPLATE + "/" + backupId + "/download")
                  .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.*").exists())
               .andExpect(jsonPath("$.status").value(404));

            then(getBackupDumpFileUseCase).should(times(1)).getResourceDumpFile(any(UUID.class));
         }

         @ParameterizedTest
         @CsvSource(useHeadersInDisplayName = true,
            textBlock = UUID_PATH_VARIABLE_ERRORS,
            nullValues = "NULL")
         @DisplayName("GET[400] Bad Request: /backups/{invalidPath}/download")
         void badRequestInvalidPathVariable(String value, String expectedCode) throws Exception {
            mockMvc.perform(get(URI_TEMPLATE + "/" + value + "/download")
                  .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.*").exists())
               .andExpect(jsonPath("$.status").value(400))
               .andExpect(jsonPath("$.code").value(expectedCode));

            then(getBackupDumpFileUseCase).shouldHaveNoInteractions();
         }
      }
   }

   @Nested
   @DisplayName("POST: /app/v1/administration/backups")
   class CreateDatabaseBackupTests {

      @Test
      @DisplayName("POST[202] Accepted: /backups")
      void createBackup() throws Exception {
         mockMvc.perform(post(URI_TEMPLATE)
               .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isAccepted());

         then(backupRateLimiter).should(times(1)).checkRateLimit(any(HttpServletRequest.class));
         then(createDatabaseBackupUseCase).should(times(1)).createDatabaseBackup();
      }

      @Test
      @DisplayName("POST[429] Too Many Requests: /backups")
      void tooManyRequests() throws Exception {
         willThrow(TooManyRequestsException.class)
            .given(backupRateLimiter).checkRateLimit(any(HttpServletRequest.class));

         mockMvc.perform(post(URI_TEMPLATE)
               .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isTooManyRequests())
            .andExpect(jsonPath("$.*").exists());

         then(backupRateLimiter).should(times(1)).checkRateLimit(any(HttpServletRequest.class));
         then(createDatabaseBackupUseCase).shouldHaveNoInteractions();
      }

      @Test
      @DisplayName("POST[423] Locked: /backups blocked temporarily")
      void temporaryBlocked() throws Exception {
         willThrow(TemporaryBlockedException.class)
            .given(backupRateLimiter).checkRateLimit(any(HttpServletRequest.class));

         mockMvc.perform(post(URI_TEMPLATE)
               .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isLocked())
            .andExpect(jsonPath("$.*").exists());

         then(backupRateLimiter).should(times(1)).checkRateLimit(any(HttpServletRequest.class));
         then(createDatabaseBackupUseCase).shouldHaveNoInteractions();
      }
   }

   @Nested
   @DisplayName("POST: /app/v1/administration/backups/{id}/restore")
   class RestoreBackupTests {

      @Test
      @DisplayName("POST[202] Accepted: /backups/{id}/restore")
      void restoreBackup() throws Exception {
         mockMvc.perform(post(URI_TEMPLATE + "/" + backupId + "/restore")
               .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isAccepted());

         then(restoreBackupDatabaseUseCase).should(times(1)).restore(any(UUID.class));
      }

      @Test
      @DisplayName("POST[404] Not Found: /backups/{id}/restore")
      void backupNotFoundForRestore() throws Exception {
         willThrow(NotFoundException.class)
            .given(restoreBackupDatabaseUseCase).restore(any(UUID.class));

         mockMvc.perform(post(URI_TEMPLATE + "/" + backupId + "/restore")
               .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.status").value(404));

         then(restoreBackupDatabaseUseCase).should(times(1)).restore(any(UUID.class));
      }

      @Test
      @DisplayName("POST[409] Conflict: /backups/{id}/restore checksum mismatch")
      void checksumMismatch() throws Exception {
         willThrow(MissMatchException.class)
            .given(restoreBackupDatabaseUseCase).restore(any(UUID.class));

         mockMvc.perform(post(URI_TEMPLATE + "/" + backupId + "/restore")
               .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.status").value(409));

         then(restoreBackupDatabaseUseCase).should(times(1)).restore(any(UUID.class));
      }

      @ParameterizedTest
      @CsvSource(useHeadersInDisplayName = true,
         textBlock = UUID_PATH_VARIABLE_ERRORS,
         nullValues = "NULL")
      @DisplayName("POST[400] Bad Request: /backups/{invalidPath}/restore")
      void badRequestInvalidPathVariable(String value, String expectedCode) throws Exception {
         mockMvc.perform(post(URI_TEMPLATE + "/" + value + "/restore")
               .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.code").value(expectedCode));

         then(restoreBackupDatabaseUseCase).shouldHaveNoInteractions();
      }
   }

   @Nested
   @DisplayName("DELETE: /app/v1/administration/backups/{id}")
   class DeleteBackupTests {

      @Test
      @DisplayName("DELETE[204] No Content: /backups/{id}")
      void deleteBackup() throws Exception {
         mockMvc.perform(delete(URI_TEMPLATE + "/" + backupId)
               .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

         then(deleteBackupByIdUseCase).should(times(1)).deleteById(any(UUID.class));
      }

      @ParameterizedTest
      @CsvSource(useHeadersInDisplayName = true,
         textBlock = UUID_PATH_VARIABLE_ERRORS,
         nullValues = "NULL")
      @DisplayName("DELETE[400] Bad Request: /backups/{invalidPath}")
      void badRequestInvalidPathVariable(String value, String expectedCode) throws Exception {
         mockMvc.perform(delete(URI_TEMPLATE + "/" + value)
               .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.*").exists())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.code").value(expectedCode));

         then(deleteBackupByIdUseCase).shouldHaveNoInteractions();
      }
   }
}