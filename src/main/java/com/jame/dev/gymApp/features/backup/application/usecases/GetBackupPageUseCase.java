package com.jame.dev.gymApp.features.backup.application.usecases;

import com.jame.dev.gymApp.application.dto.PageDto;
import com.jame.dev.gymApp.features.backup.api.response.BackupResponse;
import org.springframework.data.domain.Pageable;

public interface GetBackupPageUseCase {
   PageDto<BackupResponse> getBackupPage(final Pageable pageable, final String search);
}
