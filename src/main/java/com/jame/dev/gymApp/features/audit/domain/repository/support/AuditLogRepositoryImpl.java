package com.jame.dev.gymApp.features.audit.domain.repository.support;

import com.jame.dev.gymApp.domain.repository.CustomMongoSpecification;
import com.jame.dev.gymApp.features.audit.domain.model.AuditLogDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class AuditLogRepositoryImpl implements
   CustomMongoSpecification<AuditLogDocument> {
   private final MongoTemplate mongoTemplate;

   @Override
   public Page<AuditLogDocument> search(Pageable pageable, String search) {
      final Query query = new Query().with(pageable);
      if (search != null && !search.isBlank()) {
         query.addCriteria(
            new Criteria().orOperator(
               Criteria.where("action").regex(search, "i"),
               Criteria.where("entity.type").regex(search, "i")
            )
         );
      }
      final List<AuditLogDocument> content = mongoTemplate.find(query, AuditLogDocument.class);
      final long totalElements = mongoTemplate.count(
         Query.of(query)
            .limit(-1)
            .skip(-1),
         AuditLogDocument.class);

      return new PageImpl<>(content, pageable, totalElements);
   }
}
