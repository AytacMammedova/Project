package com.company.Project.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponse {
    private Long customerId;
    private Long ecommerceUserId;
    private String name;
    private String email;
    private String phone;
    private String status;
    private LocalDate dateOfBirth;
    private LocalDateTime createdAt;
}
