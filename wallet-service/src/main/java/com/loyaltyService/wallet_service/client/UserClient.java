package com.loyaltyService.wallet_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
public interface UserClient {

    @GetMapping("/api/users/internal/phone/{phone}")
    UserProfileResponse getUserByPhone(@PathVariable("phone") String phone);

    record UserProfileResponse(Long id, String name, String email, String phone, String status,
            String kycStatus) {
    }
}
