package com.example.finpilot.dto.response;

import com.example.finpilot.entity.AccountStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record AccountDetailsResponse(
        Long id,
        String accountNumber,
        BigDecimal balance,
        String currency,
        AccountStatus status,
        Instant createdAt,
        UserSummaryResponse user
) {}