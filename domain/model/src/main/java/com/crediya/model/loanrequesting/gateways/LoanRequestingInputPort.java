package com.crediya.model.loanrequesting.gateways;

import com.crediya.model.loanrequesting.LoanRequesting;
import reactor.core.publisher.Mono;

public interface LoanRequestingInputPort {
    Mono<LoanRequesting> save(LoanRequesting loanRequesting);
}
