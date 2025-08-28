package com.crediya.r2dbc.loanapplication;

import com.crediya.model.loanapplication.LoanApplication;
import com.crediya.model.loanapplication.gateways.LoanApplicationRepository;
import com.crediya.r2dbc.entity.LoanApplicationEntity;
import com.crediya.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

@Repository
public class LoanApplicationReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        LoanApplication,
        LoanApplicationEntity,
        String,
        LoanApplicationReactiveRepository
        > implements LoanApplicationRepository {

    private final TransactionalOperator transactionalOperator;

    public LoanApplicationReactiveRepositoryAdapter(LoanApplicationReactiveRepository repository, ObjectMapper mapper, TransactionalOperator transactionalOperator) {
        super(repository, mapper, d -> mapper.map(d, LoanApplication.class));
        this.transactionalOperator = transactionalOperator;
    }

    @Override
    public Mono<LoanApplication> Save(LoanApplication loanApplication, Integer stateId, Integer loanTypeId) {
        LoanApplicationEntity entity = mapper.map(loanApplication, LoanApplicationEntity.class);
        entity.setStateId(stateId);
        entity.setLoanTypeId(loanTypeId);
        return this.repository.save(entity)
                .map(saved -> {
                    LoanApplication result = mapper.map(saved, LoanApplication.class);
                    result.setState(loanApplication.getState());
                    result.setLoanType(loanApplication.getLoanType());
                    return result;
                })
                .onErrorMap(e -> new RuntimeException("Error al guardar la solicitud", e))
                .as(transactionalOperator::transactional);
    }
}
