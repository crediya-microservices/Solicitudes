package com.crediya.api;

import com.crediya.api.config.LoanApplicationPath;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@RequiredArgsConstructor
public class RouterRest {
    private final LoanApplicationPath loanApplicationPath;
    @Bean
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(POST(loanApplicationPath.getRequests()), handler::listenSaveLoanRequest);
    }
}
