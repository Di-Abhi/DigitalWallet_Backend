package com.loyaltyService.wallet_service.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TransferRequest {
    @NotBlank(message = "Receiver identifier is required")
    @Size(max = 100, message = "Receiver identifier is too long")
    private String receiverIdentifier;
    @NotNull @DecimalMin("1.00") @DecimalMax("25000.00") private BigDecimal amount;
    @Size(max = 64) private String idempotencyKey;
    @Size(max = 255) private String description;
}
