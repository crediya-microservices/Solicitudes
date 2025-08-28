package com.crediya.api.service;

import com.crediya.api.dto.UserDTO;
import com.crediya.library.client.ApiResponse;
import com.crediya.library.client.GatewayClient;
import com.crediya.model.loanrequesting.LoanRequesting;
import com.crediya.model.loanrequesting.gateways.LoanRequestingInputPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


@Service
@RequiredArgsConstructor
@Slf4j
public class LoanRequestingService {
    private final LoanRequestingInputPort loanRequestingInputPort;
    private final GatewayClient gatewayClient;

    public Mono<LoanRequesting> saveLoanRequest(LoanRequesting loanRequesting) {
        String url = "autenticacion/api/v1/usuarios/" + loanRequesting.getEmail();
        return gatewayClient.get(url, null, new ParameterizedTypeReference<ApiResponse<UserDTO>>() {
        }).doOnNext(response -> log.info("Usuario encontrado: {}", response.getContent()))
                .doOnError(error -> log.error("Error al buscar usuario: {}", error.getMessage()))
                .flatMap(response -> loanRequestingInputPort.save(loanRequesting))
                .onErrorResume(error -> {
            log.error("Fallo al obtener usuario: {}", error.getMessage());
            return Mono.error(new RuntimeException("No se pudo registrar la solicitud: " + error.getMessage()));
        });
    }

}
