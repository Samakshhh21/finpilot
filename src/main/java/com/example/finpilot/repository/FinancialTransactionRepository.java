
package com.example.finpilot.repository;

import com.example.finpilot.entity.FinancialTransaction;
import com.example.finpilot.entity.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

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

}
