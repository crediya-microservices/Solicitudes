package com.crediya.model.loanapplication.gateways;

import com.crediya.model.loanapplication.LoanApplication;
import com.crediya.model.loanapplication.LoanApplicationWithExtras;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface LoanApplicationRepository {
    Mono<LoanApplication> save(LoanApplication loanApplication, Integer stateId, Integer loanTypeId );
    Flux<LoanApplicationWithExtras> findByStateIds(int page, int size);
    Flux<LoanApplicationWithExtras> findApprovedByIdentity(String identityDocument);

}
