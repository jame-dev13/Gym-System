package com.jame.dev.gymApp.mapper;

import com.jame.dev.gymApp.features.customer.domain.model.CustomerEntity;
import com.jame.dev.gymApp.features.subscription.api.response.SubscriptionResponse;
import com.jame.dev.gymApp.features.subscription.application.support.mapper.PeriodMapper;
import com.jame.dev.gymApp.features.subscription.application.support.mapper.PeriodMapperImpl;
import com.jame.dev.gymApp.features.subscription.application.support.mapper.SubscriptionMapper;
import com.jame.dev.gymApp.features.subscription.application.support.mapper.SubscriptionMapperImpl;
import com.jame.dev.gymApp.features.subscription.domain.model.*;
import com.jame.dev.gymApp.features.user.domain.model.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SubscriptionMapperTest {
   private final PeriodMapper periodMapper = new PeriodMapperImpl();
   private final SubscriptionMapper subscriptionMapper =
           new SubscriptionMapperImpl();
   private CustomerEntity customer;
   private PricingEntity pricing;
   private SubscriptionEntity subs;

   @BeforeEach
   void setup() {
      this.customer = CustomerEntity.builder()
              .user(new UserEntity())
              .phoneContact("3926441")
              .build();
      this.pricing = PricingEntity.builder()
              .id(1)
              .memberShipEntity(new MemberShipEntity(1, Membership.MONTHLY))
              .price(BigDecimal.valueOf(300.00d))
              .build();
      this.subs = SubscriptionEntity.builder()
              .customer(customer)
              .pricing(pricing)
              .subscriptionPeriods(List.of(new PeriodEntity()))
              .status(SubscriptionStatus.PAID)
              .build();
   }

   @Test
   @DisplayName("To Dto")
   void toDto() {
      final SubscriptionResponse dto = subscriptionMapper.toDto(this.subs);
      final Membership membership = this.subs.getPricing().getMemberShipEntity().getMembership();
      final BigDecimal price = this.subs.getPricing().getPrice();
      assertAll("Not null, properties equals and not finished.",
              () -> assertNotNull(dto, "Should not be null."),
              () -> assertEquals(price, dto.price(), "Should be the same."),
              () -> assertEquals(membership, dto.membership(), "Should be the same."),
              () -> assertEquals(SubscriptionStatus.PAID, dto.status(), "Status should be equals."));
   }

   @Test
   @DisplayName("To Entity")
   void toEntity() {
      final List<PeriodEntity> periods = new ArrayList<>();

      SubscriptionEntity subs = subscriptionMapper.toEntity(customer, pricing, periods);
      assertAll("Not null, properties equals and not finished.",
              () -> assertNotNull(subs, "Should not be null."),
              () -> assertEquals(customer, subs.getCustomer(), "Should be the same."),
              () -> assertEquals(pricing, subs.getPricing(), "Should be the same."),
              () -> assertEquals(periods, subs.getSubscriptionPeriods(), "Should be the same."),
              () -> assertEquals(SubscriptionStatus.PAID, subs.getStatus(), "Status should be equals."));
   }
}