package com.crediya.api.dto;

import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record LoanRequestDTO(
        String requestId,
        BigDecimal amount,
        Integer term,
        String email,
        String loanType,
        String identityDocument,
        String state
) {
}