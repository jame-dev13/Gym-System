package com.jame.dev.gymApp.features.audit.domain.repository;

import com.jame.dev.gymApp.features.audit.domain.model.AuditLogDocument;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AuditLogRepository extends
   MongoRepository<AuditLogDocument, ObjectId>,
   AuditLogRepositoryCustom<AuditLogDocument> {
}
