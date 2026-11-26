
package com.example.finpilot.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(
        name = "transfers",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_transfer_idempotency_key",
                        columnNames = "idempotency_key"
                )
        }
)
public class Transfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "idempotency_key",
            nullable = false,
            unique = true,
            length = 100
    )
    private String idempotencyKey;

    @Column(name = "from_account_id", nullable = false)
    private Long fromAccountId;

    @Column(name = "to_account_id", nullable = false)
    private Long toAccountId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(length = 255)
    private String description;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "debit_transaction_id")
    private Long debitTransactionId;

    @Column(name = "credit_transaction_id")
    private Long creditTransactionId;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    protected Transfer() {}

    public Transfer(
            String idempotencyKey,
            Long fromAccountId,
            Long toAccountId,
            BigDecimal amount,
            String description) {

        this.idempotencyKey = idempotencyKey;
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.amount = amount;
        this.description = description;
        this.status = "PROCESSING";
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }
    public void markCompleted(Long debitId, Long creditId) {
        this.debitTransactionId = debitId;
        this.creditTransactionId = creditId;
        this.status = "COMPLETED";
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public Long getFromAccountId() {
        return fromAccountId;
    }

    public Long getToAccountId() {
        return toAccountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public Long getDebitTransactionId() {
        return debitTransactionId;
    }

    public Long getCreditTransactionId() {
        return creditTransactionId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setTransactionIds(
            Long debitTransactionId,
            Long creditTransactionId) {
        this.debitTransactionId = debitTransactionId;
        this.creditTransactionId = creditTransactionId;
    }
}
