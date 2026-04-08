package com.loyaltyService.wallet_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReceiverSuggestionResponse {
    private Long userId;
    private String name;
    private String email;
    private String phone;
}
