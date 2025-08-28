package com.crediya.api.service;

import com.crediya.api.dto.UserDTO;
import com.crediya.library.client.ApiResponse;
import com.crediya.library.client.GatewayClient;
import com.crediya.model.loanapplication.LoanApplication;
import com.crediya.model.loanapplication.gateways.LoanApplicationInputPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


@Service
@RequiredArgsConstructor
@Slf4j
public class LoanApplicationService {
    private final LoanApplicationInputPort loanApplicationInputPort;
    private final GatewayClient gatewayClient;

    public Mono<LoanApplication> saveLoanRequest(LoanApplication loanApplication) {
        String url = "autenticacion/api/v1/usuarios/" + loanApplication.getEmail();
        return gatewayClient.get(url, null, new ParameterizedTypeReference<ApiResponse<UserDTO>>() {
        }).doOnNext(response -> log.info("Usuario encontrado: {}", response.getContent()))
                .doOnError(error -> log.error("Error al buscar usuario: {}", error.getMessage()))
                .flatMap(response -> loanApplicationInputPort.save(loanApplication))
                .onErrorResume(error -> {
            log.error("Fallo al obtener usuario: {}", error.getMessage());
            return Mono.error(new RuntimeException("No se pudo registrar la solicitud: " + error.getMessage()));
        });
    }

}
