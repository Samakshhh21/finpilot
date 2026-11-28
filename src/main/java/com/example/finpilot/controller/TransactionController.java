
package com.example.finpilot.controller;

import com.example.finpilot.dto.request.TransactionRequest;
import com.example.finpilot.dto.response.TransactionResponse;
import com.example.finpilot.entity.TransactionCategory;
import com.example.finpilot.entity.TransactionType;
import com.example.finpilot.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(
            @Valid @RequestBody TransactionRequest request) {

        TransactionResponse response =
                transactionService.createTransaction(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    @GetMapping
    public ResponseEntity<Page<TransactionResponse>> getTransactionHistory(

            @RequestParam Long accountId,

            @RequestParam(required = false)
            TransactionType type,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size) {

        Page<TransactionResponse> response =
                transactionService.getTransactionHistory(
                        accountId, type, page, size
                );

        return ResponseEntity.ok(response);
    }


    @GetMapping
    public ResponseEntity<Page<TransactionResponse>> searchTransactions(
            @RequestParam Long accountId,
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) TransactionCategory category,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            Instant fromDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            Instant toDate,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                transactionService.searchTransactions(
                        accountId, type, category,
                        fromDate, toDate, minAmount, maxAmount,
                        page, size
                )
        );
    }


}
