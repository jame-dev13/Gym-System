package com.jame.dev.gymApp.service;

import com.jame.dev.gymApp.entity.*;
import com.jame.dev.gymApp.mapper.SubscriptionMapper;
import com.jame.dev.gymApp.model.dto.in.SubscriptionDtoInput;
import com.jame.dev.gymApp.repository.CustomerRepository;
import com.jame.dev.gymApp.repository.PricingRepository;
import com.jame.dev.gymApp.repository.SubscriptionRepository;
import com.jame.dev.gymApp.service.out.SubscriptionServiceImplementation;
import com.jame.dev.gymApp.shared.enums.Membership;
import com.jame.dev.gymApp.shared.enums.Period;
import com.jame.dev.gymApp.shared.enums.Role;
import lombok.NonNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {
   @Mock
   private SubscriptionRepository repo;
   @Mock
   private CustomerRepository customerRepo;
   @Mock
   private PricingRepository pricingRepo;
   @Mock
   private SubscriptionMapper subscriptionMapper;
   @InjectMocks
   private SubscriptionServiceImplementation service;

   private final Sort sort = Sort.sort(SubscriptionEntity.class).by(SubscriptionEntity::getId);
   private final MemberShipEntity MEMBERSHIP_TEST = new MemberShipEntity(1, Membership.MONTHLY);
   private final PricingEntity pricingEntityTest = new PricingEntity(1, MEMBERSHIP_TEST, BigDecimal.valueOf(300.00));
   private final UserEntity USER_TEST = UserEntity.builder()
           .id(1L)
           .name("userTest")
           .email("test@mail.com")
           .password("testSecret123")
           .roles(Set.of(new RoleEntity(null, Role.ADMIN)))
           .active(true)
           .build();
   private final CustomerEntity customerEntityTest = new CustomerEntity(1L, USER_TEST, "18392434", true);
   private final SubscriptionDtoInput dtoTest = SubscriptionDtoInput.builder()
           .customerId(customerEntityTest.getId())
           .pricingId(pricingEntityTest.getId())
           .build();
   private final SubscriptionEntity subscriptionEntityTest = SubscriptionEntity.builder()
           .id(1L)
           .customer(customerEntityTest)
           .pricing(pricingEntityTest)
           .subscriptionPeriods(List.of(new PeriodEntity(Period.MONTHLY, LocalDate.now())))
           .active(true)
           .finished(false)
           .build();
   private final Long idSubscriptionTest = subscriptionEntityTest.getId();
   private final Long idCustomerTest = customerEntityTest.getId();
   private final Integer idPricingTest = pricingEntityTest.getId();

   private final List<SubscriptionEntity> testSubsList = IntStream.range(0, 10)
           .mapToObj(i -> {
              CustomerEntity customer = new CustomerEntity((long) (i + 1), new UserEntity(), "32472525" + i, true);
              PricingEntity pricing = new PricingEntity((i + 1), new MemberShipEntity((i + 1), Membership.MONTHLY), BigDecimal.valueOf(300.00d));
              return SubscriptionEntity.builder()
                      .id((long) (i + 1))
                      .customer(customer)
                      .pricing(pricing)
                      .subscriptionPeriods(List.of(new PeriodEntity()))
                      .active(true)
                      .finished(false)
                      .build();
           })
           .sorted(Comparator.comparing(SubscriptionEntity::getId).reversed())
           .toList();
   private final Predicate<SubscriptionEntity> matcher = s ->
           (s.getCustomer() != null && s.getPricing() != null) && (s.isActive() && !s.isFinished());


   @Test
   @DisplayName("Should Get Page of subscriptions")
   void getPageByActive() {
      final Pageable pageable = PageRequest.of(0, 5, sort);
      final List<SubscriptionEntity> subList = testSubsList.subList(0, 5);
      when(repo.findAllByActiveTrue(pageable)).thenReturn(new PageImpl<>(subList));

      final Page<@NonNull SubscriptionEntity> page = service.getPage(pageable);
      final List<SubscriptionEntity> pageContent = page.getContent();
      System.out.println(pageContent);

      assertAll("Test to non nullity and emptiness, contains, matching and equality.",
              () -> assertNotNull(page, "Returning list should not be null."),
              () -> assertFalse(page.isEmpty() && !page.hasContent(), "Returning list should not be empty."),
              () -> assertTrue(pageContent.stream().allMatch(matcher), "Should match with the give predicate."),
              () -> assertEquals(pageContent, subList, "List should be equals."),
              () -> assertSame(pageContent.getFirst(), subList.getFirst(), "The first element should be the same."),
              () -> assertSame(pageContent.getLast(), subList.getLast(), "The last element should be the same.")
      );
      verify(repo, atLeastOnce()).findAllByActiveTrue(pageable);
      verifyNoMoreInteractions(repo);
   }

   @Test
   @DisplayName("Should Get subscription by id")
   void getById() {
      when(repo.findById(idSubscriptionTest)).thenReturn(Optional.of(this.subscriptionEntityTest));
      final Optional<SubscriptionEntity> optionalSubs = service.getById(idSubscriptionTest);

      verify(repo, atLeastOnce()).findById(idSubscriptionTest);
      verifyNoMoreInteractions(repo);
      assertNotEquals(Optional.empty(), optionalSubs, "Should not be an Optional empty.");
      assertDoesNotThrow(optionalSubs::get, "Should doesn't throw Exception");
   }

   @Test
   @DisplayName("Save subscription")
   void save() {
      when(repo.existsByCustomer_IdAndActiveTrue(customerEntityTest.getId())).thenReturn(false);
      when(customerRepo.findById(idCustomerTest))
              .thenReturn(Optional.of(this.customerEntityTest));
      when(pricingRepo.findById(idPricingTest))
              .thenReturn(Optional.of(this.pricingEntityTest));
      when(subscriptionMapper.toEntity(
              eq(dtoTest),
              eq(customerEntityTest),
              eq(pricingEntityTest),
              anyList()
      )).thenReturn(subscriptionEntityTest);
      when(repo.save(any(SubscriptionEntity.class)))
              .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

      final SubscriptionEntity subscriptionAdded = service.save(this.dtoTest);

      final ArgumentCaptor<SubscriptionEntity> captor = ArgumentCaptor.forClass(SubscriptionEntity.class);

      verify(repo, atLeastOnce()).existsByCustomer_IdAndActiveTrue(customerEntityTest.getId());
      verify(customerRepo, atLeastOnce()).findById(idCustomerTest);
      verify(pricingRepo, atLeastOnce()).findById(idPricingTest);
      verify(subscriptionMapper, atLeastOnce())
              .toEntity(eq(dtoTest), eq(customerEntityTest), eq(pricingEntityTest), anyList());
      verify(repo, atLeastOnce()).save(captor.capture());
      verifyNoMoreInteractions(repo);

      final SubscriptionEntity subscriptionSaved = captor.getValue();

      assertAll("Not null, and equals.",
              () -> assertNotNull(subscriptionSaved, "Should not be null."),
              () -> assertEquals(subscriptionAdded, subscriptionSaved, "Should be the same object.")
      );
   }

   @Test
   @DisplayName("Should update the SubscriptionEntity")
   void update() {
      when(repo.findById(idSubscriptionTest)).thenReturn(Optional.of(subscriptionEntityTest));
      when(repo.save(any(SubscriptionEntity.class)))
              .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

      final SubscriptionEntity subscription = service.patch(idSubscriptionTest);

      final ArgumentCaptor<SubscriptionEntity> captor = ArgumentCaptor.forClass(SubscriptionEntity.class);

      verify(repo, atLeastOnce()).findById(idSubscriptionTest);
      verify(repo, atLeastOnce()).save(captor.capture());
      verifyNoMoreInteractions(repo);

      final SubscriptionEntity finalizedSubscription = captor.getValue();

      assertAll("",
              () -> assertNotNull(finalizedSubscription, "Should not be null."),
              () -> assertNotEquals(false, finalizedSubscription.isFinished(),
                      "Should be finalized."));
   }

   @Test
   void softDeleteById() {
      service.softDelete(idSubscriptionTest);
      verify(repo, times(1)).softDelete(idSubscriptionTest);
   }
}