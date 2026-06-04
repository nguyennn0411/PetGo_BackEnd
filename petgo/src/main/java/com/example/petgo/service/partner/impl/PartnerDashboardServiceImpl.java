package com.example.petgo.service.partner.impl;

import com.example.petgo.dto.partner.PartnerBookingSummaryResponse;
import com.example.petgo.dto.partner.PartnerDashboardSummaryResponse;
import com.example.petgo.dto.partner.PartnerTopServiceResponse;
import com.example.petgo.entity.Booking;
import com.example.petgo.entity.ProviderProfile;
import com.example.petgo.entity.ProviderService;
import com.example.petgo.repository.BookingRepository;
import com.example.petgo.repository.ProviderBusinessHourRepository;
import com.example.petgo.repository.ProviderServiceRepository;
import com.example.petgo.service.partner.PartnerAccessService;
import com.example.petgo.service.partner.PartnerDashboardService;
import com.example.petgo.service.partner.PartnerMappingSupport;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PartnerDashboardServiceImpl implements PartnerDashboardService {

        private final PartnerAccessService partnerAccessService;
        private final PartnerMappingSupport mapper;
        private final BookingRepository bookingRepository;
        private final ProviderServiceRepository providerServiceRepository;
        private final ProviderBusinessHourRepository providerBusinessHourRepository;

        @Override
        @Transactional(readOnly = true)
        public PartnerDashboardSummaryResponse getSummary(HttpServletRequest request) {
                PartnerAccessService.PartnerContext context = partnerAccessService.requirePartnerContext(request);
                ProviderProfile provider = context.provider();
                LocalDate today = LocalDate.now(PartnerMappingSupport.APP_ZONE);
                LocalDate monthStart = today.withDayOfMonth(1);

                List<Booking> bookings = bookingRepository.findDetailedByProviderId(provider.getId());
                List<ProviderService> services = providerServiceRepository.findAllDetailsByProviderId(provider.getId());
                boolean missingServices = services.stream()
                                .noneMatch(service -> Boolean.TRUE.equals(service.getActive()));
                boolean missingSchedule = providerBusinessHourRepository
                                .findByProvider_IdOrderByWeekdayAscIdAsc(provider.getId()).stream()
                                .noneMatch(hour -> !Boolean.TRUE.equals(hour.getClosed()) && hour.getOpensAt() != null
                                                && hour.getClosesAt() != null);
                boolean missingProfile = mapper.firstNonBlank(provider.getDescription()) == null
                                || mapper.firstNonBlank(provider.getPrimaryAddressLine1(), provider.getCity(),
                                                provider.getProvince()) == null
                                || mapper.firstNonBlank(provider.getMainImageUrl(),
                                                provider.getCoverImageUrl()) == null;

                Map<Long, Long> bookingCountByService = bookings.stream()
                                .filter(booking -> booking.getProviderService() != null)
                                .collect(Collectors.groupingBy(booking -> booking.getProviderService().getId(),
                                                Collectors.counting()));

                List<PartnerTopServiceResponse> topServices = services.stream()
                                .map(service -> PartnerTopServiceResponse.builder()
                                                .providerServiceId(service.getId())
                                                .serviceName(mapper.firstNonBlank(service.getCustomName(),
                                                                service.getService() != null
                                                                                ? service.getService().getName()
                                                                                : null))
                                                .bookingCount(bookingCountByService.getOrDefault(service.getId(), 0L))
                                                .build())
                                .sorted(Comparator.comparingLong(PartnerTopServiceResponse::bookingCount).reversed())
                                .limit(5)
                                .toList();

                long completed = bookings.stream()
                                .filter(booking -> "COMPLETED"
                                                .equalsIgnoreCase(mapper.firstNonBlank(booking.getStatus(), "")))
                                .count();
                long cancelled = bookings.stream()
                                .filter(booking -> "CANCELLED"
                                                .equalsIgnoreCase(mapper.firstNonBlank(booking.getStatus(), "")))
                                .count();
                long countedForCompletion = completed + cancelled;
                BigDecimal completionRate = countedForCompletion == 0
                                ? BigDecimal.ZERO
                                : BigDecimal.valueOf(completed * 100.0 / countedForCompletion).setScale(1,
                                                RoundingMode.HALF_UP);

                BigDecimal monthlyRevenue = bookings.stream()
                                .filter(booking -> "COMPLETED"
                                                .equalsIgnoreCase(mapper.firstNonBlank(booking.getStatus(), "")))
                                .filter(booking -> booking.getAppointmentDate() != null
                                                && !booking.getAppointmentDate().isBefore(monthStart))
                                .map(Booking::getTotalAmount)
                                .filter(Objects::nonNull)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                List<String> warnings = new ArrayList<>();
                if (missingProfile)
                        warnings.add("Hồ sơ nhà cung cấp còn thiếu mô tả, địa chỉ hoặc ảnh hiển thị.");
                if (missingServices)
                        warnings.add("Nhà cung cấp chưa có dịch vụ active.");
                if (missingSchedule)
                        warnings.add("Nhà cung cấp chưa cấu hình lịch làm việc mở cửa.");

                List<PartnerBookingSummaryResponse> actionRequired = bookings.stream()
                                .filter(booking -> mapper.canConfirm(booking) || mapper.canStart(booking)
                                                || mapper.canComplete(booking))
                                .limit(6)
                                .map(mapper::mapBookingSummary)
                                .toList();

                return PartnerDashboardSummaryResponse.builder()
                                .providerId(provider.getId())
                                .businessName(provider.getBusinessName())
                                .verificationStatus(provider.getVerificationStatus())
                                .status(provider.getStatus())
                                .todayBookings(bookings.stream()
                                                .filter(booking -> today.equals(booking.getAppointmentDate())).count())
                                .pendingBookings(bookings.stream()
                                                .filter(booking -> mapper.isPending(booking.getStatus())).count())
                                .upcomingBookings(bookings.stream().filter(mapper::isUpcoming).count())
                                .completedBookings(completed)
                                .cancelledBookings(cancelled)
                                .monthlyRevenue(monthlyRevenue)
                                .monthlyRevenueDisplay(mapper.formatMoney(monthlyRevenue))
                                .averageRating(mapper.defaultMoney(provider.getAverageRating()))
                                .newReviews(0)
                                .completionRate(completionRate)
                                .topServices(topServices)
                                .missingProfileInfo(missingProfile)
                                .missingServices(missingServices)
                                .missingSchedule(missingSchedule)
                                .warnings(warnings)
                                .actionRequiredBookings(actionRequired)
                                .build();
        }
}