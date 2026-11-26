
package com.example.finpilot.dto.response;

import java.math.BigDecimal;

public record TransferResponse(
        Long transferId,
        Long debitTransactionId,
        Long creditTransactionId,
        Long fromAccountId,
        Long toAccountId,
        BigDecimal amount,
        String status,
        String message
) {}
