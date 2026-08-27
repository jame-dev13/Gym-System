package com.jame.dev.gymApp.features.metrics.domain.repository;

import com.jame.dev.gymApp.features.metrics.api.response.TotalEarned;
import com.jame.dev.gymApp.features.metrics.domain.model.TotalPerMembershipTypeDto;
import com.jame.dev.gymApp.features.metrics.domain.model.TotalPerMonth;
import com.jame.dev.gymApp.features.metrics.domain.model.YearPeriodicalEarning;
import com.jame.dev.gymApp.features.metrics.infrastructure.query.MetricsRepository;
import com.jame.dev.gymApp.features.subscription.domain.model.SubscriptionEntity;
import org.springframework.data.jpa.repository.NativeQuery;

import java.util.List;

public interface EarningMetricsRepository extends MetricsRepository<SubscriptionEntity, Long> {

   @NativeQuery("""
           SELECT
              DISTINCT COALESCE(SUM(m.price), 0) as total
           FROM subscriptions s
           INNER JOIN memberships m ON m.id = s.membership_id
           WHERE s.status = 'PAID'
           """)
   TotalEarned calculateTotalEarned();

   @NativeQuery("""
           SELECT
               CAST(EXTRACT(YEAR FROM p.start_period) AS integer) AS year,
               CAST(TO_CHAR(p.start_period, 'FMMon') AS varchar(4)) AS month,
               COALESCE(SUM(m.price), 0) as total
           FROM subscriptions s
           INNER JOIN subscription_periods sp ON sp.subscription_id = s.id
           INNER JOIN periods p ON p.id = sp.period_id
           INNER JOIN memberships m ON m.id = s.membership_id
           WHERE s.status = 'PAID'
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
              COALESCE(SUM(m.price), 0) as total
           FROM subscriptions s
           INNER JOIN memberships m ON m.id = s.membership_id
           WHERE s.status = 'PAID'
           GROUP BY m.membership
           ORDER BY m.membership DESC
           """)
   List<TotalPerMembershipTypeDto> calculateTotalPerMembership();

   @NativeQuery("""
      SELECT
        CAST(EXTRACT(YEAR FROM p.start_period) AS INTEGER) AS year,
        CAST(
           UPPER(
             CONCAT(TO_CHAR(p.start_period, 'FMMon'), ' - ', TO_CHAR(p.end_period, 'FMMon'))) AS varchar(12)
           ) AS period,
        CAST(p.period AS varchar(12)) AS membership,
        COALESCE(SUM(m.price), 0) AS totalEarned,
        RANK() OVER (ORDER BY COALESCE(SUM(m.price), 0) DESC, EXTRACT(YEAR FROM p.start_period) DESC) AS rank
      FROM subscriptions s
      INNER JOIN memberships m ON m.id = s.membership_id
      INNER JOIN subscription_periods sp ON sp.subscription_id = s.id
      INNER JOIN periods p ON p.id = sp.period_id
      WHERE s.status = 'PAID'
      GROUP BY p.start_period, p.end_period, p.period
      """)
   List<YearPeriodicalEarning> calculatePeriodicalEarnings();
}
