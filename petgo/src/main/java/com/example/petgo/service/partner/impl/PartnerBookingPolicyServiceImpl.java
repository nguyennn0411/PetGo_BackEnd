package com.example.petgo.service.partner.impl;

import com.example.petgo.dto.partner.PartnerBookingPolicyRequest;
import com.example.petgo.dto.partner.PartnerBookingPolicyResponse;
import com.example.petgo.entity.ProviderBookingPolicy;
import com.example.petgo.entity.ProviderProfile;
import com.example.petgo.exception.BadRequestException;
import com.example.petgo.repository.ProviderBookingPolicyRepository;
import com.example.petgo.service.partner.PartnerAccessService;
import com.example.petgo.service.partner.PartnerBookingPolicyService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DateTimeException;
import java.time.ZoneId;
import java.util.Locale;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PartnerBookingPolicyServiceImpl implements PartnerBookingPolicyService {

    private static final String DEFAULT_TIMEZONE = "Asia/Ho_Chi_Minh";
    private static final Set<String> FEE_TYPES = Set.of("NONE", "FIXED", "PERCENTAGE");

    private final PartnerAccessService partnerAccessService;
    private final ProviderBookingPolicyRepository providerBookingPolicyRepository;

    @Override
    @Transactional(readOnly = true)
    public PartnerBookingPolicyResponse getPolicy(HttpServletRequest request) {
        ProviderProfile provider = partnerAccessService.requirePartnerContext(request).provider();
        return providerBookingPolicyRepository.findByProvider_Id(provider.getId())
                .map(policy -> mapPolicy(provider, policy, false))
                .orElseGet(() -> defaultPolicy(provider));
    }

    @Override
    @Transactional
    public PartnerBookingPolicyResponse updatePolicy(HttpServletRequest request,
            PartnerBookingPolicyRequest requestBody) {
        ProviderProfile provider = partnerAccessService.requirePartnerContext(request).provider();
        validate(requestBody);

        ProviderBookingPolicy policy = providerBookingPolicyRepository.findByProvider_Id(provider.getId())
                .orElseGet(() -> {
                    ProviderBookingPolicy created = new ProviderBookingPolicy();
                    created.setProvider(provider);
                    return created;
                });
        policy.setTimezone(requestBody.timezone().trim());
        policy.setCancelWindowHours(requestBody.cancelWindowHours());
        policy.setCancelFeeType(normalizeFeeType(requestBody.cancelFeeType()));
        policy.setCancelFeeAmount(requestBody.cancelFeeAmount());
        policy.setCancelFeeAppliesAfterHours(requestBody.cancelFeeAppliesAfterHours());
        policy.setAllowUserReschedule(requestBody.allowUserReschedule());
        policy.setRescheduleWindowHours(requestBody.rescheduleWindowHours());
        policy.setMaxReschedulesPerBooking(requestBody.maxReschedulesPerBooking());
        ProviderBookingPolicy saved = providerBookingPolicyRepository.save(policy);
        return mapPolicy(provider, saved, false);
    }

    private void validate(PartnerBookingPolicyRequest requestBody) {
        try {
            ZoneId.of(requestBody.timezone().trim());
        } catch (DateTimeException ex) {
            throw new BadRequestException("Timezone không hợp lệ.");
        }
        String feeType = normalizeFeeType(requestBody.cancelFeeType());
        if (!FEE_TYPES.contains(feeType)) {
            throw new BadRequestException("Cancel fee type chỉ hỗ trợ NONE, FIXED hoặc PERCENTAGE.");
        }
        BigDecimal amount = requestBody.cancelFeeAmount() != null ? requestBody.cancelFeeAmount() : BigDecimal.ZERO;
        if ("NONE".equals(feeType) && amount.compareTo(BigDecimal.ZERO) != 0) {
            throw new BadRequestException("Cancel fee type NONE phải có amount = 0.");
        }
        if ("PERCENTAGE".equals(feeType) && amount.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new BadRequestException("Cancel fee percentage không được vượt quá 100.");
        }
        if (Boolean.FALSE.equals(requestBody.allowUserReschedule()) && requestBody.maxReschedulesPerBooking() > 0) {
            throw new BadRequestException("Khi không cho user đổi lịch, max reschedules phải bằng 0.");
        }
    }

    private PartnerBookingPolicyResponse defaultPolicy(ProviderProfile provider) {
        String timezone = firstNonBlank(provider.getTimezone(), DEFAULT_TIMEZONE);
        return PartnerBookingPolicyResponse.builder()
                .policyId(null)
                .providerId(provider.getId())
                .timezone(timezone)
                .cancelWindowHours(24)
                .cancelFeeType("NONE")
                .cancelFeeAmount(BigDecimal.ZERO)
                .cancelFeeAppliesAfterHours(null)
                .allowUserReschedule(true)
                .rescheduleWindowHours(24)
                .maxReschedulesPerBooking(1)
                .usingDefault(true)
                .note("Shop chưa cấu hình policy riêng; đang dùng default platform 24h, không phí, đổi lịch tối đa 1 lần.")
                .build();
    }

    private PartnerBookingPolicyResponse mapPolicy(ProviderProfile provider, ProviderBookingPolicy policy,
            boolean usingDefault) {
        return PartnerBookingPolicyResponse.builder()
                .policyId(policy.getId())
                .providerId(provider.getId())
                .timezone(policy.getTimezone())
                .cancelWindowHours(policy.getCancelWindowHours())
                .cancelFeeType(policy.getCancelFeeType())
                .cancelFeeAmount(policy.getCancelFeeAmount())
                .cancelFeeAppliesAfterHours(policy.getCancelFeeAppliesAfterHours())
                .allowUserReschedule(policy.getAllowUserReschedule())
                .rescheduleWindowHours(policy.getRescheduleWindowHours())
                .maxReschedulesPerBooking(policy.getMaxReschedulesPerBooking())
                .usingDefault(usingDefault)
                .note("Policy riêng của shop đang được áp dụng.")
                .build();
    }

    private String normalizeFeeType(String value) {
        return value == null ? "" : value.trim().toUpperCase(Locale.ROOT);
    }

    private String firstNonBlank(String... values) {
        if (values == null) {
            return null;
        }
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value.trim();
            }
        }
        return null;
    }
}