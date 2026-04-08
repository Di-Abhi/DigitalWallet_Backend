package com.loayaltyService.notification_service.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loayaltyService.notification_service.client.UserClient;
import com.loayaltyService.notification_service.service.EmailService;
import com.loayaltyService.notification_service.service.NotificationConsumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumerImpl implements NotificationConsumer {

    private final EmailService emailService;
    private final UserClient userClient;
    private final ObjectMapper objectMapper;

    @Override
    @KafkaListener(topics = "wallet-events", groupId = "notification-group")
    public void walletEvents(String event) {
        processEvent(event);
    }

    @Override
    @KafkaListener(topics = "payment-events", groupId = "notification-group")
    public void paymentEvents(String event) {
        processEvent(event);
    }

    @Override
    @KafkaListener(topics = "reward-events", groupId = "notification-group")
    public void rewardEvents(String event) {
        processEvent(event);
    }

    @Override
    @KafkaListener(topics = "kyc-events", groupId = "notification-group")
    public void kycEvents(String event) {
        processEvent(event);
    }

    @SuppressWarnings("unchecked")
    private void processEvent(String event) {
        log.info("Received Kafka event: {}", event);
        try {
            Map<String, Object> data = objectMapper.readValue(event, Map.class);
            String eventType = (String) data.get("event");
            if (eventType == null) {
                log.warn("Event missing 'event' field: {}", event);
                return;
            }

            switch (eventType) {
                case "KYC_APPROVED":
                case "KYC_REJECTED": {
                    Long kycUserId = Long.valueOf(data.get("userId").toString());
                    String email = userClient.getProfile(kycUserId).getEmail();
                    String subject = "KYC_APPROVED".equals(eventType) ? "KYC Approved" : "KYC Rejected";
                    String message = "KYC_APPROVED".equals(eventType)
                            ? "Your KYC has been approved successfully. You can now access all Digital Wallet features."
                            : "Your KYC verification was rejected. Reason: " + data.get("reason");
                    emailService.sendHtml(email, subject,
                            buildEmailHtml(subject, message, "-", "-", "KYC-" + kycUserId));
                    log.info("KYC email sent to {}", email);
                    break;
                }

                case "TRANSFER_SUCCESS": {
                    Long senderId = Long.valueOf(data.get("senderId").toString());
                    Long receiverId = Long.valueOf(data.get("receiverId").toString());
                    String senderEmail = userClient.getProfile(senderId).getEmail();
                    String receiverEmail = userClient.getProfile(receiverId).getEmail();
                    String amount = String.valueOf(data.get("amount"));
                    String senderBalance = String.valueOf(data.getOrDefault("senderBalance",
                            data.getOrDefault("balance", "0")));
                    String receiverBalance = String.valueOf(data.getOrDefault("receiverBalance",
                            data.getOrDefault("balance", "0")));
                    String reference = String.valueOf(data.getOrDefault("reference", "N/A"));

                    emailService.sendHtml(senderEmail, "Transfer Successful",
                            buildEmailHtml("Money Sent",
                                    "Your transfer has been completed successfully.",
                                    amount, senderBalance, reference));

                    emailService.sendHtml(receiverEmail, "Money Received",
                            buildEmailHtml("Money Received",
                                    "Funds have been credited to your Digital Wallet.",
                                    amount, receiverBalance, reference));
                    break;
                }

                case "TOPUP_SUCCESS":
                case "WITHDRAW_SUCCESS":
                case "PAYMENT_SUCCESS":
                case "POINTS_EARNED":
                case "REDEEM_SUCCESS": {
                    Long userId = Long.valueOf(data.get("userId").toString());
                    String userEmail = userClient.getProfile(userId).getEmail();
                    String amt = String.valueOf(data.getOrDefault("amount", "0"));
                    String bal = String.valueOf(data.getOrDefault("balance", "0"));
                    String ref = String.valueOf(data.getOrDefault("reference", "N/A"));
                    String subjectOther = getSubject(eventType);
                    String messageOther = getMessage(eventType);
                    emailService.sendHtml(userEmail, subjectOther,
                            buildEmailHtml(subjectOther, messageOther, amt, bal, ref));
                    break;
                }

                default:
                    log.debug("Unhandled event type: {}", eventType);
            }

        } catch (Exception e) {
            log.error("Error processing Kafka event: {}", event, e);
        }
    }

    private String getSubject(String eventType) {
        return switch (eventType) {
            case "TOPUP_SUCCESS" -> "Digital Wallet Top-up Successful";
            case "WITHDRAW_SUCCESS" -> "Digital Wallet Withdrawal Successful";
            case "PAYMENT_SUCCESS" -> "Payment Successful";
            case "POINTS_EARNED" -> "Reward Points Added";
            case "REDEEM_SUCCESS" -> "Reward Redemption Successful";
            default -> "Digital Wallet Notification";
        };
    }

    private String getMessage(String eventType) {
        return switch (eventType) {
            case "TOPUP_SUCCESS" -> "Funds have been added to your Digital Wallet successfully.";
            case "WITHDRAW_SUCCESS" -> "Your withdrawal has been completed successfully.";
            case "PAYMENT_SUCCESS" -> "Your payment has been processed successfully.";
            case "POINTS_EARNED" -> "New reward points have been credited to your account.";
            case "REDEEM_SUCCESS" -> "Your reward points have been redeemed successfully.";
            default -> "There is a new update on your Digital Wallet account.";
        };
    }

    private String buildEmailHtml(String title, String message, String amount, String balance, String reference) {
        String safeTitle = safe(title);
        String safeMessage = safe(message);
        String safeAmount = displayAmount(amount);
        String safeBalance = displayAmount(balance);
        String safeReference = safe(reference);

        return "<!DOCTYPE html>" +
                "<html lang='en'>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "<title>" + safeTitle + "</title>" +
                "</head>" +
                "<body style='margin:0;padding:0;background:#edf2f7;font-family:Segoe UI,Roboto,Helvetica,Arial,sans-serif;color:#0f172a;'>" +
                "<div style='display:none;max-height:0;overflow:hidden;opacity:0;'>" + safeMessage + "</div>" +
                "<table role='presentation' width='100%' cellspacing='0' cellpadding='0' style='background:linear-gradient(180deg,#e0f2fe 0%,#edf2f7 45%,#f8fafc 100%);padding:32px 16px;'>" +
                "<tr><td align='center'>" +
                "<table role='presentation' width='100%' cellspacing='0' cellpadding='0' style='max-width:680px;background:#ffffff;border-radius:24px;overflow:hidden;box-shadow:0 20px 45px rgba(15,23,42,0.12);'>" +
                "<tr><td style='padding:0;background:linear-gradient(135deg,#0f172a 0%,#0f766e 55%,#22c55e 100%);'>" +
                "<div style='padding:36px 40px 30px;color:#ffffff;'>" +
                "<div style='display:inline-block;padding:8px 14px;border:1px solid rgba(255,255,255,0.22);border-radius:999px;background:rgba(255,255,255,0.10);font-size:12px;font-weight:700;letter-spacing:0.08em;text-transform:uppercase;'>Digital Wallet</div>" +
                "<h1 style='margin:18px 0 10px;font-size:30px;line-height:1.2;font-weight:800;'>" + safeTitle + "</h1>" +
                "<p style='margin:0;font-size:16px;line-height:1.7;color:rgba(255,255,255,0.88);'>" + safeMessage + "</p>" +
                "</div>" +
                "</td></tr>" +
                "<tr><td style='padding:32px 40px 12px;'>" +
                "<table role='presentation' width='100%' cellspacing='0' cellpadding='0' style='border-collapse:separate;border-spacing:0 14px;'>" +
                buildMetricRow("Amount", safeAmount) +
                buildMetricRow("Reference", safeReference) +
                buildMetricRow("Available Balance", safeBalance) +
                "</table>" +
                "</td></tr>" +
                "<tr><td style='padding:8px 40px 32px;'>" +
                "<div style='border-radius:18px;background:#f8fafc;border:1px solid #e2e8f0;padding:20px 22px;'>" +
                "<p style='margin:0 0 8px;font-size:14px;font-weight:700;color:#0f172a;'>Need a quick recap?</p>" +
                "<p style='margin:0;font-size:14px;line-height:1.7;color:#475569;'>This is an automated update from Digital Wallet regarding recent activity on your account. If you did not expect this notification, please review your account activity immediately.</p>" +
                "</div>" +
                "</td></tr>" +
                "<tr><td style='padding:0 40px 30px;'>" +
                "<p style='margin:0;font-size:12px;line-height:1.7;color:#64748b;text-align:center;'>Digital Wallet notifications are sent to keep you informed about important account events.</p>" +
                "</td></tr>" +
                "</table>" +
                "</td></tr>" +
                "</table>" +
                "</body></html>";
    }

    private String buildMetricRow(String label, String value) {
        return "<tr>" +
                "<td style='padding:18px 22px;border:1px solid #e2e8f0;border-radius:18px;background:#ffffff;'>" +
                "<table role='presentation' width='100%' cellspacing='0' cellpadding='0'>" +
                "<tr>" +
                "<td style='font-size:13px;font-weight:700;letter-spacing:0.06em;text-transform:uppercase;color:#64748b;'>" + safe(label) + "</td>" +
                "<td align='right' style='font-size:18px;font-weight:800;color:#0f172a;'>" + value + "</td>" +
                "</tr>" +
                "</table>" +
                "</td>" +
                "</tr>";
    }

    private String displayAmount(String value) {
        if (value == null || value.isBlank() || "-".equals(value)) {
            return "-";
        }
        return "&#8377;" + safe(value);
    }

    private String safe(String value) {
        if (value == null) {
            return "-";
        }
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
