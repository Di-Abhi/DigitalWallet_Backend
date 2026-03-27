package com.loyaltyService.reward_service.service;

import java.math.BigDecimal;
import java.util.List;

import com.loyaltyService.reward_service.dto.RewardItemRequest;
import com.loyaltyService.reward_service.dto.RewardSummaryDto;
import com.loyaltyService.reward_service.entity.Redemption;
import com.loyaltyService.reward_service.entity.RewardItem;
import com.loyaltyService.reward_service.entity.RewardTransaction;

public interface RewardService {
	void earnPoints(Long userId, BigDecimal amount);
	RewardItem addCatalogItem(RewardItemRequest req);
	void redeemPoints(Long userId, Integer points);
	void convertPointsToCash(Long userId, Integer points);
	Redemption redeemReward(Long userId, Long rewardId);
	RewardSummaryDto getSummary(Long userId);
	List<RewardItem> getCatalog();
	List<RewardTransaction> getTransactions(Long userId);
	void createAccountIfNotExists(Long userId);
}
