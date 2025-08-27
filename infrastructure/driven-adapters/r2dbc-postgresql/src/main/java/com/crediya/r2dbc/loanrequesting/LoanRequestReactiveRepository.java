package com.crediya.r2dbc.loanrequesting;

import com.crediya.r2dbc.entity.LoanRequestingEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface LoanRequestReactiveRepository extends ReactiveCrudRepository<LoanRequestingEntity, String>, ReactiveQueryByExampleExecutor<LoanRequestingEntity> {

}
