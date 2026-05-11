package com.jame.dev.gymApp.features.metrics.domain.repository;

import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionEntity;
import com.jame.dev.gymApp.features.metrics.domain.model.TotalPerMembershipTypeDto;
import com.jame.dev.gymApp.features.metrics.domain.model.TotalPerMonth;
import com.jame.dev.gymApp.features.metrics.infrastructure.query.MetricsRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface EarningMetricsRepository extends MetricsRepository<SubscriptionEntity, Long> {

   @Query("""
           SELECT COALESCE(SUM(p.price), 0)
           FROM SubscriptionEntity s
           JOIN s.pricing p
           """)
   BigDecimal calculateTotalEarned();

   @Query("""
           SELECT new com.jame.dev.gymApp.model.metrics.TotalPerMonth(
               CAST(YEAR(sp.startPeriod) AS integer) AS year,
               CAST(FUNCTION('to_char', sp.startPeriod, 'FMMonth') AS string) AS month,
               CAST(COALESCE(SUM(p.price), 0) AS big_decimal) as total
           )
           FROM SubscriptionEntity s
           JOIN s.subscriptionPeriods sp
           JOIN s.pricing p
           GROUP BY
               YEAR(sp.startPeriod),
               FUNCTION('to_char', sp.startPeriod, 'FMMonth'),
               MONTH(sp.startPeriod)
           ORDER BY
               YEAR(sp.startPeriod),
               MONTH(sp.startPeriod)
           """)
   List<TotalPerMonth> calculateTotalPerMonth();

   @Query("""
           SELECT m.membership, COALESCE(SUM(p.price), 0)
           FROM SubscriptionEntity s
           JOIN s.pricing p
           JOIN s.pricing.memberShipEntity m
           GROUP BY m.membership
           """)
   List<TotalPerMembershipTypeDto> calculateTotalPerMembership();
}
