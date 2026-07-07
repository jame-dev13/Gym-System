package com.jame.dev.gymApp.features.metrics.domain.repository;

import com.jame.dev.gymApp.features.metrics.api.response.TotalEarned;
import com.jame.dev.gymApp.features.metrics.domain.model.TotalPerMembershipTypeDto;
import com.jame.dev.gymApp.features.metrics.domain.model.TotalPerMonth;
import com.jame.dev.gymApp.features.metrics.infrastructure.query.MetricsRepository;
import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionEntity;
import org.springframework.data.jpa.repository.NativeQuery;

import java.util.List;

public interface EarningMetricsRepository extends MetricsRepository<SubscriptionEntity, Long> {

   @NativeQuery("""
           SELECT
             COALESCE(SUM(mp.price), 0) as total
           FROM subscriptions s
           INNER JOIN membership_pricing mp ON mp.id = s.pricing_id
           WHERE s.paid = true
           """)
   TotalEarned calculateTotalEarned();

   @NativeQuery("""
           SELECT
               CAST(EXTRACT(YEAR FROM p.start_period) AS integer) AS year,
               CAST(TO_CHAR(p.start_period, 'FMMon') AS varchar(4)) AS month,
               COALESCE(SUM(mp.price), 0) as total
           FROM subscriptions s
           INNER JOIN subscription_periods sp ON sp.subscription_id = s.id
           INNER JOIN periods p ON p.id = sp.period_id
           INNER JOIN membership_pricing mp ON mp.id = s.pricing_id
           WHERE s.paid = true
           GROUP BY
               EXTRACT(YEAR FROM p.start_period),
               EXTRACT(MONTH FROM p.start_period),
               TO_CHAR(p.start_period, 'FMMon')
           ORDER BY
               EXTRACT(YEAR FROM p.start_period),
               EXTRACT(MONTH FROM p.start_period)
      """)
   List<TotalPerMonth> calculateTotalPerMonth();

   @NativeQuery("""
           SELECT
              m.membership as membership,
              COALESCE(SUM(mp.price), 0) as total
           FROM subscriptions s
           INNER JOIN membership_pricing mp ON mp.id = s.pricing_id
           INNER JOIN memberships m ON m.id = mp.membership_id
           WHERE s.paid = true
           GROUP BY m.membership
           ORDER BY m.membership DESC
           """)
   List<TotalPerMembershipTypeDto> calculateTotalPerMembership();
}
