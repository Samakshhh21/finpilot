package com.example.finpilot.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class AccountRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotBlank(message = "Currency is required")
    @Pattern(
            regexp = "^[A-Z]{3}$",
            message = "Currency must be a 3-letter uppercase code"
    )
    private String currency;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}