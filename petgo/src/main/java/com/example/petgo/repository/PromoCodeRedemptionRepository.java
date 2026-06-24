package com.example.petgo.repository;

import com.example.petgo.entity.PromoCodeRedemption;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PromoCodeRedemptionRepository extends JpaRepository<PromoCodeRedemption, Long> {
    long countByPromoCode_Id(Long promoCodeId);

    long countByPromoCode_IdAndUser_Id(Long promoCodeId, Long userId);

    boolean existsByInvoice_IdAndPromoCode_Id(Long invoiceId, Long promoCodeId);

    boolean existsByShippingBooking_IdAndPromoCode_Id(Long bookingId, Long promoCodeId);
}