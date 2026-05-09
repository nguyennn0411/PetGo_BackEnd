package com.example.petgo.service.impl;

import com.example.petgo.dto.UserResponse;
import com.example.petgo.dto.UserStatusRequest;
import com.example.petgo.entity.User;
import com.example.petgo.repository.UserRepository;
import com.example.petgo.repository.UserRoleRepository;
import com.example.petgo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;
import java.util.List;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;

    @Override
    @Transactional(readOnly = true) // <--- THÊM DÒNG NÀY ĐỂ FIX LỖI LAZY INITIALIZATION
    public List<UserResponse> getAllUsers() {
        System.out.println("--- Bat dau lay danh sach user ---");
        List<User> users = userRepository.findAll();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return users.stream().map(user -> {
            // Log để theo dõi
            System.out.println("Dang xu ly user: " + user.getEmail());

            // Lấy roles từ bảng trung gian
            List<String> roles = userRoleRepository.findByUser_Id(user.getId()).stream()
                    .map(ur -> ur.getRole().getCode()) // Bây giờ Session vẫn mở nên lấy được code ngon lành
                    .toList();

            if (roles.isEmpty()) roles = List.of("CUSTOMER");

            return UserResponse.builder()
                    .id(user.getId())
                    .userCode(user.getUserCode())
                    .email(user.getEmail())
                    .fullName(user.getFullName())
                    .phoneNumber(user.getPhoneNumber())
                    .avatarUrl(user.getAvatarUrl())
                    .status(user.getStatus())
                    .createdAt(user.getCreatedAt() != null ? user.getCreatedAt().format(formatter) : null)
                    .roles(roles)
                    .build();
        }).toList();
    }

    @Override
    @Transactional
    public void updateUserStatus(UserStatusRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        boolean isAdmin = userRoleRepository.findByUser_Id(user.getId()).stream()
                .anyMatch(ur -> "ADMIN".equalsIgnoreCase(ur.getRole().getCode()));
        if (isAdmin) {
            throw new RuntimeException("Không được phép thay đổi trạng thái tài khoản ADMIN.");
        }

        user.setStatus(request.status());
        userRepository.save(user);
    }
}