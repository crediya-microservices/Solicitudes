package com.crediya.api.service;

import com.crediya.model.loanrequesting.LoanRequesting;
import com.crediya.model.loanrequesting.gateways.LoanRequestingInputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class LoanRequestingService {
    private final LoanRequestingInputPort loanRequestingInputPort;

    public Mono<LoanRequesting> saveLoanRequest(LoanRequesting loanRequesting) {
        return loanRequestingInputPort.save(loanRequesting);
    }

}
