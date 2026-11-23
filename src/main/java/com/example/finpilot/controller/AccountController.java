
package com.example.finpilot.controller;

import com.example.finpilot.dto.request.AccountRequest;
import com.example.finpilot.dto.response.AccountDetailsResponse;
import com.example.finpilot.dto.response.AccountResponse;
import com.example.finpilot.entity.Account;
import com.example.finpilot.service.AccountService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(
            @Valid @RequestBody AccountRequest request) {

        Account account = accountService.createAccount(
                request.getUserId(),
                request.getCurrency()
        );

        AccountResponse response = new AccountResponse(
                account.getId(),
                account.getAccountNumber(),
                account.getBalance(),
                account.getCurrency(),
                account.getStatus(),
                account.getCreatedAt()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountDetailsResponse> getAccountDetails(
            @PathVariable Long id) {

        AccountDetailsResponse response =
                accountService.getAccountDetails(id);

        return ResponseEntity.ok(response);
    }
}
