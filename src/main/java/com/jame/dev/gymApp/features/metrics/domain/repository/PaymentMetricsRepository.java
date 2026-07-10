package com.jame.dev.gymApp.features.metrics.domain.repository;

import com.jame.dev.gymApp.features.metrics.api.response.AnnualResumeResponse;
import com.jame.dev.gymApp.features.metrics.api.response.TotalInvestment;
import com.jame.dev.gymApp.features.metrics.domain.model.MonthTotal;
import com.jame.dev.gymApp.features.metrics.infrastructure.query.MetricsRepository;
import com.jame.dev.gymApp.features.subscription.domain.model.PaymentEntity;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PaymentMetricsRepository extends MetricsRepository<PaymentEntity, Long> {

   @NativeQuery("""
      SELECT
          COALESCE(SUM(p.amount), 0) AS total
      FROM payments p
      WHERE p.customer_id = :customerId
      """)
   TotalInvestment calculateTotalAmountExpended(@Param("customerId") long customerId);

   @NativeQuery("""
      SELECT
          COUNT(*) AS totalPaymentsMade,
          COALESCE(SUM(p.amount), 0) AS totalExpend,
          COALESCE(ROUND(AVG(p.amount), 2), 0) AS avergare,
          COUNT(*) FILTER ( WHERE p.payment_method = 'ELECTRONIC' ) as electronicPaymentsDone,
          COUNT(*) FILTER ( WHERE p.payment_method = 'PHYSIC' ) as physicPaymentsDone
      FROM payments p
      WHERE
          p.customer_id = :customerId AND
          p.created_at >= DATE_TRUNC('year', CURRENT_DATE) AND
          p.created_at < date_trunc('year', CURRENT_DATE) + INTERVAL '1 year'
      """)
   AnnualResumeResponse calculateAnnualResume(@Param("customerId") long customerId);

   @NativeQuery("""
      SELECT
          COUNT(*) AS totalPaymentsMade,
          COALESCE(SUM(p.amount), 0) AS totalExpend,
          COALESCE(ROUND(AVG(p.amount), 2), 0) AS avergare,
          COUNT(*) FILTER ( WHERE p.payment_method = 'ELECTRONIC' ) as electronicPaymentsDone,
          COUNT(*) FILTER ( WHERE p.payment_method = 'PHYSIC' ) as physicPaymentsDone
      FROM payments p
      WHERE
          p.created_at >= DATE_TRUNC('year', CURRENT_DATE) AND
          p.created_at < date_trunc('year', CURRENT_DATE) + INTERVAL '1 year'
      """)
   AnnualResumeResponse calculateAnnualResume();

   @NativeQuery("""
      SELECT
          TO_CHAR(MAKE_DATE(EXTRACT(YEAR FROM CURRENT_DATE)::int, m.month_number, 1), 'Mon') AS month,
          COALESCE(SUM(p.amount), 0) AS total
      FROM generate_series(1, 12) AS m(month_number)
      LEFT JOIN payments p
          ON EXTRACT(MONTH FROM p.created_at) = m.month_number
          AND p.customer_id = :customerId
          AND p.amount > 0
          AND p.created_at >= DATE_TRUNC('year', CURRENT_DATE)
          AND p.created_at < DATE_TRUNC('year', CURRENT_DATE) + INTERVAL '1 year'
      GROUP BY m.month_number
      ORDER BY m.month_number
      """)
   List<MonthTotal> calculatePaymentEvolutionAlongMonths(@Param("customerId") long customerId);

   @NativeQuery("""
      SELECT
          TO_CHAR(MAKE_DATE(EXTRACT(YEAR FROM CURRENT_DATE)::int, m.month_number, 1), 'Mon') AS month,
          COALESCE(SUM(p.amount), 0) AS total
      FROM generate_series(1, 12) AS m(month_number)
      LEFT JOIN payments p
          ON EXTRACT(MONTH FROM p.created_at) = m.month_number
          AND p.amount > 0
          AND p.created_at >= DATE_TRUNC('year', CURRENT_DATE)
          AND p.created_at < DATE_TRUNC('year', CURRENT_DATE) + INTERVAL '1 year'
      GROUP BY m.month_number
      ORDER BY m.month_number
      """)
   List<MonthTotal> calculatePaymentEvolutionAlongMonths();
}
