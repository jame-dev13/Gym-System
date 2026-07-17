package com.jame.dev.gymApp.features.metrics.infrastructure.query;

import com.jame.dev.gymApp.features.metrics.domain.model.CustomerEvolution;
import com.jame.dev.gymApp.features.metrics.domain.model.SubscriberEvolution;
import com.jame.dev.gymApp.features.metrics.domain.model.MonthTotal;
import com.jame.dev.gymApp.features.metrics.domain.repository.EvolutionMetricsRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class EvolutionMetricsRepositorySqlAdapter implements EvolutionMetricsRepository {

   @PersistenceContext
   private final EntityManager em;

   @Override
   public List<CustomerEvolution> calculateJoiningCustomerEvolution(final long year) {
      final String sql = """
            SELECT
             TO_CHAR(MAKE_DATE(EXTRACT(YEAR FROM CURRENT_DATE)::int, m.month_number, 1), 'Mon') AS month,
             COUNT(c.id) AS customersNum
         FROM generate_series(1, 12) AS m(month_number)
         LEFT JOIN customers c
             ON EXTRACT(MONTH FROM c.created_at) = m.month_number
             AND EXTRACT(YEAR FROM c.created_at) = :year
         GROUP BY m.month_number
         ORDER BY m.month_number
         """;
      @SuppressWarnings("unchecked") final List<Tuple> rows = em.createNativeQuery(sql, Tuple.class)
         .setParameter("year", year)
         .getResultList();

      return rows.stream()
         .map(row -> new CustomerEvolution(
            row.get("month", String.class),
            row.get("customersNum", Long.class)
         ))
         .toList();
   }

   @Override
   public List<CustomerEvolution> calculateDowningCustomerEvolution(final long year) {
      final String sql = """
            SELECT
             TO_CHAR(MAKE_DATE(EXTRACT(YEAR FROM CURRENT_DATE)::int, m.month_number, 1), 'Mon') AS month,
             COUNT(c.id) AS customersNum
         FROM generate_series(1, 12) AS m(month_number)
         LEFT JOIN customers c
             ON EXTRACT(MONTH FROM c.deleted_at) = m.month_number
             AND EXTRACT(YEAR FROM c.deleted_at) = :year
         GROUP BY m.month_number
         ORDER BY m.month_number
         """;
      @SuppressWarnings("unchecked") final List<Tuple> rows = em.createNativeQuery(sql, Tuple.class)
         .setParameter("year", year)
         .getResultList();

      return rows.stream()
         .map(row -> new CustomerEvolution(
            row.get("month", String.class),
            row.get("customersNum", Long.class)
         ))
         .toList();
   }

   @Override
   public List<SubscriberEvolution> calculateJoiningSubscriberEvolution(final long year) {
      final String sql = """
            SELECT
             TO_CHAR(MAKE_DATE(EXTRACT(YEAR FROM CURRENT_DATE)::int, m.month_number, 1), 'Mon') AS month,
             COUNT(s.id) AS subscriptionsNum
         FROM generate_series(1, 12) AS m(month_number)
         LEFT JOIN subscriptions s
             ON EXTRACT(MONTH FROM s.created_at) = m.month_number
             AND s.status = 'PAID'
             AND EXTRACT(YEAR FROM s.created_at) = :year
         GROUP BY m.month_number
         ORDER BY m.month_number
         """;
      @SuppressWarnings("unchecked") final List<Tuple> rows = em.createNativeQuery(sql, Tuple.class)
         .setParameter("year", year)
         .getResultList();

      return rows.stream()
         .map(row -> new SubscriberEvolution(
            row.get("month", String.class),
            row.get("subscriptionsNum", Long.class)
         ))
         .toList();
   }

   @Override
   public List<SubscriberEvolution> calculateDowningSubscribersBeforeEndTime(long year) {
      final String sql = """
         SELECT
             TO_CHAR(MAKE_DATE(EXTRACT(YEAR FROM CURRENT_DATE)::int, m.month_number, 1), 'Mon') AS month,
             COUNT(DISTINCT s.id) AS subscriptionsNum
         FROM generate_series(1, 12) AS m(month_number)
         LEFT JOIN subscriptions s
             ON EXTRACT(MONTH FROM s.created_at) = m.month_number
             AND (s.status = 'FINALIZED' OR s.active = false)
             AND EXTRACT(YEAR FROM s.created_at) = :year
         LEFT JOIN
             subscription_periods sp ON sp.subscription_id = s.id
         LEFT JOIN
             periods p ON p.id = sp.period_id AND
             p.end_period - p.start_period > 0
         GROUP by m.month_number
         ORDER BY m.month_number
         """;
      @SuppressWarnings("unchecked") final List<Tuple> rows = em.createNativeQuery(sql, Tuple.class)
         .setParameter("year", year)
         .getResultList();
      return rows.stream()
         .map(
            r -> new SubscriberEvolution(
               r.get("month", String.class),
               r.get("subscriptionsNum", Long.class))
         )
         .toList();
   }

   @Override
   public List<MonthTotal> calculateBillingEvolution(final long year) {
      final String sql = """
         SELECT
             TO_CHAR(MAKE_DATE(EXTRACT(YEAR FROM CURRENT_DATE)::int, m.month_number, 1), 'Mon') AS month,
             COALESCE(SUM(p.amount), 0) AS total
         FROM generate_series(1, 12) AS m(month_number)
         LEFT JOIN payments p
             ON EXTRACT(MONTH FROM p.created_at) = m.month_number
             AND p.amount > 0
             AND EXTRACT(YEAR FROM p.created_at) = :year
         GROUP BY m.month_number
         ORDER BY m.month_number
         """;
      @SuppressWarnings("unchecked") final List<Tuple> rows = em.createNativeQuery(sql, Tuple.class)
         .setParameter("year", year)
         .getResultList();

      return rows.stream()
         .map(row -> new MonthTotal(
            row.get("month", String.class),
            row.get("total", BigDecimal.class)
         ))
         .toList();
   }
}
