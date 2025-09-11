package com.company.Project.model.dto;



import com.company.Project.model.TransactionStatus;
import com.company.Project.model.TransactionType;
import lombok.Data;

@Data
public class TransactionDto {
    private Long id;
    private Double amount;
    private TransactionType transactionType;
    private TransactionStatus transactionStatus;
}
