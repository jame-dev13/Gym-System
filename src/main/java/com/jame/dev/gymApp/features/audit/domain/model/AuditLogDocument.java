package com.jame.dev.gymApp.features.audit.domain.model;

import com.jame.dev.gymApp.features.audit.application.dto.*;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.Objects;

@Builder
@RequiredArgsConstructor
@Getter
@Document(collection = "audit_logs")
@CompoundIndexes({
   @CompoundIndex(
      name = "audit_logs_entity_action_created_idx",
      def = "{ 'entity.type': 1, 'action': 1, 'created_at': -1 }"
   ),
   @CompoundIndex(
      name = "audit_logs_actor_created_idx",
      def = "{ 'actor.userId': 1, 'created_at': -1 }"
   )
})
public class AuditLogDocument {

   @Id
   @Builder.Default
   private final ObjectId id = ObjectId.get();

   @Field("entity")
   private final AuditLogEntity entity;

   @Field("action")
   @Indexed
   private final AuditLogAction action;

   @Field("actor")
   private final AuditLogActor actor;

   @Field("payload")
   private final AuditPayload payload;

   @Field("success")
   private final boolean success;

   @Field("metadata")
   private final AuditLogMetadata metadata;

   @Field("created_at")
   @Indexed(direction = IndexDirection.DESCENDING)
   @Builder.Default
   private final Instant createdAt = Instant.now();

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      AuditLogDocument that = (AuditLogDocument) o;
      return Objects.equals(id, that.id);
   }

   @Override
   public int hashCode() {
      return Objects.hash(id);
   }

   @Override
   public String toString() {
      return "AuditLogDocument{" +
             "id=" + id +
             ", entityType=" + entity.type() +
             ", auditLogAction=" + action +
             ", actorId=" + actor.userId() +
             ", changes=" + payload +
             ", metadata=" + metadata +
             ", createdAt=" + createdAt +
             '}';
   }
}

