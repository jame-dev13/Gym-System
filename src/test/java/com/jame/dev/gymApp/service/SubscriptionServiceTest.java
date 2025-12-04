package com.jame.dev.gymApp.service;

import com.jame.dev.gymApp.entity.*;
import com.jame.dev.gymApp.exception.NoOperationException;
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
import org.junit.jupiter.api.Assertions;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
           .finished(false)
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
           (s.getCustomer() != null && s.getPricing() != null) && (s.getActive() && !s.getFinished());

   @Test
   @DisplayName("Get All Subscriptions")
   void getAllSubs() {
      when(repo.findAll()).thenReturn(testSubsList);
      List<SubscriptionEntity> subs = service.getAll();
      assertAll("Test to non nullity and emptiness, contains, matching and equality.",
              () -> assertNotNull(subs, "Returning list should not be null."),
              () -> assertFalse(subs.isEmpty(), "Returning list should not be empty."),
              () -> assertTrue(subs.contains(testSubsList.getFirst()), "Should contain the first element."),
              () -> assertTrue(subs.stream().allMatch(s -> (s.getCustomer() != null && s.getPricing() != null)),
                      "Should match with the given predicate."),
              () -> assertEquals(subs, testSubsList, "List should be equals."),
              () -> assertSame(subs.getFirst(), testSubsList.getFirst(), "The first element should be the same."),
              () -> assertSame(subs.getLast(), testSubsList.getLast(), "The last element should be the same.")
      );
      verify(repo).findAll();
   }

   @Test
   @DisplayName("Get all Actives")
   void getAllActives() {
      when(repo.findAllByActiveTrue()).thenReturn(testSubsList);
      List<SubscriptionEntity> subs = service.getActives();

      assertAll("Test to non nullity and emptiness, contains, matching and equality.",
              () -> assertNotNull(subs, "Returning list should not be null."),
              () -> assertFalse(subs.isEmpty(), "Returning list should not be empty."),
              () -> assertTrue(subs.contains(testSubsList.getFirst()), "Should contain the first element."),
              () -> assertTrue(subs.stream().allMatch(matcher), "Should match with the give predicate."),
              () -> assertEquals(subs, testSubsList, "List should be equals."),
              () -> assertSame(subs.getFirst(), testSubsList.getFirst(), "The first element should be the same."),
              () -> assertSame(subs.getLast(), testSubsList.getLast(), "The last element should be the same.")
      );
      verify(repo).findAllByActiveTrue();
   }

   @Test
   @DisplayName("Get Page with actives only")
   void getPageByActive() {
      Pageable pageable = PageRequest.of(0, 5, sort);
      List<SubscriptionEntity> subList = testSubsList.subList(0, 5);
      when(repo.findAllByActiveTrue(pageable)).thenReturn(new PageImpl<>(subList));

      Page<@NonNull SubscriptionEntity> page = service.getPageOfActives(pageable);
      List<SubscriptionEntity> pageContent = page.getContent();
      System.out.println(pageContent);
      assertAll("Test to non nullity and emptiness, contains, matching and equality.",
              () -> assertNotNull(page, "Returning list should not be null."),
              () -> assertFalse(page.isEmpty() && !page.hasContent(), "Returning list should not be empty."),
              () -> assertTrue(pageContent.stream().allMatch(matcher), "Should match with the give predicate."),
              () -> assertEquals(pageContent, subList, "List should be equals."),
              () -> assertSame(pageContent.getFirst(), subList.getFirst(), "The first element should be the same."),
              () -> assertSame(pageContent.getLast(), subList.getLast(), "The last element should be the same.")
      );
      verify(repo).findAllByActiveTrue(pageable);
   }

   @Test
   @DisplayName("Next Page")
   void nextPage() {
      Pageable pageable = PageRequest.of(1, 5, sort);
      List<SubscriptionEntity> subList = testSubsList.subList(5, 9);
      when(repo.findAllByActiveTrue(pageable)).thenReturn(new PageImpl<>(subList));

      Page<@NonNull SubscriptionEntity> page = service.getPageOfActives(pageable);
      List<SubscriptionEntity> pageContent = page.getContent();
      System.out.println(pageContent);
      assertAll("Test to non nullity and emptiness, contains, matching and equality.",
              () -> assertNotNull(page, "Returning list should not be null."),
              () -> assertFalse(page.isEmpty() && !page.hasContent(), "Returning list should not be empty."),
              () -> assertTrue(pageContent.stream().allMatch(matcher), "Should match with the give predicate."),
              () -> assertEquals(pageContent, subList, "List should be equals."),
              () -> assertSame(pageContent.getFirst(), subList.getFirst(), "The first element should be the same."),
              () -> assertSame(pageContent.getLast(), subList.getLast(), "The last element should be the same.")
      );
      verify(repo).findAllByActiveTrue(pageable);
   }

   @Test
   @DisplayName("Get by id")
   void getById() {
      when(repo.findById(idSubscriptionTest)).thenReturn(Optional.of(this.subscriptionEntityTest));
      var optionalSubs = service.getById(idSubscriptionTest);
      verify(repo).findById(idSubscriptionTest);

      Assertions.assertNotEquals(Optional.empty(), optionalSubs, "Should not be an Optional empty.");
      Assertions.assertDoesNotThrow(optionalSubs::get, "Should doesn't throw Exception");
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

      SubscriptionEntity subscriptionAdded = service.save(this.dtoTest);

      ArgumentCaptor<SubscriptionEntity> captor = ArgumentCaptor.forClass(SubscriptionEntity.class);
      verify(repo).existsByCustomer_IdAndActiveTrue(customerEntityTest.getId());
      verify(customerRepo).findById(idCustomerTest);
      verify(pricingRepo).findById(idPricingTest);
      verify(subscriptionMapper)
              .toEntity(eq(dtoTest), eq(customerEntityTest), eq(pricingEntityTest), anyList());
      verify(repo).save(captor.capture());

      SubscriptionEntity subscriptionSaved = captor.getValue();

      assertAll("Not null, and equals.",
              () -> Assertions.assertNotNull(subscriptionSaved, "Should not be null."),
              () -> Assertions.assertEquals(subscriptionAdded, subscriptionSaved, "Should be the same object.")
      );
   }

   @Test
   void update() {
      Assertions.assertThrows(NoOperationException.class,
              () -> service.update(idSubscriptionTest, dtoTest), "Should throw the Exception.");
   }

   @Test
   void finalizeSubscription() {
      when(repo.findById(idSubscriptionTest)).thenReturn(Optional.of(subscriptionEntityTest));
      when(repo.save(any(SubscriptionEntity.class)))
              .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

      service.finalizeSubscription(idSubscriptionTest);

      ArgumentCaptor<SubscriptionEntity> captor = ArgumentCaptor.forClass(SubscriptionEntity.class);
      verify(repo).findById(idSubscriptionTest);
      verify(repo).save(captor.capture());

      SubscriptionEntity finalizedSubscription = captor.getValue();

      assertAll("",
              () -> Assertions.assertNotNull(finalizedSubscription, "Should not be null."),
              () -> Assertions.assertNotEquals(false, finalizedSubscription.getFinished(),
                      "Should be finalized."));
   }

   @Test
   void softDeleteById() {
      service.softDeleteById(idSubscriptionTest);
      verify(repo).softDelete(idSubscriptionTest);
   }
}