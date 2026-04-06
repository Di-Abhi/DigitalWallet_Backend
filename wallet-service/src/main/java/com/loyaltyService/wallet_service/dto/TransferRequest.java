package com.loyaltyService.wallet_service.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TransferRequest {
    @NotNull
    @Pattern(regexp = "^\\d{10}$", message = "Receiver phone must be exactly 10 digits")
    private String receiverPhone;
    @NotNull @DecimalMin("1.00") @DecimalMax("25000.00") private BigDecimal amount;
    @Size(max = 64) private String idempotencyKey;
    @Size(max = 255) private String description;
}
