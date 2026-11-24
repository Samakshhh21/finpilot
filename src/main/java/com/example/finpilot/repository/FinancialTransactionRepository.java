
package com.example.finpilot.repository;

import com.example.finpilot.entity.FinancialTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FinancialTransactionRepository
        extends JpaRepository<FinancialTransaction, Long> {

    List<FinancialTransaction> findByAccountIdOrderByTransactionDateDesc(
            Long accountId
    );
}
