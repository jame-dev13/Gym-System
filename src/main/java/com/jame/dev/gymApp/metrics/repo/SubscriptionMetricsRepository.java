package com.jame.dev.gymApp.metrics.repo;

import com.jame.dev.gymApp.entity.SubscriptionEntity;
import com.jame.dev.gymApp.model.metrics.PeriodCountDto;
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
           SELECT COUNT(DISTINCT s) FROM SubscriptionEntity s
           JOIN s.subscriptionPeriods p
           WHERE s.active = true AND p.startPeriod < :now
           """)
   long countByStartDateBefore(@Param("now") final LocalDate now);

   @Query("""
           SELECT p.period, COUNT(DISTINCT s)
           FROM SubscriptionEntity s
           JOIN s.subscriptionPeriods p
           WHERE s.active = true AND s.finished = false
           GROUP BY p.period
           """)
   List<PeriodCountDto> countSubsByPeriod();

   @Query("""
           SELECT
               FUNCTION('TO_CHAR', p.startPeriod, 'FMMonth'), COUNT(s)
           FROM SubscriptionEntity s
           JOIN s.subscriptionPeriods p
           WHERE s.active = true AND s.finished = false
           GROUP BY FUNCTION('TO_CHAR', p.startPeriod, 'FMMonth')
           ORDER BY FUNCTION('TO_CHAR', p.startPeriod, 'FMMonth')
           """)
   List<SubsPerMonthDto> countSubsByMonth();

   @Query("""
           SELECT
           m.membership, COUNT(s)
           FROM SubscriptionEntity s
           JOIN s.pricing.memberShipEntity m
           GROUP BY m.membership
           """)
   List<SubsPerMembership> countSubsByMembership();
}
