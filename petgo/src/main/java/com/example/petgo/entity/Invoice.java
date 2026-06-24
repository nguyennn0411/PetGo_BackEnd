package com.example.petgo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "invoices")
@Getter
@Setter
public class Invoice extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "invoice_number", nullable = false, length = 32)
    private String invoiceNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_order_id")
    private ShopOrder shopOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "membership_subscription_id")
    private MembershipSubscription membershipSubscription;

    @Column(name = "invoice_type", nullable = false, length = 20)
    private String invoiceType;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "billing_name", nullable = false, length = 150)
    private String billingName;

    @Column(name = "billing_email", length = 190)
    private String billingEmail;

    @Column(name = "billing_phone", length = 30)
    private String billingPhone;

    @Column(name = "billing_address", length = 255)
    private String billingAddress;

    @Column(name = "subtotal_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotalAmount;

    @Column(name = "discount_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal discountAmount;

    @Column(name = "tax_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal taxAmount;

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "currency_code", nullable = false, length = 3)
    private String currencyCode;

    @Column(name = "issued_at")
    private LocalDateTime issuedAt;

    @Column(name = "due_at")
    private LocalDateTime dueAt;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(length = 255)
    private String note;
}
