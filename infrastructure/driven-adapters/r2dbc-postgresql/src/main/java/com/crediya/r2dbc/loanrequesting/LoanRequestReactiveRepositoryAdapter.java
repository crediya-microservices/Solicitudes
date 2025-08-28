package com.crediya.r2dbc.loanrequesting;

import com.crediya.model.loanrequesting.LoanRequesting;
import com.crediya.model.loanrequesting.gateways.LoanRequestingRepository;
import com.crediya.r2dbc.entity.LoanRequestingEntity;
import com.crediya.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

@Repository
public class LoanRequestReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        LoanRequesting,
        LoanRequestingEntity,
        String,
        LoanRequestReactiveRepository
        > implements LoanRequestingRepository {

    private final TransactionalOperator transactionalOperator;

    public LoanRequestReactiveRepositoryAdapter(LoanRequestReactiveRepository repository, ObjectMapper mapper, TransactionalOperator transactionalOperator) {
        super(repository, mapper, d -> mapper.map(d, LoanRequesting.class));
        this.transactionalOperator = transactionalOperator;
    }

    @Override
    public Mono<LoanRequesting> Save(LoanRequesting loanRequesting, Integer stateId, Integer loanTypeId) {
        LoanRequestingEntity entity = mapper.map(loanRequesting, LoanRequestingEntity.class);
        entity.setStateId(stateId);
        entity.setLoanTypeId(loanTypeId);
        return this.repository.save(entity)
                .map(saved -> {
                    LoanRequesting result = mapper.map(saved, LoanRequesting.class);
                    result.setState(loanRequesting.getState());
                    result.setLoanType(loanRequesting.getLoanType());
                    return result;
                })
                .onErrorMap(e -> new RuntimeException("Error al guardar la solicitud", e))
                .as(transactionalOperator::transactional);
    }
}
