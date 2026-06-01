package com.example.petgo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Getter
@Setter
public class Product extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_code", nullable = false, length = 32)
    private String productCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private ProductCategory category;

    @Column(nullable = false, length = 180)
    private String name;

    @Column(nullable = false, length = 190)
    private String slug;

    @Column(length = 120)
    private String brand;

    @Column(name = "short_description", length = 255)
    private String shortDescription;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "target_species", nullable = false, length = 20)
    private String targetSpecies = "ALL";

    @Column(name = "price_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal priceAmount;

    @Column(name = "sale_price_amount", precision = 12, scale = 2)
    private BigDecimal salePriceAmount;

    @Column(name = "currency_code", nullable = false, columnDefinition = "CHAR(3)")
    private String currencyCode = "VND";

    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity = 0;

    @Column(name = "sold_quantity", nullable = false)
    private Integer soldQuantity = 0;

    @Column(length = 100)
    private String sku;

    @Column(length = 100)
    private String barcode;

    @Column(name = "weight_gram")
    private Integer weightGram;

    @Column(name = "main_image_url", length = 500)
    private String mainImageUrl;

    @Column(name = "average_rating", nullable = false, precision = 3, scale = 2)
    private BigDecimal averageRating = BigDecimal.ZERO;

    @Column(name = "total_reviews", nullable = false)
    private Integer totalReviews = 0;

    @Column(name = "is_featured", nullable = false)
    private Boolean featured = false;

    @Column(name = "is_hot", nullable = false)
    private Boolean hot = false;

    @Column(name = "is_active", nullable = false)
    private Boolean active = true;

    @Column(nullable = false, length = 30)
    private String status = "ACTIVE";

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
