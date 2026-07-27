package com.jame.dev.gymApp.features.backup.domain.model;


import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Document(collection = "backups")
public class BackupDocument {

   @MongoId
   @Setter(AccessLevel.NONE)
   @Builder.Default
   private UUID id = UUID.randomUUID();

   @Field("fileName")
   @NotEmpty(message = "File name should not be empty.")
   private String fileName;

   @Field("size")
   private long size;

   @Field("checksum")
   @NotEmpty(message = "Checksum required as a sign for the record")
   @Setter(AccessLevel.NONE)
   private String checksum;

   @Field("backupStatus")
   private BackupStatus backupStatus;

   @Field("createdBy")
   @Setter(AccessLevel.NONE)
   private String createdBy;

   @Field("createdAt")
   @Setter(AccessLevel.NONE)
   @Indexed(direction = IndexDirection.DESCENDING, name = "backups_createdAt_index")
   @Builder.Default
   private Instant createdAt = Instant.now();

   @Override
   public boolean equals(Object o) {
      if (o == null || getClass() != o.getClass()) return false;
      BackupDocument that = (BackupDocument) o;
      return Objects.equals(id, that.id);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id);
   }
}
