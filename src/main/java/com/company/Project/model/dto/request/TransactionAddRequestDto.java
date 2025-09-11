package com.company.Project.model.dto.request;

import com.company.Project.model.TransactionType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransactionAddRequestDto {
    private Double amount;
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;
    private String accountNumber;
    private String username;
    private String password;
}
