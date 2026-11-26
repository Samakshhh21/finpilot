
package com.example.finpilot.service;

import com.example.finpilot.dto.request.TransferRequest;
import com.example.finpilot.dto.response.TransferResponse;
import com.example.finpilot.entity.*;
import com.example.finpilot.exception.*;
import com.example.finpilot.repository.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Service
public class TransferExecutionService {

    private final AccountRepository accountRepository;
    private final FinancialTransactionRepository transactionRepository;
    private final TransferRepository transferRepository;

    public TransferExecutionService(
            AccountRepository accountRepository,
            FinancialTransactionRepository transactionRepository,
            TransferRepository transferRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.transferRepository = transferRepository;
    }

    @Transactional
    public TransferResponse execute(
            TransferRequest request, String idempotencyKey) {

        Long fromId = request.getFromAccountId();
        Long toId = request.getToAccountId();

        if (fromId.equals(toId)) {
            throw new IllegalArgumentException(
                    "Source and destination accounts cannot be the same");
        }

        // Reserve the idempotency key BEFORE moving money.
        Transfer transfer = new Transfer(
                idempotencyKey,
                fromId,
                toId,
                request.getAmount(),
                request.getDescription()
        );

        transferRepository.saveAndFlush(transfer);

        // Lock accounts in deterministic ascending ID order.
        List<Long> lockOrder = List.of(fromId, toId)
                .stream()
                .sorted()
                .toList();

        Map<Long, Account> accountMap = new HashMap<>();

        for (Long accountId : lockOrder) {
            Account account = accountRepository
                    .findByIdForUpdate(accountId)
                    .orElseThrow(() -> new AccountNotFoundException(
                            "Account not found: " + accountId));

            accountMap.put(accountId, account);
        }

        Account source = accountMap.get(fromId);
        Account destination = accountMap.get(toId);

        if (source.getStatus() != AccountStatus.ACTIVE ||
                destination.getStatus() != AccountStatus.ACTIVE) {
            throw new AccountNotActiveException(
                    "Both accounts must be active");
        }

        if (!source.getCurrency().equals(destination.getCurrency())) {
            throw new IllegalArgumentException(
                    "Both accounts must have the same currency");
        }

        source.debit(request.getAmount());
        destination.credit(request.getAmount());

        Instant transactionTime = Instant.now();

        String debitDescription = request.getDescription() != null
                ? request.getDescription()
                : "Transfer to account " + toId;

        String creditDescription = request.getDescription() != null
                ? request.getDescription()
                : "Transfer from account " + fromId;

        FinancialTransaction debit = new FinancialTransaction(
                request.getAmount(),
                TransactionType.DEBIT,
                TransactionCategory.OTHER,
                debitDescription,
                transactionTime,
                source
        );

        FinancialTransaction credit = new FinancialTransaction(
                request.getAmount(),
                TransactionType.CREDIT,
                TransactionCategory.OTHER,
                creditDescription,
                transactionTime,
                destination
        );

        List<FinancialTransaction> savedTransactions =
                transactionRepository.saveAllAndFlush(
                        List.of(debit, credit));

        FinancialTransaction savedDebit = savedTransactions.get(0);
        FinancialTransaction savedCredit = savedTransactions.get(1);

        transfer.markCompleted(
                savedDebit.getId(),
                savedCredit.getId()
        );

        // Transfer is managed by JPA. Its update is committed
        // together with both account changes and ledger entries.
        return new TransferResponse(
                transfer.getId(),
                savedDebit.getId(),
                savedCredit.getId(),
                fromId,
                toId,
                request.getAmount(),
                transfer.getStatus(),
                "Transfer completed successfully"
        );
    }
}
