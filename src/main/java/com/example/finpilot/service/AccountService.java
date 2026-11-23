
package com.example.finpilot.service;

import com.example.finpilot.entity.Account;
import com.example.finpilot.entity.User;
import com.example.finpilot.exception.UserNotFoundException;
import com.example.finpilot.repository.AccountRepository;
import com.example.finpilot.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    private final SecureRandom secureRandom = new SecureRandom();

    public AccountService(
            AccountRepository accountRepository,
            UserRepository userRepository) {

        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Account createAccount(Long userId, String currency) {

        // 1. Find the account owner
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        // 2. Generate account number
        String accountNumber = generateAccountNumber();

        // 3. Create account with initial balance zero
        Account account = new Account(
                accountNumber,
                BigDecimal.ZERO,
                currency,
                user
        );

        // 4. Persist account
        return accountRepository.save(account);
    }

    private String generateAccountNumber() {

        long number = 1000000000000000L
                + secureRandom.nextLong(9000000000000000L);

        return String.valueOf(number);
    }
}

