package com.example.petgo.service.partner;

import com.example.petgo.config.AuthenticatedUser;
import com.example.petgo.entity.ProviderProfile;
import com.example.petgo.entity.RoleType;
import com.example.petgo.entity.User;
import com.example.petgo.exception.ResourceNotFoundException;
import com.example.petgo.exception.UnauthorizedException;
import com.example.petgo.repository.ProviderProfileRepository;
import com.example.petgo.repository.UserRepository;
import com.example.petgo.repository.UserRoleRepository;
import com.example.petgo.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PartnerAccessService {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final ProviderProfileRepository providerProfileRepository;
    private final UserRoleRepository userRoleRepository;

    @Transactional(readOnly = true)
    public PartnerContext requirePartnerContext(HttpServletRequest request) {
        AuthenticatedUser authenticatedUser = authService.requireAccessUser(request);
        User user = userRepository.findById(authenticatedUser.userId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng."));
        ProviderProfile provider = providerProfileRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Tài khoản chưa có provider profile được duyệt."));

        boolean hasPartnerRole = authenticatedUser.roles() != null && authenticatedUser.roles().stream()
                .anyMatch(role -> "PROVIDER".equalsIgnoreCase(role)
                        || "PARTNER".equalsIgnoreCase(role));
        if (!hasPartnerRole) {
            hasPartnerRole = userRoleRepository.findByUser_Id(user.getId()).stream()
                    .anyMatch(userRole -> userRole.getRole() != null
                            && RoleType.PROVIDER.equals(userRole.getRole().getCode()));
        }

        if (provider.getDeletedAt() != null || !"ACTIVE".equalsIgnoreCase(nullToEmpty(provider.getStatus()))) {
            throw new UnauthorizedException("Provider profile hiện không hoạt động.");
        }
        if (!hasPartnerRole) {
            throw new UnauthorizedException("Bạn không có quyền truy cập Partner Dashboard.");
        }

        return new PartnerContext(user, provider);
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    public record PartnerContext(User user, ProviderProfile provider) {
    }
}