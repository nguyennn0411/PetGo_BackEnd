package com.example.petgo.repository;

import com.example.petgo.entity.Payment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @EntityGraph(attributePaths = {"invoice", "invoice.booking", "payerUser"})
    Optional<Payment> findTopByInvoiceIdOrderByCreatedAtDesc(Long invoiceId);

    @EntityGraph(attributePaths = {"invoice", "invoice.booking", "payerUser"})
    Optional<Payment> findById(Long id);
}
