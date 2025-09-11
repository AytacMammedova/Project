package com.company.Project.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private String transactionId;
    private String ecommerceOrderId;
    private String status;
    private BigDecimal amount;
    private String currency;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;
    private String failureReason;
}