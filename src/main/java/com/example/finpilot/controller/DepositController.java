
package com.example.finpilot.controller;

import com.example.finpilot.dto.request.DepositRequest;
import com.example.finpilot.dto.response.DepositResponse;
import com.example.finpilot.service.DepositService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
public class DepositController {

    private final DepositService depositService;

    public DepositController(DepositService depositService) {
        this.depositService = depositService;
    }

    @PostMapping("/{accountId}/deposit")
    public ResponseEntity<DepositResponse> deposit(
            @PathVariable Long accountId,
            @Valid @RequestBody DepositRequest request) {

        DepositResponse response =
                depositService.deposit(accountId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}
