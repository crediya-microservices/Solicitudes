package com.crediya.usecase.loanrequesting;

import com.crediya.model.loanrequesting.LoanRequesting;
import com.crediya.model.loanrequesting.gateways.LoanRequestingInputPort;
import com.crediya.model.loanrequesting.gateways.LoanRequestingRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class LoanRequestingUseCase implements LoanRequestingInputPort {
    private final LoanRequestingRepository loanRequestingRepository;

    @Override
    public Mono<LoanRequesting> save(LoanRequesting loanRequesting) {
        return null;
    }
}
