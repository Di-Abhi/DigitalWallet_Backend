package com.loyaltyService.user_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "auth-service", fallback = AuthServiceClientFallback.class)
public interface AuthServiceClient {

    @PutMapping("/api/auth/internal/update-profile")
    void updateProfile(@RequestBody UpdateProfileRequest request);

    @PutMapping("/api/auth/internal/update-role")
    void updateRole(@RequestBody UpdateRoleRequest request);

    record UpdateProfileRequest(Long userId, String name, String phone) {}

    record UpdateRoleRequest(Long userId, String role) {}
}
