package com.crediya.usecase.loanapplication;

import com.crediya.model.loanapplication.LoanApplication;
import com.crediya.model.loanapplication.gateways.LoanApplicationRepository;
import com.crediya.model.loantype.LoanType;
import com.crediya.model.loantype.gateways.LoanTypeRepository;
import com.crediya.model.state.State;
import com.crediya.model.state.gateways.StateRepository;
import com.crediya.usecase.loanrequesting.LoanApplicationUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class LoanApplicationUseCaseTest {

    private LoanApplicationRepository loanApplicationRepository;
    private LoanTypeRepository loanTypeRepository;
    private StateRepository stateRepository;
    private LoanApplicationUseCase useCase;

    @BeforeEach
    void setUp() {
        loanApplicationRepository = mock(LoanApplicationRepository.class);
        loanTypeRepository = mock(LoanTypeRepository.class);
        stateRepository = mock(StateRepository.class);
        useCase = new LoanApplicationUseCase(loanApplicationRepository, loanTypeRepository, stateRepository);
    }

    private LoanType buildLoanType(BigDecimal min, BigDecimal max) {
        LoanType loanType = new LoanType();
        loanType.setLoanTypeId(1);
        loanType.setName("Personal");
        loanType.setMinAmount(min);
        loanType.setMaxAmount(max);
        return loanType;
    }

    private State buildState() {
        State state = new State();
        state.setStateId(100);
        state.setName("Pendiente de revisión");
        return state;
    }

    private LoanApplication buildApplication(BigDecimal amount) {
        LoanApplication app = new LoanApplication();
        app.setRequestId(200);
        app.setEmail("user@test.com");
        app.setLoanType("Personal");
        app.setAmount(amount);
        return app;
    }

    @Test
    void shouldSaveLoanApplicationSuccessfully() {
        LoanApplication application = buildApplication(BigDecimal.valueOf(500));
        LoanType loanType = buildLoanType(BigDecimal.valueOf(100), BigDecimal.valueOf(1000));
        State state = buildState();

        when(loanTypeRepository.getLoanTypeByName("Personal")).thenReturn(Mono.just(loanType));
        when(stateRepository.getStateByName("Pendiente de revisión")).thenReturn(Mono.just(state));
        when(loanApplicationRepository.save(any(), eq(state.getStateId()), eq(loanType.getLoanTypeId())))
                .thenAnswer(invocation -> {
                    LoanApplication saved = invocation.getArgument(0);
                    saved.setRequestId(999);
                    return Mono.just(saved);
                });

        StepVerifier.create(useCase.save(application))
                .expectNextMatches(saved -> saved.getRequestId() == 999 && "Pendiente de revisión".equals(saved.getState()))
                .verifyComplete();

        verify(loanApplicationRepository, times(1)).save(any(), any(), any());
    }

    @Test
    void shouldFailWhenLoanTypeDoesNotExist() {
        LoanApplication application = buildApplication(BigDecimal.valueOf(500));
        when(loanTypeRepository.getLoanTypeByName("Personal")).thenReturn(Mono.empty());
        when(stateRepository.getStateByName("Pendiente de revisión")).thenReturn(Mono.just(buildState()));

        StepVerifier.create(useCase.save(application))
                .expectErrorMatches(e -> e instanceof RuntimeException && e.getMessage().contains("tipo de préstamo"))
                .verify();
    }

    @Test
    void shouldFailWhenStateDoesNotExist() {
        LoanApplication application = buildApplication(BigDecimal.valueOf(500));
        when(loanTypeRepository.getLoanTypeByName("Personal")).thenReturn(Mono.just(buildLoanType(BigDecimal.valueOf(100), BigDecimal.valueOf(1000))));
        when(stateRepository.getStateByName("Pendiente de revisión")).thenReturn(Mono.empty());

        StepVerifier.create(useCase.save(application))
                .expectErrorMatches(e -> e instanceof RuntimeException && e.getMessage().contains("estado inicial"))
                .verify();
    }

    @Test
    void shouldFailWhenAmountIsNull() {
        LoanApplication application = buildApplication(null);
        when(loanTypeRepository.getLoanTypeByName("Personal")).thenReturn(Mono.just(buildLoanType(BigDecimal.valueOf(100), BigDecimal.valueOf(1000))));
        when(stateRepository.getStateByName("Pendiente de revisión")).thenReturn(Mono.just(buildState()));

        StepVerifier.create(useCase.save(application))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException && e.getMessage().contains("monto debe ser mayor"))
                .verify();
    }

    @Test
    void shouldFailWhenAmountIsZeroOrNegative() {
        LoanApplication application = buildApplication(BigDecimal.ZERO);
        when(loanTypeRepository.getLoanTypeByName("Personal")).thenReturn(Mono.just(buildLoanType(BigDecimal.valueOf(100), BigDecimal.valueOf(1000))));
        when(stateRepository.getStateByName("Pendiente de revisión")).thenReturn(Mono.just(buildState()));

        StepVerifier.create(useCase.save(application))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void shouldFailWhenAmountLessThanMin() {
        LoanApplication application = buildApplication(BigDecimal.valueOf(50));
        LoanType loanType = buildLoanType(BigDecimal.valueOf(100), BigDecimal.valueOf(1000));
        when(loanTypeRepository.getLoanTypeByName("Personal")).thenReturn(Mono.just(loanType));
        when(stateRepository.getStateByName("Pendiente de revisión")).thenReturn(Mono.just(buildState()));

        StepVerifier.create(useCase.save(application))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException && e.getMessage().contains("no está dentro del rango"))
                .verify();
    }

    @Test
    void shouldFailWhenAmountGreaterThanMax() {
        LoanApplication application = buildApplication(BigDecimal.valueOf(2000));
        LoanType loanType = buildLoanType(BigDecimal.valueOf(100), BigDecimal.valueOf(1000));
        when(loanTypeRepository.getLoanTypeByName("Personal")).thenReturn(Mono.just(loanType));
        when(stateRepository.getStateByName("Pendiente de revisión")).thenReturn(Mono.just(buildState()));

        StepVerifier.create(useCase.save(application))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException && e.getMessage().contains("no está dentro del rango"))
                .verify();
    }
}

