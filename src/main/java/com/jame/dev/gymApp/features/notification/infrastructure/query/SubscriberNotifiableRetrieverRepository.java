package com.jame.dev.gymApp.features.notification.infrastructure.query;

import com.jame.dev.gymApp.features.notification.application.dto.NotifiableInfo;
import com.jame.dev.gymApp.features.notification.domain.model.SubscriberNotificationEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public interface SubscriberNotifiableRetrieverRepository extends Repository<SubscriberNotificationEntity, UUID> {

   @Query("""
      SELECT DISTINCT
         new com.jame.dev.gymApp.features.notification.application.dto.NotifiableInfo(
                  u.email, sfe.rangeNotificationDays
         )
      FROM SubscriberNotificationEntity sfe
         JOIN sfe.subscription s
         JOIN s.customer c
         JOIN c.user u
      WHERE sfe.nextNotificationDate >= :start
         AND sfe.nextNotificationDate < :end
      """)
   Set<NotifiableInfo> findAllNotificationAvailableMailAddressesByStartAndEnd(
      @Param("start") LocalDateTime start,
      @Param("end") LocalDateTime end
   );
}
