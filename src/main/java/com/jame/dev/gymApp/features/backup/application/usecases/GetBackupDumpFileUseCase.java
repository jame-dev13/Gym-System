package com.jame.dev.gymApp.features.backup.application.usecases;

import org.springframework.core.io.Resource;

import java.net.MalformedURLException;
import java.util.UUID;

public interface GetBackupDumpFileUseCase {

   Resource getResourceDumpFile(final UUID documentId) throws MalformedURLException;

}
