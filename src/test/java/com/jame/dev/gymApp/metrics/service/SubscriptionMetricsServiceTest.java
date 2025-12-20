package com.jame.dev.gymApp.metrics.service;


import com.jame.dev.gymApp.metrics.repo.SubscriptionMetricsRepository;
import com.jame.dev.gymApp.metrics.service.out.SubscriptionMetricsServiceImp;
import com.jame.dev.gymApp.model.metrics.PeriodCountDto;
import com.jame.dev.gymApp.model.metrics.SubsPerMembership;
import com.jame.dev.gymApp.model.metrics.SubsPerMonthDto;
import com.jame.dev.gymApp.shared.enums.Membership;
import com.jame.dev.gymApp.shared.enums.Period;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SubscriptionMetricsServiceTest {

   @Mock
   private SubscriptionMetricsRepository repo;

   @InjectMocks
   private SubscriptionMetricsServiceImp service;


   @Test
   @DisplayName("Should return the count of all active and unfinished subscriptions")
   void allSubs() {
      when(repo.countDistinctByActiveTrueAndFinishedFalse()).thenReturn(2L);

      final long count = service.getTotalSubscriptions();

      verify(repo, times(1)).countDistinctByActiveTrueAndFinishedFalse();
      verifyNoMoreInteractions(repo);

      assertTrue(count >= 0, "Should return 0 or any long value greater then 0.");
   }

   @Captor
   private ArgumentCaptor<LocalDate> dateCaptor;

   @Test
   @DisplayName("Should return the count of subs that made before a given date")
   void subsBeforeDate() {
      final LocalDate date = LocalDate.of(2025, 12, 13);
      when(repo.countByStartDateBefore(any(LocalDate.class)))
              .thenReturn(3L);

      final long count = service.getSubscriptionsBefore(date);

      verify(repo, times(1)).countByStartDateBefore(dateCaptor.capture());
      verifyNoMoreInteractions(repo);

      final LocalDate dateValue = dateCaptor.getValue();

      assertNotNull(dateValue, "Date should not be null.");
      assertTrue(count >= 0, "Should return 0 or any long value greater then 0.");
   }
   @Test
   @DisplayName("Should return a list of subs by period")
   void subsByPeriod() {
      final List<PeriodCountDto> periodCountList = List.of(
                      new PeriodCountDto(Period.BIWEEKLY, 4L),
                      new PeriodCountDto(Period.MONTHLY, 8L),
                      new PeriodCountDto(Period.QUARTERLY, 10L),
                      new PeriodCountDto(Period.ANNUAL, 2L));
      when(repo.countSubsByPeriod())
              .thenReturn(periodCountList);

      final List<PeriodCountDto> periodCountDtos = service.getSubscriptionsPerPeriod();

      verify(repo, times(1)).countSubsByPeriod();
      verifyNoMoreInteractions(repo);

      assertNotNull(periodCountDtos, "List should not be null.");
   }

   @Test
   @DisplayName("Should return a list of subs by Memberships.")
   void subsByMemberships() {
      when(repo.countSubsByMembership())
              .thenReturn(List.of(new SubsPerMembership(Membership.MONTHLY, 2L)));

      final List<SubsPerMembership> membershipCountList = service.getSubscriptionsPerMembership();

      verify(repo, times(1)).countSubsByMembership();
      verifyNoMoreInteractions(repo);

      assertNotNull(membershipCountList, "List should not be null.");
   }

   @Test
   @DisplayName("Should return a list of subs by Month")
   void subsByMonth() {
      when(repo.countSubsByMonth())
              .thenReturn(List.of(new SubsPerMonthDto("December", 2L)));

      final List<SubsPerMonthDto> monthDtoList = service.getSubscriptionsPerMonth();

      verify(repo, times(1)).countSubsByMonth();
      verifyNoMoreInteractions(repo);

      assertNotNull(monthDtoList, "List should not be null.");
   }
}
