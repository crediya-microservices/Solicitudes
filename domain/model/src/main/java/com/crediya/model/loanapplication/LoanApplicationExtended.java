package com.crediya.model.loanapplication;

import java.math.BigDecimal;

public class LoanApplicationExtended {
    private final LoanApplicationWithExtras base;
    private BigDecimal interestRate;
    private BigDecimal totalMonthlyDebt;
    public LoanApplicationExtended(LoanApplicationWithExtras base) {
        this.base = base;
    }
    public LoanApplicationWithExtras getBase() {
        return base;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public BigDecimal getTotalMonthlyDebt() {
        return totalMonthlyDebt;
    }

    public void setTotalMonthlyDebt(BigDecimal totalMonthlyDebt) {
        this.totalMonthlyDebt = totalMonthlyDebt;
    }
}
