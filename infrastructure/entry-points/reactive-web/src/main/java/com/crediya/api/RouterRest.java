package com.crediya.api;

import com.crediya.api.config.LoanApplicationPath;
import com.crediya.api.dto.CreateLoanApplicationDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@RequiredArgsConstructor
public class RouterRest {
    private final LoanApplicationPath loanApplicationPath;

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/solicitud",
                    produces = {"application/json"},
                    method = org.springframework.web.bind.annotation.RequestMethod.POST,
                    beanClass = Handler.class,
                    beanMethod = "listenSaveLoanApplication",
                    operation = @Operation(
                            operationId = "CreateLoanApplication",
                            summary = "Crear una solicitud de préstamo",
                            description = "Crea una nueva solicitud de préstamo en el sistema",
                            requestBody = @RequestBody(
                                    required = true,
                                    description = "Información del usuario a crear",
                                    content = @Content(
                                            schema = @Schema(implementation = CreateLoanApplicationDTO.class)
                                    )
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "201",
                                            description = "Solicitud de préstamo creada exitosamente",
                                            content = @Content(
                                                    schema = @Schema(implementation = CreateLoanApplicationDTO.class)
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Error de validación en los datos enviados",
                                            content = @Content(schema = @Schema(example = """
                                                    {
                                                      "status": 400,
                                                      "error": "Bad Request",
                                                      "message": "Error de validación en los datos enviados",
                                                      "path":  "/api/v1/solicitud"
                                                    }
                                                    """))
                                    ),
                                    @ApiResponse(
                                            responseCode = "500",
                                            description = "Error interno del servidor",
                                            content = @Content(schema = @Schema(example = """
                                                    {
                                                      "status": 500,
                                                      "error": "Internal Server Error",
                                                      "message": "Ocurrió un error inesperado",
                                                      "path":  "/api/v1/solicitud"
                                                    }
                                                    """))
                                    )
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(POST(loanApplicationPath.getRequests()), handler::listenSaveLoanApplication)
                .andRoute(GET(loanApplicationPath.getRequests()), handler::listenListApplicationsForReview);
    }
}
