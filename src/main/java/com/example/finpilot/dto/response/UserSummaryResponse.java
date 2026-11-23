package com.example.finpilot.dto.response;

public record UserSummaryResponse(
        Long id,
        String name,
        String email
) {}