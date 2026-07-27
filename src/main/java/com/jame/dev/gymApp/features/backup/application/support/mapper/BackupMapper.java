package com.jame.dev.gymApp.features.backup.application.support.mapper;

import com.jame.dev.gymApp.features.backup.api.response.BackupResponse;
import com.jame.dev.gymApp.features.backup.domain.model.BackupDocument;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface BackupMapper {

   @Mapping(target = "createdAt", expression = "java(mapInstantToHumanReadable(backupDocument))")
   BackupResponse toResponse(final BackupDocument backupDocument);

   default String mapInstantToHumanReadable(final BackupDocument backupDocument) {
      final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE MMMM, d yyyy HH:mm:ss")
         .withZone(ZoneId.systemDefault())
         .withLocale(Locale.US);
      return formatter.format(backupDocument.getCreatedAt());
   }
}
