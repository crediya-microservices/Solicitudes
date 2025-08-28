package com.crediya.api;

import com.crediya.api.dto.CreateLoanApplicationDTO;
import com.crediya.api.mapper.LoanApplicationMapper;
import com.crediya.api.service.LoanApplicationService;
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
    private final LoanApplicationService loanApplicationService;
    private final LoanApplicationMapper loanApplicationMapper;

    public Mono<ServerResponse> listenSaveLoanApplication(ServerRequest serverRequest) {
        log.debug("Recibiendo petición para crear solicitud de préstamo");

        return serverRequest.bodyToMono(CreateLoanApplicationDTO.class)
                .doOnNext(dto -> log.debug("Payload recibido: {}", dto))
                .map(loanApplicationMapper::toModel)
                .flatMap(loanApplicationService::saveLoanRequest)
                .map(loanApplicationMapper::toResponse)
                .flatMap(loanApplicationResponse -> created("Solicitud de préstamo creada exitosamente", loanApplicationResponse));
    }
}
