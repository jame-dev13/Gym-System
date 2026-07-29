package com.jame.dev.gymApp.features.backup.infrastructure.custom_impl;

import com.jame.dev.gymApp.domain.repository.CustomMongoSpecification;
import com.jame.dev.gymApp.features.backup.domain.model.BackupDocument;
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
public class BackupRepositoryImpl implements CustomMongoSpecification<BackupDocument> {
   private final MongoTemplate mongoTemplate;

   @Override
   public Page<BackupDocument> search(final Pageable pageable, final String search) {
      final Query query = new Query();
      query.with(pageable);

      if (search != null && !search.isBlank()) {
         query.addCriteria(
            new Criteria()
               .orOperator(
                  Criteria.where("backupStatus").regex(search, "i"),
                  Criteria.where("fileName").regex(search, "i"),
                  Criteria.where("createdBy").regex(search, "i")
               )
         );
      }

      final List<BackupDocument> content = mongoTemplate.find(query, BackupDocument.class);
      final long totalElements = mongoTemplate.count(
         Query.of(query)
            .skip(-1)
            .limit(-1),
         BackupDocument.class
      );
      return new PageImpl<>(content, pageable, totalElements);
   }
}
