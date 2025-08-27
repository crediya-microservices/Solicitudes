package com.crediya.api;

import com.crediya.api.dto.CreateLoanRequestDTO;
import com.crediya.api.mapper.LoanRequestMapper;
import com.crediya.api.service.LoanRequestingService;
import com.crediya.library.api.BaseHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class Handler extends BaseHandler {
    private final LoanRequestingService loanRequestingService;
    private final LoanRequestMapper loanRequestMapper;

    public Mono<ServerResponse> listenSaveLoanRequest(ServerRequest serverRequest) {
        log.debug("Recibiendo petición para crear solicitud de préstamo");

        return serverRequest.bodyToMono(CreateLoanRequestDTO.class)
                .doOnNext(dto -> log.debug("Payload recibido: {}", dto))
                .map(loanRequestMapper::toModel)
                .flatMap(loanRequestingService::saveLoanRequest)
                .map(loanRequestMapper::toResponse)
                .flatMap(loanRequestResponse -> created("Solicitud de préstamo creada exitosamente", loanRequestResponse));
    }
}
