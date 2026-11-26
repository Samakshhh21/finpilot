
package com.example.finpilot.service;

import com.example.finpilot.dto.request.TransactionRequest;
import com.example.finpilot.dto.response.TransactionResponse;
import com.example.finpilot.entity.Account;
import com.example.finpilot.entity.AccountStatus;
import com.example.finpilot.entity.FinancialTransaction;
import com.example.finpilot.entity.TransactionType;
import com.example.finpilot.exception.AccountNotFoundException;
import com.example.finpilot.exception.AccountNotActiveException;
import com.example.finpilot.exception.InvalidPaginationException;
import com.example.finpilot.repository.AccountRepository;
import com.example.finpilot.repository.FinancialTransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionService {

    private final AccountRepository accountRepository;
    private final FinancialTransactionRepository transactionRepository;

    public TransactionService(
            AccountRepository accountRepository,
            FinancialTransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public TransactionResponse createTransaction(TransactionRequest request) {

        // 1. Lock account row
        Account account = accountRepository
                .findByIdForUpdate(request.getAccountId())
                .orElseThrow(() ->
                        new AccountNotFoundException("Account not found"));

        // 2. Check account status
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new AccountNotActiveException(
                    "Transactions are not allowed on this account");
        }

        // 3. Update balance
        switch (request.getType()) {
            case CREDIT -> account.credit(request.getAmount());
            case DEBIT -> account.debit(request.getAmount());
        }

        // 4. Create transaction entity
        FinancialTransaction transaction = new FinancialTransaction(
                request.getAmount(),
                request.getType(),
                request.getCategory(),
                request.getDescription(),
                request.getTransactionDate(),
                account
        );

        // 5. Save transaction
        FinancialTransaction savedTransaction =
                transactionRepository.save(transaction);

        // 6. Build API response
        return new TransactionResponse(
                savedTransaction.getId(),
                account.getId(),
                savedTransaction.getAmount(),
                savedTransaction.getType(),
                savedTransaction.getCategory(),
                savedTransaction.getDescription(),
                savedTransaction.getTransactionDate(),
                savedTransaction.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public Page<TransactionResponse> getTransactionHistory(
            Long accountId,
            TransactionType type,
            int page,
            int size) {

        if (page < 0) {
            throw new InvalidPaginationException(
                    "Page number cannot be negative"
            );
        }

        if (size < 1 || size > 100) {
            throw new InvalidPaginationException(
                    "Page size must be between 1 and 100"
            );
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("transactionDate").descending()
        );

        Page<FinancialTransaction> transactions;

        if (type == null) {
            transactions = transactionRepository.findByAccountId(
                    accountId, pageable
            );
        } else {
            transactions = transactionRepository.findByAccountIdAndType(
                    accountId, type, pageable
            );
        }

        return transactions.map(transaction ->
                new TransactionResponse(
                        transaction.getId(),
                        transaction.getAccount().getId(),
                        transaction.getAmount(),
                        transaction.getType(),
                        transaction.getCategory(),
                        transaction.getDescription(),
                        transaction.getTransactionDate(),
                        transaction.getCreatedAt()
                )
        );
    }

}
