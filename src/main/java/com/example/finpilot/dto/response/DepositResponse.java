
package com.example.finpilot.dto.response;

import java.math.BigDecimal;

public record DepositResponse(
        Long accountId,
        Long transactionId,
        BigDecimal depositedAmount,
        BigDecimal updatedBalance,
        String status,
        String message
) {}
