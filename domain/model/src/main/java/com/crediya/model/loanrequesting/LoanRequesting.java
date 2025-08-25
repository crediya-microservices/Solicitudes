package com.crediya.model.loanrequesting;

import java.math.BigDecimal;

public class LoanRequesting {
    private Integer requestId;
    private BigDecimal amount;
    private Integer term;
    private String email;
    private Integer stateId;
    private Integer loanTypeId;
    private String identityDocument;

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
    public Integer getStateId() {
        return stateId;
    }
    public void setStateId(Integer stateId) {
        this.stateId = stateId;
    }
    public Integer getLoanTypeId() {
        return loanTypeId;
    }
    public void setLoanTypeId(Integer loanTypeId) {
        this.loanTypeId = loanTypeId;
    }
    public String getIdentityDocument() {
        return identityDocument;
    }
    public void setIdentityDocument(String identityDocument) {
        this.identityDocument = identityDocument;
    }
}

