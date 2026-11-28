
package com.example.finpilot.service;

import com.example.finpilot.dto.request.DepositRequest;
import com.example.finpilot.dto.response.DepositResponse;
import com.example.finpilot.entity.*;
import com.example.finpilot.exception.AccountNotFoundException;
import com.example.finpilot.exception.AccountNotActiveException;
import com.example.finpilot.repository.AccountRepository;
import com.example.finpilot.repository.FinancialTransactionRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class DepositService {

    private final AccountRepository accountRepository;
    private final FinancialTransactionRepository transactionRepository;

    public DepositService(
            AccountRepository accountRepository,
            FinancialTransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public DepositResponse deposit(
            Long accountId, DepositRequest request) {

        // 1. Lock the account
        Account account = accountRepository
                .findByIdForUpdate(accountId)
                .orElseThrow(() -> new AccountNotFoundException(
                        "Account not found"
                ));

        // 2. Validate account status
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new AccountNotActiveException(
                    "Account is not active"
            );
        }

        // 3. Credit the account
        account.credit(request.getAmount());

        // 4. Create ledger entry
        String description = request.getDescription() != null
                ? request.getDescription()
                : "Account deposit";

        FinancialTransaction transaction =
                new FinancialTransaction(
                        request.getAmount(),
                        TransactionType.CREDIT,
                        TransactionCategory.OTHER,
                        description,
                        Instant.now(),
                        account
                );

        FinancialTransaction savedTransaction =
                transactionRepository.saveAndFlush(transaction);

        // 5. Return updated balance
        return new DepositResponse(
                account.getId(),
                savedTransaction.getId(),
                request.getAmount(),
                account.getBalance(),
                "COMPLETED",
                "Deposit completed successfully"
        );
    }
}
