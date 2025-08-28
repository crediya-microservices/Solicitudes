package com.crediya.usecase.loanrequesting;

import com.crediya.model.loanapplication.LoanApplication;
import com.crediya.model.loanapplication.gateways.LoanApplicationInputPort;
import com.crediya.model.loanapplication.gateways.LoanApplicationRepository;
import com.crediya.model.loantype.LoanType;
import com.crediya.model.loantype.gateways.LoanTypeRepository;
import com.crediya.model.state.State;
import com.crediya.model.state.gateways.StateRepository;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.logging.Logger;


public class LoanApplicationUseCase implements LoanApplicationInputPort {
    private final LoanApplicationRepository loanApplicationRepository;
    private final LoanTypeRepository loanTypeRepository;
    private final StateRepository stateRepository;
    private static final Logger logger = Logger.getLogger(LoanApplicationUseCase.class.getName());
    private static final String INITIAL_STATE = "Pendiente de revisión";

    public LoanApplicationUseCase(LoanApplicationRepository loanApplicationRepository, LoanTypeRepository loanTypeRepository, StateRepository stateRepository) {
        this.loanApplicationRepository = loanApplicationRepository;
        this.loanTypeRepository = loanTypeRepository;
        this.stateRepository = stateRepository;
    }

    @Override
    public Mono<LoanApplication> save(LoanApplication loanApplication) {
        logger.info("Iniciando registro de solicitud de préstamo para el usuario: " + loanApplication.getEmail());

        return Mono.zip(
                        loanTypeRepository.getLoanTypeByName(loanApplication.getLoanType())
                                .switchIfEmpty(Mono.error(new RuntimeException("El tipo de préstamo no existe"))),
                        stateRepository.getStateByName(INITIAL_STATE)
                                .switchIfEmpty(Mono.error(new RuntimeException("El estado inicial no existe")))
                ).flatMap(tuple -> {
                    LoanType loanTypeEntity = tuple.getT1();
                    State stateEntity = tuple.getT2();

                    validateAmount(loanApplication.getAmount());
                    validateAmountWithinLoanType(loanApplication.getAmount(), loanTypeEntity);

                    loanApplication.setState(INITIAL_STATE);

                    logger.info("Validaciones superadas: tipo de préstamo y estado inicial correctos");

                    return loanApplicationRepository.Save(
                            loanApplication,
                            stateEntity.getStateId(),
                            loanTypeEntity.getLoanTypeId()
                    );
                })
                .doOnSuccess(saved -> logger.info("Solicitud de préstamo guardada correctamente con ID: " + saved.getRequestId()))
                .doOnError(error -> logger.severe("Error al guardar la solicitud de préstamo: " + error.getMessage()));
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor que cero");
        }
    }

    private void validateAmountWithinLoanType(BigDecimal amount, LoanType loanType) {
        if (amount.compareTo(loanType.getMinAmount()) < 0 || amount.compareTo(loanType.getMaxAmount()) > 0) {
            throw new IllegalArgumentException(String.format(
                    "El monto %.2f no está dentro del rango permitido [%.2f - %.2f] para el tipo de préstamo %s",
                    amount, loanType.getMinAmount(), loanType.getMaxAmount(), loanType.getName()
            ));
        }
    }

}
