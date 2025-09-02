package com.crediya.api.service;

import com.crediya.api.dto.IdentitiesRequestDTO;
import com.crediya.api.dto.LoanApplicationWithUserDTO;
import com.crediya.api.dto.UserDTO;
import com.crediya.library.client.ApiResponse;
import com.crediya.library.client.GatewayClient;
import com.crediya.model.loanapplication.LoanApplication;
import com.crediya.model.loanapplication.gateways.LoanApplicationInputPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
@Slf4j
public class LoanApplicationService {
    private final LoanApplicationInputPort loanApplicationInputPort;
    private final GatewayClient gatewayClient;
    @Value("${services.auth.base-url}")
    private String authBaseUrl;
    @Value("${services.auth.endpoints.get-user}")
    private String getUserEndpoint;

    public Mono<LoanApplication> saveLoanRequest(LoanApplication loanApplication, String token) {
        String url = getUserEndpoint + loanApplication.getEmail();
        Map<String, String> headers = Map.of("Authorization", token);
        return gatewayClient.get(authBaseUrl, url, headers, new ParameterizedTypeReference<ApiResponse<UserDTO>>() {
                }).doOnNext(response -> log.info("Usuario encontrado: {}", response.getContent()))
                .doOnError(error -> log.error("Error al buscar usuario: {}", error.getMessage()))
                .flatMap(response -> loanApplicationInputPort.save(loanApplication))
                .onErrorResume(error -> {
                    log.error("Fallo al obtener usuario: {}", error.getMessage());
                    return Mono.error(new RuntimeException("No se pudo registrar la solicitud, el usuario no fue encontrado: " + error.getMessage()));
                });
    }

    public Mono<List<LoanApplicationWithUserDTO>> listApplicationsForReview(int page, int size, String token) {
        log.info("Listando solicitudes en revisión - página {}, tamaño {}", page, size);

        return loanApplicationInputPort.findByStates(page, size)
                .flatMap(applications -> {
                    List<String> identityDocs = applications.stream()
                            .map(app -> app.getBase().getIdentityDocument())
                            .toList();

                    return fetchUsers(identityDocs, token)
                            .map(users -> applications.stream()
                                    .map(app -> {
                                        UserDTO user = users.stream()
                                                .filter(u -> u.identityDocument().equals(app.getBase().getIdentityDocument()))
                                                .findFirst()
                                                .orElse(null);
                                        return LoanApplicationWithUserDTO.of(app, user);
                                    })
                                    .toList()
                            );
                });
    }

    private Mono<List<UserDTO>> fetchUsers(List<String> identityDocs, String token) {
        String url = getUserEndpoint + "/identification-numbers";
        Map<String, String> headers = Map.of("Authorization", token);
        IdentitiesRequestDTO requestBody = new IdentitiesRequestDTO();
        requestBody.setIdentities(identityDocs);

        return gatewayClient.post(authBaseUrl,url, headers, requestBody,
                        new ParameterizedTypeReference<ApiResponse<List<UserDTO>>>() {
                        })
                .map(ApiResponse::getContent)
                .onErrorResume(error -> {
                    log.error("Error al obtener usuarios: {}", error.getMessage());
                    return Mono.just(List.of());
                });
    }

}
