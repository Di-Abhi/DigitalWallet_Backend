package com.loyaltyService.user_service.service.impl;

import com.loyaltyService.user_service.client.AuthServiceClient;
import com.loyaltyService.user_service.dto.UpdateUserRequest;
import com.loyaltyService.user_service.dto.UserProfileResponse;
import com.loyaltyService.user_service.entity.User;
import com.loyaltyService.user_service.exception.ResourceNotFoundException;
import com.loyaltyService.user_service.mapper.UserMapper;
import com.loyaltyService.user_service.repository.KycRepository;
import com.loyaltyService.user_service.repository.UserRepository;
import com.loyaltyService.user_service.service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepo;
    private final KycRepository kycRepo;
    private final AuthServiceClient authServiceClient;
    private final UserMapper userMapper;

    // ==================== READ ====================

    @Override
    public UserProfileResponse getProfile(Long userId) {
        return buildUserProfile(userId);
    }

    @Override
    public UserProfileResponse getUserProfile(Long userId) {
        return buildUserProfile(userId);
    }

    // ==================== UPDATE ====================

    @Override
    @Transactional
    public UserProfileResponse updateProfile(Long userId, UpdateUserRequest req) {

        User user = findUser(userId);

        // Partial update via mapper
        userMapper.updateUserFromDto(req, user);

        userRepo.save(user);

        // Sync with Auth Service
        authServiceClient.updateProfile(
                new AuthServiceClient.UpdateProfileRequest(
                        userId,
                        req.getName(),
                        req.getPhone()
                )
        );

        return buildUserProfile(userId);
    }

    // ==================== CREATE ====================

    @Override
    @Transactional
    public void createUser(Long id, String name, String email, String phone, User.Role role) {

        if (userRepo.existsById(id)) return;

        User user = User.builder()
                .id(id)
                .name(name)
                .email(email)
                .phone(phone)
                .role(role)
                .status(User.UserStatus.ACTIVE)
                .build();

        userRepo.save(user);
    }

    // ==================== PRIVATE HELPERS ====================

    private UserProfileResponse buildUserProfile(Long userId) {

        User user = findUser(userId);

        String kycStatus = kycRepo
                .findFirstByUserIdOrderBySubmittedAtDesc(userId)
                .map(k -> k.getStatus().name())
                .orElse("NOT_SUBMITTED");

        return userMapper.toUserProfile(user, kycStatus);
    }

    private User findUser(Long id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }
}