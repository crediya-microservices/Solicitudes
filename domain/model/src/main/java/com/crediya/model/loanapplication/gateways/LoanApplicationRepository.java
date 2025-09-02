package com.crediya.model.loanapplication.gateways;

import com.crediya.model.loanapplication.LoanApplication;
import reactor.core.publisher.Mono;

public interface LoanApplicationRepository {
    Mono<LoanApplication> save(LoanApplication loanApplication, Integer stateId, Integer loanTypeId );
}
