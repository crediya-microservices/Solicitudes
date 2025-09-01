package com.crediya.r2dbc.loanapplication;

import com.crediya.r2dbc.entity.LoanApplicationEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface LoanApplicationReactiveRepository extends ReactiveCrudRepository<LoanApplicationEntity, String>, ReactiveQueryByExampleExecutor<LoanApplicationEntity> {
    @Query("SELECT * FROM solicitud WHERE id_estado <> 2 LIMIT $2 OFFSET $1")
    Flux<LoanApplicationEntity> findAllExceptStateTwo(int offset, int size);
    @Query("SELECT * FROM solicitud WHERE documento_identidad = :identityDocument AND id_estado = 2")
    Flux<LoanApplicationEntity> findApprovedByIdentity(String identityDocument);
}
