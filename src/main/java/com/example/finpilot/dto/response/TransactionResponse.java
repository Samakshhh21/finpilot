
package com.example.finpilot.dto.response;

import com.example.finpilot.entity.TransactionCategory;
import com.example.finpilot.entity.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;

public record TransactionResponse(
        Long id,
        Long accountId,
        BigDecimal amount,
        TransactionType type,
        TransactionCategory category,
        String description,
        Instant transactionDate,
        Instant createdAt
) {}
