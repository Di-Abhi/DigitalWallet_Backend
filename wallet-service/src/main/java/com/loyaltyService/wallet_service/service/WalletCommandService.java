package com.loyaltyService.wallet_service.service;

import com.loyaltyService.wallet_service.dto.ReceiverSuggestionResponse;
import com.loyaltyService.wallet_service.entity.WalletAccount;

import java.math.BigDecimal;

/**
 * CQRS — Command side: all write operations for Wallet.
 */
public interface WalletCommandService {

    void createWallet(Long userId);

    void topup(Long userId, BigDecimal amount, String idempotencyKey);

    void transfer(Long senderId, String receiverIdentifier, BigDecimal amount,
            String idempotencyKey, String description);

    ReceiverSuggestionResponse resolveReceiver(String receiverIdentifier);

    void withdraw(Long userId, BigDecimal amount);

    void creditInternal(Long userId, BigDecimal amount, String source);

    void creditInternal(Long userId, BigDecimal amount);

    void updateStatus(Long userId, WalletAccount.WalletStatus status);
}
