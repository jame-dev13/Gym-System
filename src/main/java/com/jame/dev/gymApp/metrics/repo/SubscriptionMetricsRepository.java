package com.jame.dev.gymApp.metrics.repo;

import com.jame.dev.gymApp.entity.SubscriptionEntity;
import com.jame.dev.gymApp.model.metrics.SubsPerMembership;
import com.jame.dev.gymApp.model.metrics.SubsPerMonthDto;
import com.jame.dev.gymApp.repository.common.MetricsRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface SubscriptionMetricsRepository extends
        MetricsRepository<SubscriptionEntity, Long> {

   long countDistinctByActiveTrueAndFinishedFalse();

   @Query("""
           SELECT COUNT(s) FROM SubscriptionEntity s
           WHERE s.active = true
           AND EXISTS (
               SELECT 1 FROM s.subscriptionPeriods p
               WHERE p.startPeriod < :now
           )
           """)
   long countByStartDateBefore(@Param("now") LocalDate now);

   @Query("""
           SELECT new com.jame.dev.gymApp.model.metrics.SubsPerMonthDto(
               CAST(FUNCTION('TO_CHAR', p.startPeriod, 'FMMonth') AS string) AS month,
               COUNT(s) as total
           )
           FROM SubscriptionEntity s
           JOIN s.subscriptionPeriods p
           WHERE s.active = true AND s.finished = false
           GROUP BY FUNCTION('TO_CHAR', p.startPeriod, 'FMMonth')
           ORDER BY MIN(p.startPeriod)
           """)
   List<SubsPerMonthDto> countSubsByMonth();

   @Query("""
           SELECT
           m.membership, COUNT(s)
           FROM SubscriptionEntity s
           JOIN s.pricing.memberShipEntity m
           GROUP BY m.membership
           ORDER BY m.membership ASC
           """)
   List<SubsPerMembership> countSubsByMembership();
}
