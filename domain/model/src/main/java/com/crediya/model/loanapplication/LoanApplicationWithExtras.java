package com.crediya.model.loanapplication;

import java.math.BigDecimal;

public class LoanApplicationWithExtras {
    private Integer requestId;
    private BigDecimal amount;
    private Integer term;
    private String email;
    private String identityDocument;
    private String stateId;
    private String loanTypeId;

    public Integer getRequestId() {
        return requestId;
    }
    public void setRequestId(Integer requestId) {
        this.requestId = requestId;
    }
    public BigDecimal getAmount() {
        return amount;
    }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    public Integer getTerm() {
        return term;
    }
    public void setTerm(Integer term) {
        this.term = term;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getIdentityDocument() {
        return identityDocument;
    }
    public void setIdentityDocument(String identityDocument) {
        this.identityDocument = identityDocument;
    }
    public String getStateId() {
        return stateId;
    }
    public void setStateId(String stateId) {
        this.stateId = stateId;
    }
    public String getLoanTypeId() {
        return loanTypeId;
    }
    public void setLoanTypeId(String loanTypeId) {
        this.loanTypeId = loanTypeId;
    }
}
