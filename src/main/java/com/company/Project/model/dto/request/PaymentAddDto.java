package com.company.Project.model.dto.request;

import com.company.Project.model.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentAddDto {
    @NotNull(message = "Bucket ID is required")
    private Long bucketId;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;
}
