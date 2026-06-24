package com.example.petgo.service;

import com.example.petgo.entity.Invoice;
import com.example.petgo.entity.MembershipPlan;
import com.example.petgo.entity.MembershipSubscription;
import com.example.petgo.entity.PromoCode;
import com.example.petgo.entity.ShippingBooking;
import com.example.petgo.entity.User;

import java.math.BigDecimal;
import java.util.List;

public interface PromotionPolicyService {
    PromoPreview previewForMembership(User user, MembershipPlan plan, String rawPromoCode);

    void recordMembershipRedemption(PromoPreview preview, User user, MembershipSubscription subscription,
            Invoice invoice);

    PromoPreview previewForBooking(BigDecimal priceAmount, BigDecimal shippingFee, User user, String rawPromoCode,
            Long areaId, List<Long> serviceCategoryIds);

    void recordBookingRedemption(PromoPreview preview, User user, ShippingBooking booking);

    BigDecimal calculateTotal(BigDecimal subtotal, BigDecimal discount, BigDecimal tax);

    record PromoPreview(PromoCode promoCode, String appliedCode, BigDecimal discountAmount, String message) {
        public boolean applied() {
            return promoCode != null && appliedCode != null;
        }
    }
}
