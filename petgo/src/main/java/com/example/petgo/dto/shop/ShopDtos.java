package com.example.petgo.dto.shop;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public final class ShopDtos {

    public record CategoryResponse(Long id, String name, String slug, String iconKey, String description, Integer sortOrder, Boolean active) {}

    public record ProductResponse(
            Long id, String productCode, Long categoryId, String categoryName, String name, String slug, String brand,
            String shortDescription, String description, String targetSpecies, BigDecimal priceAmount,
            BigDecimal salePriceAmount, String currencyCode, Integer stockQuantity, Integer soldQuantity,
            String sku, String barcode, Integer weightGram, String mainImageUrl, BigDecimal averageRating,
            Integer totalReviews, Boolean featured, Boolean hot, Boolean active, String status
    ) {}

    public record ProductUpsertRequest(
            @NotNull Long categoryId,
            String productCode,
            @NotBlank String name,
            String slug,
            String brand,
            String shortDescription,
            String description,
            String targetSpecies,
            @NotNull BigDecimal priceAmount,
            BigDecimal salePriceAmount,
            Integer stockQuantity,
            String sku,
            String barcode,
            Integer weightGram,
            String mainImageUrl,
            Boolean featured,
            Boolean hot,
            Boolean active,
            String status
    ) {}

    public record CartItemRequest(@NotNull Long userId, @NotNull Long productId, @Min(1) Integer quantity) {}
    public record CartItemUpdateRequest(@Min(1) Integer quantity) {}
    public record CartItemResponse(Long id, Long productId, String productName, String productSlug, String productImageUrl, Integer quantity, BigDecimal unitPrice, BigDecimal lineTotal) {}
    public record CartResponse(Long userId, List<CartItemResponse> items, BigDecimal subtotalAmount, BigDecimal shippingFeeAmount, BigDecimal discountAmount, BigDecimal taxAmount, BigDecimal totalAmount) {}

    public record CheckoutRequest(
            @NotNull Long userId,
            @NotBlank String receiverName,
            @NotBlank String receiverPhone,
            String receiverEmail,
            @NotBlank String shippingAddress,
            String ward,
            String district,
            String city,
            String province,
            String paymentMethod,
            String customerNote
    ) {}

    public record OrderItemResponse(Long id, Long productId, String productName, String productSku, String productImageUrl, Integer quantity, BigDecimal unitPrice, BigDecimal lineTotal) {}
    public record OrderResponse(Long id, String orderCode, Long customerUserId, String receiverName, String receiverPhone, String receiverEmail, String shippingAddress, String ward, String district, String city, String province, String status, String paymentMethod, BigDecimal subtotalAmount, BigDecimal shippingFeeAmount, BigDecimal discountAmount, BigDecimal taxAmount, BigDecimal totalAmount, String currencyCode, LocalDateTime createdAt, String checkoutUrl, List<OrderItemResponse> items) {}
    public record OrderStatusUpdateRequest(@NotBlank String status, Long changedByUserId, String note) {}
}
