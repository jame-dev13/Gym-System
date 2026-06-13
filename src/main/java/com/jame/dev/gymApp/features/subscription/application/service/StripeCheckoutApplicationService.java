package com.jame.dev.gymApp.features.subscription.application.service;

import com.jame.dev.gymApp.features.subscription.api.request.CheckoutRequest;
import com.jame.dev.gymApp.features.subscription.api.response.SubscriptionCheckoutResponse;
import com.jame.dev.gymApp.features.subscription.application.contract.StripeCheckoutService;
import com.jame.dev.gymApp.features.subscription.domain.exception.PricingNotFoundException;
import com.jame.dev.gymApp.features.subscription.domain.exception.StripeSessionCreationException;
import com.jame.dev.gymApp.features.subscription.domain.model.Membership;
import com.jame.dev.gymApp.features.subscription.domain.model.PricingEntity;
import com.jame.dev.gymApp.features.subscription.infrastructure.persistence.PricingRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class StripeCheckoutApplicationService implements StripeCheckoutService {

    private final PricingRepository pricingRepository;

    @Value("${stripe.success-url}")
    private String successUrl;

    @Value("${stripe.cancel-url}")
    private String cancelUrl;

    @Override
    @Transactional(readOnly = true)
    public SubscriptionCheckoutResponse createCheckoutSession(CheckoutRequest request) {
        final PricingEntity pricing = pricingRepository
            .findByMemberShipEntity_Membership(request.membership())
            .orElseThrow(() -> new PricingNotFoundException("Pricing not found for membership: " + request.membership()));

        final Session session = buildStripeSession(pricing, request.membership(), request.customerEmail());

        return SubscriptionCheckoutResponse.builder()
            .sessionId(session.getId())
            .sessionUrl(session.getUrl())
            .build();
    }

    private Session buildStripeSession(PricingEntity pricing, Membership membership, String customerEmail) {
        final long unitAmount = pricing.getPrice().multiply(BigDecimal.valueOf(100)).longValue();

        final SessionCreateParams.LineItem.PriceData.ProductData productData =
            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                .setName("Gym membership - " + membership.name())
                .build();

        final SessionCreateParams.LineItem.PriceData.Recurring recurring =
            SessionCreateParams.LineItem.PriceData.Recurring.builder()
                .setInterval(toStripeInterval(membership))
                .setIntervalCount(toIntervalCount(membership))
                .build();

        final SessionCreateParams.LineItem.PriceData priceData =
            SessionCreateParams.LineItem.PriceData.builder()
                .setCurrency("mxn")
                .setUnitAmount(unitAmount)
                .setProductData(productData)
                .setRecurring(recurring)
                .build();

        final SessionCreateParams.LineItem lineItem =
            SessionCreateParams.LineItem.builder()
                .setQuantity(1L)
                .setPriceData(priceData)
                .build();

        final SessionCreateParams params = SessionCreateParams.builder()
            .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
            .setSuccessUrl(successUrl)
            .setCancelUrl(cancelUrl)
            .addLineItem(lineItem)
            .setCustomerEmail(customerEmail)
            .putMetadata("pricingId", pricing.getId().toString())
            .putMetadata("customerEmail", customerEmail)
            .build();

        try {
            return Session.create(params);
        } catch (StripeException e) {
            throw new StripeSessionCreationException("Failed to create Stripe checkout session", e);
        }
    }

    private SessionCreateParams.LineItem.PriceData.Recurring.Interval toStripeInterval(Membership membership) {
        return switch (membership) {
            case BIWEEKLY -> SessionCreateParams.LineItem.PriceData.Recurring.Interval.WEEK;
            case MONTHLY, QUARTERLY -> SessionCreateParams.LineItem.PriceData.Recurring.Interval.MONTH;
           case ANNUAL -> SessionCreateParams.LineItem.PriceData.Recurring.Interval.YEAR;
        };
    }

    private long toIntervalCount(Membership membership) {
        return switch (membership) {
            case BIWEEKLY -> 2;
            case MONTHLY, ANNUAL -> 1;
            case QUARTERLY -> 3;
        };
    }
}
