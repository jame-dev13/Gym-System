package com.jame.dev.gymApp.features.audit.domain.repository.specification;

import com.jame.dev.gymApp.features.audit.domain.model.AuditLogDocument;
import com.jame.dev.gymApp.features.audit.infrastructure.specification.AuditLogSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;

@Repository
@RequiredArgsConstructor
public class AuditLogSpecificationRepository implements AuditLogSpecifications {

   private final MongoTemplate mongoTemplate;

   private static final Function<String, Criteria> actorCriteria =
      username -> Criteria.where("actor.username")
         .regex("^" + Pattern.quote(username) + "$", "i");

   @Override
   public Page<AuditLogDocument> findAllByCurrentActor(
      Pageable pageable,
      String actorUsername,
      String search
   ) {
      final Query query = new Query();

      query.addCriteria(actorCriteria.apply(actorUsername));

      if (search != null && !search.isBlank()) {
         query.addCriteria(
            new Criteria().orOperator(
               Criteria.where("action").regex(Pattern.quote(search), "i"),
               Criteria.where("entity.type").regex(Pattern.quote(search), "i")
            )
         );
      }

      final long totalElements = mongoTemplate.count(
         Query.of(query),
         AuditLogDocument.class
      );

      query.with(pageable);

      final List<AuditLogDocument> content =
         mongoTemplate.find(query, AuditLogDocument.class);

      return new PageImpl<>(content, pageable, totalElements);
   }
}