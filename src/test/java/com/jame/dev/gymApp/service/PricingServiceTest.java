package com.jame.dev.gymApp.service;

import com.jame.dev.gymApp.entity.MemberShipEntity;
import com.jame.dev.gymApp.entity.PricingEntity;
import com.jame.dev.gymApp.repository.PricingRepository;
import com.jame.dev.gymApp.service.out.PricingServiceImplementation;
import com.jame.dev.gymApp.shared.enums.Membership;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PricingServiceTest {

   @Mock
   private PricingRepository repo;

   @InjectMocks
   private PricingServiceImplementation service;

   private final MemberShipEntity memberShipEntityTest = MemberShipEntity.builder()
           .id(1)
           .membership(Membership.MONTHLY)
           .build();
   private final PricingEntity pricingTest = PricingEntity.builder()
           .id(1)
           .memberShipEntity(memberShipEntityTest)
           .price(BigDecimal.valueOf(300.00d))
           .build();

   @Test
   @DisplayName("Get All")
   void getAll() {
      when(repo.findAll()).thenReturn(List.of(this.pricingTest));
      final List<PricingEntity> pricingList = service.getAll();

      assertFalse(pricingList.isEmpty(), "List should not be empty.");
      assertTrue(pricingList.contains(this.pricingTest), "List should contains 'pricingTest'.");
      assertEquals(1, pricingList.size(), "The list length should be 1 in this case.");
      assertNotNull(pricingList.getFirst(), "The first element should not be null.");
      assertEquals(pricingList.getFirst(), this.pricingTest, "The elements should be the same.");

      verify(repo, atLeastOnce()).findAll();
      verifyNoMoreInteractions(repo);
   }

   @Test
   @DisplayName("Save Pricing")
   void save() {
      final PricingEntity pricing = new PricingEntity(1, pricingTest.getMemberShipEntity(), pricingTest.getPrice());
      when(repo.save(any(PricingEntity.class)))
              .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

      final PricingEntity pricingAdded = service.save(pricing);

      final ArgumentCaptor<PricingEntity> captor = ArgumentCaptor.forClass(PricingEntity.class);
      verify(repo, atLeastOnce()).save(captor.capture());
      verifyNoMoreInteractions(repo);

      final PricingEntity pricingSaved = captor.getValue();

      assertNotNull(pricingSaved, "The pricingSaved should not be null.");
      assertEquals(pricingSaved, pricingAdded, "The entities should be the same.");
   }

   @Test
   @DisplayName("Find by id.")
   void findById() {
      final Integer id = pricingTest.getId();
      when(repo.findById(id)).thenReturn(Optional.of(pricingTest));

      final Optional<PricingEntity> optionalPricing = service.getById(id);
      final PricingEntity pricing = optionalPricing.orElseThrow();

      verify(repo, atLeastOnce()).findById(id);
      verifyNoMoreInteractions(repo);

      assertNotEquals(Optional.empty(), optionalPricing, "The return value should not be Empty.");
      assertNotNull(pricing, "Should not be null.");
      assertEquals(this.pricingTest, pricing, "The Objects should be the same.");
   }
}