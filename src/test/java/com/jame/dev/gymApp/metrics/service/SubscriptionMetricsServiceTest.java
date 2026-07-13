package com.jame.dev.gymApp.metrics.service;


import com.jame.dev.gymApp.features.metrics.domain.repository.SubscriptionMetricsRepository;
import com.jame.dev.gymApp.features.metrics.application.service.SubscriptionMetricsApplicationService;
import com.jame.dev.gymApp.features.metrics.api.response.SubscriptionsPerMembershipResponse;
import com.jame.dev.gymApp.features.metrics.api.response.SubscriptionsPerMonthResponse;
import com.jame.dev.gymApp.features.metrics.api.response.TotalSubscriptions;
import com.jame.dev.gymApp.features.metrics.domain.model.SubsPerMembership;
import com.jame.dev.gymApp.features.metrics.domain.model.SubsPerMonthDto;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SubscriptionMetricsServiceTest {

   @Mock
   private SubscriptionMetricsRepository repo;

   @InjectMocks
   private SubscriptionMetricsApplicationService service;


   @Test
   @DisplayName("Should return the count of all active and unfinished subscriptions")
   void allSubs() {
      when(repo.countAllSubscriptionDistinct()).thenReturn(new TotalSubscriptions(2L));

      final TotalSubscriptions result = service.getTotalSubscriptions();

      verify(repo, times(1)).countAllSubscriptionDistinct();
      verifyNoMoreInteractions(repo);

      assertNotNull(result, "Result should not be null.");
   }

   @Captor
   private ArgumentCaptor<LocalDate> dateCaptor;

   @Test
   @DisplayName("Should return the count of subs that made before a given date")
   void subsBeforeDate() {
      final LocalDate date = LocalDate.of(2025, 12, 13);
      when(repo.countByStartDateBefore(any(LocalDate.class)))
              .thenReturn(new TotalSubscriptions(3L));

      final TotalSubscriptions result = service.getSubscriptionsBefore(date);

      verify(repo, times(1)).countByStartDateBefore(dateCaptor.capture());
      verifyNoMoreInteractions(repo);

      final LocalDate dateValue = dateCaptor.getValue();

      assertNotNull(dateValue, "Date should not be null.");
      assertNotNull(result, "Result should not be null.");
   }

   @Test
   @DisplayName("Should return a list of subs by Memberships.")
   void subsByMemberships() {
      when(repo.countSubsByMembership())
              .thenReturn(List.of(new SubsPerMembership("MONTHLY", 2L)));

      final SubscriptionsPerMembershipResponse membershipCountList = service.getSubscriptionsPerMembership();

      verify(repo, times(1)).countSubsByMembership();
      verifyNoMoreInteractions(repo);

      assertNotNull(membershipCountList, "Response should not be null.");
   }

   @Test
   @DisplayName("Should return a list of subs by Month")
   void subsByMonth() {
      when(repo.countSubsByMonth())
              .thenReturn(List.of(new SubsPerMonthDto("December", 2L)));

      final SubscriptionsPerMonthResponse monthDtoList = service.getSubscriptionsPerMonth();

      verify(repo, times(1)).countSubsByMonth();
      verifyNoMoreInteractions(repo);

      assertNotNull(monthDtoList, "Response should not be null.");
   }
}
