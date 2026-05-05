package com.payments.payment_processing_api.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class AccountResponse {
    private UUID id;
    private String accountNumber;
    private String ownerName;
    private BigDecimal balance;
    private String currency;
    private LocalDateTime createdAt;
}