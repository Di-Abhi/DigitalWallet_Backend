package com.loyaltyService.wallet_service.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.loyaltyService.wallet_service.dto.WalletBalanceResponse;
import com.loyaltyService.wallet_service.entity.Transaction;
import com.loyaltyService.wallet_service.entity.WalletAccount;

public interface WalletService {
	void createWallet(Long userId);
	WalletBalanceResponse getBalance(Long userId);
	void topup(Long userId, BigDecimal amount, String idempotencyKey);
	void transfer(Long senderId, Long receiverId, BigDecimal amount, String idempotencyKey, String description);
	void withdraw(Long userId, BigDecimal amount);
	void creditInternal(Long userId, BigDecimal amount, String source);
	void creditInternal(Long userId, BigDecimal amount);
	void updateStatus(Long userId, WalletAccount.WalletStatus status);
	Page<Transaction> getTransactions(Long userId, Pageable pageable);
	List<Transaction> getStatement(Long userId, LocalDateTime from, LocalDateTime to);
}
