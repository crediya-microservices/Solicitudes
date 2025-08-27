package com.crediya.api.mapper;

import com.crediya.api.dto.CreateLoanRequestDTO;
import com.crediya.api.dto.LoanRequestDTO;
import com.crediya.model.loanrequesting.LoanRequesting;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LoanRequestMapper {
    LoanRequestDTO toResponse(LoanRequesting loanRequesting);
    LoanRequesting toModel(CreateLoanRequestDTO createLoanRequestDTO);
}
