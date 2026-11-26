
package com.example.finpilot.service;

import com.example.finpilot.dto.request.TransferRequest;
import com.example.finpilot.dto.response.TransferResponse;
import com.example.finpilot.entity.Account;
import com.example.finpilot.entity.AccountStatus;
import com.example.finpilot.entity.FinancialTransaction;
import com.example.finpilot.entity.TransactionCategory;
import com.example.finpilot.entity.TransactionType;
import com.example.finpilot.exception.AccountNotFoundException;
import com.example.finpilot.exception.AccountNotActiveException;
import com.example.finpilot.repository.AccountRepository;
import com.example.finpilot.repository.FinancialTransactionRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class TransferService {

    private final AccountRepository accountRepository;
    private final FinancialTransactionRepository transactionRepository;

    public TransferService(
            AccountRepository accountRepository,
            FinancialTransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public TransferResponse transfer(TransferRequest request) {

        // 1. Validate source and destination
        Long fromId = request.getFromAccountId();
        Long toId = request.getToAccountId();
        BigDecimal amount = request.getAmount();

        if (fromId.equals(toId)) {
            throw new IllegalArgumentException(
                    "Source and destination accounts cannot be the same"
            );
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Transfer amount must be greater than zero"
            );
        }

        // 2. Lock both accounts in ascending ID order
        List<Account> accounts =
                accountRepository.findAllByIdForUpdate(
                        List.of(fromId, toId)
                );

        if (accounts.size() != 2) {
            throw new AccountNotFoundException(
                    "Source or destination account not found"
            );
        }

        // 3. Convert locked accounts into a lookup map
        Map<Long, Account> accountMap = accounts.stream()
                .collect(Collectors.toMap(
                        Account::getId,
                        Function.identity()
                ));

        Account source = accountMap.get(fromId);
        Account destination = accountMap.get(toId);

        // 4. Validate account statuses
        if (source.getStatus() != AccountStatus.ACTIVE ||
                destination.getStatus() != AccountStatus.ACTIVE) {
            throw new AccountNotActiveException(
                    "Both accounts must be active"
            );
        }

        // 5. Validate currency compatibility
        if (!source.getCurrency().equals(destination.getCurrency())) {
            throw new IllegalArgumentException(
                    "Both accounts must have the same currency"
            );
        }

        // 6. Debit and credit balances
        source.debit(amount);
        destination.credit(amount);

        // 7. Create transaction history records
        Instant transactionTime = Instant.now();

        String debitDescription = request.getDescription() != null
                ? request.getDescription()
                : "Transfer to account " + toId;

        String creditDescription = request.getDescription() != null
                ? request.getDescription()
                : "Transfer from account " + fromId;

        FinancialTransaction debitTransaction =
                new FinancialTransaction(
                        amount,
                        TransactionType.DEBIT,
                        TransactionCategory.OTHER,
                        debitDescription,
                        transactionTime,
                        source
                );

        FinancialTransaction creditTransaction =
                new FinancialTransaction(
                        amount,
                        TransactionType.CREDIT,
                        TransactionCategory.OTHER,
                        creditDescription,
                        transactionTime,
                        destination
                );

        // 8. Save both transaction records
        List<FinancialTransaction> savedTransactions =
                transactionRepository.saveAll(
                        List.of(debitTransaction, creditTransaction)
                );

        // 9. Return response
        return new TransferResponse(
                savedTransactions.get(0).getId(),
                savedTransactions.get(1).getId(),
                fromId,
                toId,
                amount,
                "COMPLETED",
                "Transfer completed successfully"
        );
    }
}
