package com.jame.dev.gymApp.metrics.service;

import com.jame.dev.gymApp.features.metrics.domain.repository.EarningMetricsRepository;
import com.jame.dev.gymApp.features.metrics.application.service.EarningMetricsApplicationService;
import com.jame.dev.gymApp.features.metrics.domain.model.TotalPerMembershipTypeDto;
import com.jame.dev.gymApp.features.metrics.domain.model.TotalPerMonth;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EarningMetricsServiceTest {

   @Mock
   private EarningMetricsRepository repo;

   @InjectMocks
   private EarningMetricsApplicationService service;

   @Test
   @DisplayName("Should get the total earned")
   void getAllEarnings(){
      when(repo.calculateTotalEarned())
              .thenReturn(BigDecimal.valueOf(10_000d));

      final BigDecimal total = service.getTotal();

      verify(repo, atLeastOnce()).calculateTotalEarned();
      verifyNoMoreInteractions(repo);

      assertNotNull(total, "Total should not be null");
      assertTrue(total.doubleValue() >= 0d, "Total should not be negative");
   }

   @Test
   @DisplayName("Should get total earned per month.")
   void getEarningsPerMonth(){
      when(repo.calculateTotalPerMonth()).thenReturn(
              List.of(new TotalPerMonth(2025, "December", BigDecimal.valueOf(2000d)))
      );

      final var totalPerMonthList = service.getTotalPerMonth();

      verify(repo, atLeastOnce()).calculateTotalPerMonth();
      verifyNoMoreInteractions(repo);

      assertNotNull(totalPerMonthList, "List should not be null.");
   }

   @Test
   @DisplayName("Should get total earned ")
   void getEarningsPerMembership(){
      when(repo.calculateTotalPerMembership()).thenReturn(
              List.of(new TotalPerMembershipTypeDto("MONTHLY", BigDecimal.valueOf(9_000d)))
      );

      final List<TotalPerMembershipTypeDto> membershipTypeList = service.getTotalPerMembershipType();

      verify(repo, atLeastOnce()).calculateTotalPerMembership();
      verifyNoMoreInteractions(repo);

      assertNotNull(membershipTypeList, "List should not be null.");
   }
}
