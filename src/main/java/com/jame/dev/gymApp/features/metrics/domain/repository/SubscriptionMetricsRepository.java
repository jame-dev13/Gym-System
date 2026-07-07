package com.jame.dev.gymApp.features.metrics.domain.repository;

import com.jame.dev.gymApp.features.metrics.api.response.TotalSubscriptions;
import com.jame.dev.gymApp.features.metrics.domain.model.SubsPerMembership;
import com.jame.dev.gymApp.features.metrics.domain.model.SubsPerMonthDto;
import com.jame.dev.gymApp.features.metrics.infrastructure.query.MetricsRepository;
import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionEntity;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface SubscriptionMetricsRepository extends
   MetricsRepository<SubscriptionEntity, Long> {

   @NativeQuery("SELECT DISTINCT COUNT(*) AS total FROM subscriptions WHERE paid = true")
   TotalSubscriptions countAllSubscriptionDistinct();

   @NativeQuery("""
      SELECT
         DISTINCT COUNT(s) as total
      FROM subscriptions s
      INNER JOIN subscription_periods sp ON sp.subscription_id = s.id
      INNER JOIN periods p ON p.id = sp.period_id
      WHERE s.paid = true AND p.start_period <= :now
      """)
   TotalSubscriptions countByStartDateBefore(@Param("now") LocalDate now);

   @NativeQuery("""
      SELECT
          CAST(TO_CHAR(p.start_period, 'FMMon') AS varchar(4)) AS month,
          COUNT(s) AS total
      FROM subscriptions s
      LEFT JOIN subscription_periods sp ON sp.subscription_id = s.id
      INNER JOIN periods p ON p.id = sp.period_id
      WHERE s.paid = true
      GROUP BY TO_CHAR(p.start_period, 'FMMon')
      ORDER BY MIN(p.start_period)
      """)
   List<SubsPerMonthDto> countSubsByMonth();

   @NativeQuery("""
      SELECT
        CAST(m.membership as varchar(15)) AS membership,
        COUNT(s) AS subsCount
      FROM subscriptions s
      INNER JOIN membership_pricing mp ON mp.id = s.pricing_id
      INNER JOIN memberships m ON m.id = mp.membership_id
      GROUP BY m.membership
      ORDER BY m.membership DESC
      """)
   List<SubsPerMembership> countSubsByMembership();
}
