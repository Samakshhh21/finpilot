
package com.example.finpilot.dto.response;

import com.example.finpilot.entity.AccountStatus;
import java.math.BigDecimal;
import java.time.Instant;

public record AccountResponse(
        Long id,
        String accountNumber,
        BigDecimal balance,
        String currency,
        AccountStatus status,
        Instant createdAt
) {
}
