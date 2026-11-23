package com.example.finpilot.repository;


import com.example.finpilot.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByAccountNumber(String accountNumber);

    boolean existsByAccountNumber(String accountNumber);

    @Query("""
    SELECT a
    FROM Account a
    JOIN FETCH a.user
    WHERE a.id = :accountId
""")
    Optional<Account> findByIdWithUser(
            @Param("accountId") Long accountId
    );
}