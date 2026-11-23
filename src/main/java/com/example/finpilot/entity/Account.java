package com.example.finpilot.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(
        name = "accounts",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_account_number",
                        columnNames = "account_number"
                )
        }
)
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_number", nullable = false, length = 20)
    private String accountNumber;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    @Column(nullable = false, length = 3)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private AccountStatus status;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    protected Account() {
    }

    public Account(
            String accountNumber,
            BigDecimal balance,
            String currency,
            User user) {
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.currency = currency;
        this.user = user;
        this.status = AccountStatus.ACTIVE;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public String getCurrency() {
        return currency;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public User getUser() {
        return user;
    }
}