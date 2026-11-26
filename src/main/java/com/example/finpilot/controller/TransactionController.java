
package com.example.finpilot.controller;

import com.example.finpilot.dto.request.TransactionRequest;
import com.example.finpilot.dto.response.TransactionResponse;
import com.example.finpilot.entity.TransactionType;
import com.example.finpilot.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

}
