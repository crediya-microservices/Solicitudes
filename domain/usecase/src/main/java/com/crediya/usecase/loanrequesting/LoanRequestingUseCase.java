package com.crediya.usecase.loanrequesting;

import com.crediya.model.loanrequesting.LoanRequesting;
import com.crediya.model.loanrequesting.gateways.LoanRequestingInputPort;
import com.crediya.model.loanrequesting.gateways.LoanRequestingRepository;
import com.crediya.model.loantype.LoanType;
import com.crediya.model.loantype.gateways.LoanTypeRepository;
import com.crediya.model.state.State;
import com.crediya.model.state.gateways.StateRepository;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.logging.Logger;


public class LoanRequestingUseCase implements LoanRequestingInputPort {
    private final LoanRequestingRepository loanRequestingRepository;
    private final LoanTypeRepository loanTypeRepository;
    private final StateRepository stateRepository;
    private static final Logger logger = Logger.getLogger(LoanRequestingUseCase.class.getName());
    private static final String INITIAL_STATE = "Pendiente de revisión";

    public LoanRequestingUseCase(LoanRequestingRepository loanRequestingRepository, LoanTypeRepository loanTypeRepository, StateRepository stateRepository) {
        this.loanRequestingRepository = loanRequestingRepository;
        this.loanTypeRepository = loanTypeRepository;
        this.stateRepository = stateRepository;
    }

    @Override
    public Mono<LoanRequesting> save(LoanRequesting loanRequesting) {
        logger.info("Iniciando registro de solicitud de préstamo para el usuario: " + loanRequesting.getEmail());

        return Mono.zip(
                        loanTypeRepository.getLoanTypeByName(loanRequesting.getLoanType())
                                .switchIfEmpty(Mono.error(new RuntimeException("El tipo de préstamo no existe"))),
                        stateRepository.getStateByName(INITIAL_STATE)
                                .switchIfEmpty(Mono.error(new RuntimeException("El estado inicial no existe")))
                ).flatMap(tuple -> {
                    LoanType loanTypeEntity = tuple.getT1();
                    State stateEntity = tuple.getT2();

                    validateAmount(loanRequesting.getAmount());
                    validateAmountWithinLoanType(loanRequesting.getAmount(), loanTypeEntity);

                    loanRequesting.setState(INITIAL_STATE);

                    logger.info("Validaciones superadas: tipo de préstamo y estado inicial correctos");

                    return loanRequestingRepository.Save(
                            loanRequesting,
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
