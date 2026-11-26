package com.example.finpilot.repository;


import com.example.finpilot.entity.Account;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
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


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
    SELECT a
    FROM Account a
    WHERE a.id = :accountId
""")
    Optional<Account> findByIdForUpdate(
            @Param("accountId") Long accountId
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
    SELECT a
    FROM Account a
    WHERE a.id IN :accountIds
    ORDER BY a.id
""")
    List<Account> findAllByIdForUpdate(
            @Param("accountIds") Collection<Long> accountIds
    );

}