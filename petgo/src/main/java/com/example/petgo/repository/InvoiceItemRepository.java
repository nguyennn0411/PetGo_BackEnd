package com.example.petgo.repository;

import com.example.petgo.entity.InvoiceItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvoiceItemRepository extends JpaRepository<InvoiceItem, Long> {
    List<InvoiceItem> findByInvoiceIdOrderBySortOrderAscIdAsc(Long invoiceId);
    void deleteByInvoiceId(Long invoiceId);
}
