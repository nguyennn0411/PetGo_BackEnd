package com.example.petgo.service.partner.impl;

import com.example.petgo.dto.InvoiceItemResponse;
import com.example.petgo.dto.partner.PartnerInvoiceDetailResponse;
import com.example.petgo.dto.partner.PartnerInvoiceListResponse;
import com.example.petgo.dto.partner.PartnerInvoiceSummaryResponse;
import com.example.petgo.dto.partner.PartnerRevenueByServiceResponse;
import com.example.petgo.dto.partner.PartnerRevenueSummaryResponse;
import com.example.petgo.entity.Booking;
import com.example.petgo.entity.Invoice;
import com.example.petgo.entity.InvoiceItem;
import com.example.petgo.entity.Payment;
import com.example.petgo.entity.ProviderProfile;
import com.example.petgo.entity.ProviderService;
import com.example.petgo.exception.BadRequestException;
import com.example.petgo.exception.ResourceNotFoundException;
import com.example.petgo.repository.InvoiceItemRepository;
import com.example.petgo.repository.InvoiceRepository;
import com.example.petgo.repository.PaymentRepository;
import com.example.petgo.service.partner.PartnerAccessService;
import com.example.petgo.service.partner.PartnerMappingSupport;
import com.example.petgo.service.partner.PartnerRevenueService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PartnerRevenueServiceImpl implements PartnerRevenueService {

    private final PartnerAccessService partnerAccessService;
    private final PartnerMappingSupport mapper;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceItemRepository invoiceItemRepository;
    private final PaymentRepository paymentRepository;

    @Override
    @Transactional(readOnly = true)
    public PartnerRevenueSummaryResponse getRevenueSummary(HttpServletRequest request, String from, String to) {
        ProviderProfile provider = partnerAccessService.requirePartnerContext(request).provider();
        DateRange range = resolveRange(from, to, true);
        List<InvoiceWithPayment> rows = loadInvoiceRows(provider.getId()).stream()
                .filter(row -> matchesDateRange(row.invoice(), range))
                .toList();

        List<InvoiceWithPayment> paidRows = rows.stream()
                .filter(this::isPaidInvoice)
                .toList();
        List<InvoiceWithPayment> pendingRows = rows.stream()
                .filter(this::isPendingInvoice)
                .toList();

        BigDecimal totalRevenue = sumTotal(paidRows);
        BigDecimal pendingRevenue = sumTotal(pendingRows);
        BigDecimal averageBookingValue = paidRows.isEmpty()
                ? BigDecimal.ZERO
                : totalRevenue.divide(BigDecimal.valueOf(paidRows.size()), 2, RoundingMode.HALF_UP);

        long totalBookings = rows.stream()
                .map(row -> row.invoice().getBooking())
                .filter(Objects::nonNull)
                .map(Booking::getId)
                .filter(Objects::nonNull)
                .distinct()
                .count();
        long completedBookings = rows.stream()
                .map(row -> row.invoice().getBooking())
                .filter(Objects::nonNull)
                .filter(booking -> "COMPLETED".equalsIgnoreCase(mapper.firstNonBlank(booking.getStatus(), "")))
                .map(Booking::getId)
                .filter(Objects::nonNull)
                .distinct()
                .count();

        return PartnerRevenueSummaryResponse.builder()
                .providerId(provider.getId())
                .businessName(provider.getBusinessName())
                .fromDate(mapper.formatIsoDate(range.from()))
                .toDate(mapper.formatIsoDate(range.to()))
                .totalRevenue(totalRevenue)
                .totalRevenueDisplay(mapper.formatMoney(totalRevenue))
                .pendingRevenue(pendingRevenue)
                .pendingRevenueDisplay(mapper.formatMoney(pendingRevenue))
                .averageBookingValue(averageBookingValue)
                .averageBookingValueDisplay(mapper.formatMoney(averageBookingValue))
                .invoiceCount(rows.size())
                .paidInvoices(paidRows.size())
                .pendingInvoices(pendingRows.size())
                .totalBookings(totalBookings)
                .completedBookings(completedBookings)
                .revenueByService(buildRevenueByService(paidRows))
                .recentInvoices(rows.stream().limit(8).map(this::mapInvoiceSummary).toList())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PartnerInvoiceListResponse listInvoices(HttpServletRequest request, String from, String to, String status) {
        ProviderProfile provider = partnerAccessService.requirePartnerContext(request).provider();
        DateRange range = resolveRange(from, to, false);
        String normalizedStatus = normalizeStatus(status);
        List<InvoiceWithPayment> rows = loadInvoiceRows(provider.getId()).stream()
                .filter(row -> matchesDateRange(row.invoice(), range))
                .toList();

        Map<String, Long> counts = new LinkedHashMap<>();
        counts.put("ALL", (long) rows.size());
        counts.put("PAID", rows.stream().filter(this::isPaidInvoice).count());
        counts.put("PENDING", rows.stream().filter(this::isPendingInvoice).count());
        counts.put("ISSUED", rows.stream().filter(row -> statusEquals(row.invoice().getStatus(), "ISSUED")).count());
        counts.put("CANCELLED",
                rows.stream().filter(row -> statusEquals(row.invoice().getStatus(), "CANCELLED")).count());

        return PartnerInvoiceListResponse.builder()
                .providerId(provider.getId())
                .filterStatus(normalizedStatus)
                .fromDate(mapper.formatIsoDate(range.from()))
                .toDate(mapper.formatIsoDate(range.to()))
                .counts(counts)
                .invoices(rows.stream()
                        .filter(row -> matchesStatus(row, normalizedStatus))
                        .map(this::mapInvoiceSummary)
                        .toList())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PartnerInvoiceDetailResponse getInvoiceDetail(HttpServletRequest request, Long invoiceId) {
        ProviderProfile provider = partnerAccessService.requirePartnerContext(request).provider();
        Invoice invoice = invoiceRepository.findDetailedByProviderIdAndInvoiceId(provider.getId(), invoiceId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Không tìm thấy invoice thuộc nhà cung cấp hiện tại."));
        Payment payment = latestPayment(invoice.getId());
        List<InvoiceItemResponse> items = invoiceItemRepository.findByInvoiceIdOrderBySortOrderAscIdAsc(invoice.getId())
                .stream()
                .map(this::mapItem)
                .toList();
        Booking booking = invoice.getBooking();

        return PartnerInvoiceDetailResponse.builder()
                .invoiceId(invoice.getId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .invoiceStatus(invoice.getStatus())
                .invoiceType(invoice.getInvoiceType())
                .issuedAt(formatDateTime(invoice.getIssuedAt()))
                .paidAt(formatDateTime(invoice.getPaidAt()))
                .bookingId(booking != null ? booking.getId() : null)
                .bookingCode(booking != null ? booking.getBookingCode() : null)
                .customerUserId(
                        booking != null && booking.getCustomerUser() != null ? booking.getCustomerUser().getId() : null)
                .customerName(resolveCustomerName(invoice))
                .customerPhone(resolveCustomerPhone(invoice))
                .customerEmail(resolveCustomerEmail(invoice))
                .providerServiceId(resolveProviderServiceId(booking))
                .serviceName(booking != null ? booking.getServiceNameSnapshot() : null)
                .petName(booking != null ? booking.getPetNameSnapshot() : null)
                .appointmentDate(booking != null ? mapper.formatIsoDate(booking.getAppointmentDate()) : null)
                .appointmentDateDisplay(booking != null ? mapper.formatDate(booking.getAppointmentDate()) : null)
                .appointmentTime(
                        booking != null ? mapper.formatTimeRange(booking.getStartTime(), booking.getEndTime()) : null)
                .subtotalAmount(mapper.defaultMoney(invoice.getSubtotalAmount()))
                .discountAmount(mapper.defaultMoney(invoice.getDiscountAmount()))
                .taxAmount(mapper.defaultMoney(invoice.getTaxAmount()))
                .totalAmount(mapper.defaultMoney(invoice.getTotalAmount()))
                .totalAmountDisplay(mapper.formatMoney(invoice.getTotalAmount()))
                .currencyCode(mapper.firstNonBlank(invoice.getCurrencyCode(), "VND"))
                .paymentId(payment != null ? payment.getId() : null)
                .paymentCode(payment != null ? payment.getPaymentCode() : null)
                .paymentMethod(payment != null ? payment.getPaymentMethod() : null)
                .paymentStatus(payment != null ? payment.getStatus() : null)
                .items(items)
                .build();
    }

    private List<InvoiceWithPayment> loadInvoiceRows(Long providerId) {
        return invoiceRepository.findDetailedByProviderId(providerId).stream()
                .map(invoice -> new InvoiceWithPayment(invoice, latestPayment(invoice.getId())))
                .sorted(Comparator.comparing((InvoiceWithPayment row) -> invoiceDateTime(row.invoice()),
                        Comparator.nullsLast(Comparator.reverseOrder()))
                        .thenComparing(row -> row.invoice().getId(), Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();
    }

    private List<PartnerRevenueByServiceResponse> buildRevenueByService(List<InvoiceWithPayment> paidRows) {
        Map<Long, List<InvoiceWithPayment>> byService = paidRows.stream()
                .filter(row -> row.invoice().getBooking() != null)
                .collect(Collectors.groupingBy(row -> {
                    Long serviceId = resolveProviderServiceId(row.invoice().getBooking());
                    return serviceId != null ? serviceId : 0L;
                }, LinkedHashMap::new, Collectors.toList()));

        List<PartnerRevenueByServiceResponse> result = new ArrayList<>();
        byService.forEach((serviceId, serviceRows) -> {
            Booking booking = serviceRows.get(0).invoice().getBooking();
            BigDecimal revenue = sumTotal(serviceRows);
            long bookingCount = serviceRows.stream()
                    .map(row -> row.invoice().getBooking())
                    .filter(Objects::nonNull)
                    .map(Booking::getId)
                    .filter(Objects::nonNull)
                    .distinct()
                    .count();
            result.add(PartnerRevenueByServiceResponse.builder()
                    .providerServiceId(serviceId == 0L ? null : serviceId)
                    .serviceName(resolveServiceName(booking))
                    .bookingCount(bookingCount)
                    .revenue(revenue)
                    .revenueDisplay(mapper.formatMoney(revenue))
                    .build());
        });
        return result.stream()
                .sorted(Comparator.comparing(PartnerRevenueByServiceResponse::revenue).reversed())
                .limit(8)
                .toList();
    }

    private PartnerInvoiceSummaryResponse mapInvoiceSummary(InvoiceWithPayment row) {
        Invoice invoice = row.invoice();
        Payment payment = row.payment();
        Booking booking = invoice.getBooking();
        return PartnerInvoiceSummaryResponse.builder()
                .invoiceId(invoice.getId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .invoiceStatus(invoice.getStatus())
                .invoiceType(invoice.getInvoiceType())
                .bookingId(booking != null ? booking.getId() : null)
                .bookingCode(booking != null ? booking.getBookingCode() : null)
                .customerName(resolveCustomerName(invoice))
                .serviceName(booking != null ? booking.getServiceNameSnapshot() : null)
                .petName(booking != null ? booking.getPetNameSnapshot() : null)
                .appointmentDate(booking != null ? mapper.formatIsoDate(booking.getAppointmentDate()) : null)
                .appointmentDateDisplay(booking != null ? mapper.formatDate(booking.getAppointmentDate()) : null)
                .appointmentTime(
                        booking != null ? mapper.formatTimeRange(booking.getStartTime(), booking.getEndTime()) : null)
                .issuedAt(formatDateTime(invoice.getIssuedAt()))
                .paidAt(formatDateTime(invoice.getPaidAt()))
                .totalAmount(mapper.defaultMoney(invoice.getTotalAmount()))
                .totalAmountDisplay(mapper.formatMoney(invoice.getTotalAmount()))
                .currencyCode(mapper.firstNonBlank(invoice.getCurrencyCode(), "VND"))
                .paymentId(payment != null ? payment.getId() : null)
                .paymentCode(payment != null ? payment.getPaymentCode() : null)
                .paymentMethod(payment != null ? payment.getPaymentMethod() : null)
                .paymentStatus(payment != null ? payment.getStatus() : null)
                .build();
    }

    private InvoiceItemResponse mapItem(InvoiceItem item) {
        return InvoiceItemResponse.builder()
                .itemType(item.getItemType())
                .itemName(item.getItemName())
                .description(item.getDescription())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .lineTotal(item.getLineTotal())
                .build();
    }

    private BigDecimal sumTotal(List<InvoiceWithPayment> rows) {
        return rows.stream()
                .map(row -> row.invoice().getTotalAmount())
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Payment latestPayment(Long invoiceId) {
        return invoiceId == null ? null
                : paymentRepository.findTopByInvoiceIdOrderByCreatedAtDesc(invoiceId).orElse(null);
    }

    private boolean matchesDateRange(Invoice invoice, DateRange range) {
        if (range.from() == null && range.to() == null) {
            return true;
        }
        LocalDate invoiceDate = invoiceDate(invoice);
        if (invoiceDate == null) {
            return true;
        }
        if (range.from() != null && invoiceDate.isBefore(range.from())) {
            return false;
        }
        return range.to() == null || !invoiceDate.isAfter(range.to());
    }

    private boolean matchesStatus(InvoiceWithPayment row, String filter) {
        return switch (filter) {
            case "PAID" -> isPaidInvoice(row);
            case "PENDING" -> isPendingInvoice(row);
            case "ISSUED", "CANCELLED", "VOID" -> statusEquals(row.invoice().getStatus(), filter);
            default -> true;
        };
    }

    private boolean isPaidInvoice(InvoiceWithPayment row) {
        return statusEquals(row.invoice().getStatus(), "PAID")
                || (row.payment() != null && statusEquals(row.payment().getStatus(), "SUCCEEDED"));
    }

    private boolean isPendingInvoice(InvoiceWithPayment row) {
        String invoiceStatus = mapper.firstNonBlank(row.invoice().getStatus(), "").toUpperCase(Locale.ROOT);
        String paymentStatus = row.payment() != null
                ? mapper.firstNonBlank(row.payment().getStatus(), "").toUpperCase(Locale.ROOT)
                : "";
        return List.of("ISSUED", "PENDING").contains(invoiceStatus)
                || List.of("PENDING", "PROCESSING").contains(paymentStatus);
    }

    private boolean statusEquals(String actual, String expected) {
        return expected.equalsIgnoreCase(mapper.firstNonBlank(actual, ""));
    }

    private DateRange resolveRange(String from, String to, boolean defaultCurrentMonth) {
        LocalDate fromDate = parseOptionalDate(from);
        LocalDate toDate = parseOptionalDate(to);
        if (fromDate == null && toDate == null && defaultCurrentMonth) {
            LocalDate today = LocalDate.now(PartnerMappingSupport.APP_ZONE);
            fromDate = today.withDayOfMonth(1);
            toDate = today;
        }
        if (fromDate != null && toDate != null && toDate.isBefore(fromDate)) {
            throw new BadRequestException("Khoảng thời gian doanh thu không hợp lệ.");
        }
        return new DateRange(fromDate, toDate);
    }

    private LocalDate parseOptionalDate(String value) {
        String normalized = mapper.normalizeBlank(value);
        if (normalized == null) {
            return null;
        }
        try {
            return LocalDate.parse(normalized, PartnerMappingSupport.ISO_DATE);
        } catch (DateTimeParseException ex) {
            throw new BadRequestException("Ngày filter cần định dạng yyyy-MM-dd.");
        }
    }

    private String normalizeStatus(String status) {
        if (status == null || status.isBlank()) {
            return "ALL";
        }
        return status.trim().toUpperCase(Locale.ROOT);
    }

    private LocalDate invoiceDate(Invoice invoice) {
        LocalDateTime dateTime = invoiceDateTime(invoice);
        if (dateTime != null) {
            return dateTime.toLocalDate();
        }
        Booking booking = invoice.getBooking();
        return booking != null ? booking.getAppointmentDate() : null;
    }

    private LocalDateTime invoiceDateTime(Invoice invoice) {
        if (invoice.getIssuedAt() != null) {
            return invoice.getIssuedAt();
        }
        if (invoice.getPaidAt() != null) {
            return invoice.getPaidAt();
        }
        if (invoice.getCreatedAt() != null) {
            return invoice.getCreatedAt();
        }
        Booking booking = invoice.getBooking();
        return booking != null && booking.getAppointmentDate() != null ? booking.getAppointmentDate().atStartOfDay()
                : null;
    }

    private String formatDateTime(LocalDateTime value) {
        return value != null ? value.atZone(PartnerMappingSupport.APP_ZONE).format(PartnerMappingSupport.DATE_TIME_VIEW)
                : null;
    }

    private Long resolveProviderServiceId(Booking booking) {
        return booking != null && booking.getProviderService() != null ? booking.getProviderService().getId() : null;
    }

    private String resolveServiceName(Booking booking) {
        ProviderService providerService = booking != null ? booking.getProviderService() : null;
        return mapper.firstNonBlank(
                booking != null ? booking.getServiceNameSnapshot() : null,
                providerService != null ? providerService.getCustomName() : null,
                providerService != null && providerService.getService() != null ? providerService.getService().getName()
                        : null,
                "Dịch vụ");
    }

    private String resolveCustomerName(Invoice invoice) {
        Booking booking = invoice.getBooking();
        return mapper.firstNonBlank(
                invoice.getBillingName(),
                booking != null && booking.getCustomerUser() != null ? booking.getCustomerUser().getFullName() : null,
                "Khách hàng");
    }

    private String resolveCustomerPhone(Invoice invoice) {
        Booking booking = invoice.getBooking();
        return mapper.firstNonBlank(
                invoice.getBillingPhone(),
                booking != null && booking.getCustomerUser() != null ? booking.getCustomerUser().getPhoneNumber()
                        : null);
    }

    private String resolveCustomerEmail(Invoice invoice) {
        Booking booking = invoice.getBooking();
        return mapper.firstNonBlank(
                invoice.getBillingEmail(),
                booking != null && booking.getCustomerUser() != null ? booking.getCustomerUser().getEmail() : null);
    }

    private record DateRange(LocalDate from, LocalDate to) {
    }

    private record InvoiceWithPayment(Invoice invoice, Payment payment) {
    }
}