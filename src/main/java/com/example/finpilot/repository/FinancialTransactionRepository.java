
package com.example.finpilot.repository;

import com.example.finpilot.entity.FinancialTransaction;
import com.example.finpilot.entity.TransactionCategory;
import com.example.finpilot.entity.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public interface FinancialTransactionRepository
        extends JpaRepository<FinancialTransaction, Long> {


    Page<FinancialTransaction> findByAccountId(
            Long accountId,
            Pageable pageable
    );

    Page<FinancialTransaction> findByAccountIdAndType(
            Long accountId,
            TransactionType type,
            Pageable pageable
    );


    @Query(
            value = """
        SELECT t
        FROM FinancialTransaction t
        WHERE t.account.id = :accountId
          AND (:type IS NULL OR t.type = :type)
          AND (:category IS NULL OR t.category = :category)
          AND (:fromDate IS NULL OR t.transactionDate >= :fromDate)
          AND (:toDate IS NULL OR t.transactionDate < :toDate)
          AND (:minAmount IS NULL OR t.amount >= :minAmount)
          AND (:maxAmount IS NULL OR t.amount <= :maxAmount)
        ORDER BY t.transactionDate DESC
        """,
            countQuery = """
        SELECT COUNT(t)
        FROM FinancialTransaction t
        WHERE t.account.id = :accountId
          AND (:type IS NULL OR t.type = :type)
          AND (:category IS NULL OR t.category = :category)
          AND (:fromDate IS NULL OR t.transactionDate >= :fromDate)
          AND (:toDate IS NULL OR t.transactionDate < :toDate)
          AND (:minAmount IS NULL OR t.amount >= :minAmount)
          AND (:maxAmount IS NULL OR t.amount <= :maxAmount)
        """
    )
    Page<FinancialTransaction> searchTransactions(
            @Param("accountId") Long accountId,
            @Param("type") TransactionType type,
            @Param("category") TransactionCategory category,
            @Param("fromDate") Instant fromDate,
            @Param("toDate") Instant toDate,
            @Param("minAmount") BigDecimal minAmount,
            @Param("maxAmount") BigDecimal maxAmount,
            Pageable pageable
    );

}
