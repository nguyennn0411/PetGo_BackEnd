package com.example.petgo.service;

import com.example.petgo.entity.Booking;
import com.example.petgo.entity.Invoice;
import com.example.petgo.entity.MembershipPlan;
import com.example.petgo.entity.MembershipSubscription;
import com.example.petgo.entity.PromoCode;
import com.example.petgo.entity.User;

import java.math.BigDecimal;

public interface PromotionPolicyService {
    PromoPreview previewForBooking(Booking booking, String rawPromoCode);

    PromoPreview previewForMembership(User user, MembershipPlan plan, String rawPromoCode);

    void recordBookingRedemption(PromoPreview preview, User user, Booking booking, Invoice invoice);

    void recordMembershipRedemption(PromoPreview preview, User user, MembershipSubscription subscription,
            Invoice invoice);

    BigDecimal calculateTotal(BigDecimal subtotal, BigDecimal discount, BigDecimal tax);

    record PromoPreview(PromoCode promoCode, String appliedCode, BigDecimal discountAmount, String message) {
        public boolean applied() {
            return promoCode != null && appliedCode != null;
        }
    }
}