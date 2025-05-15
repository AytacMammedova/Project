package com.company.Project.mapper;

import com.company.Project.model.dto.PaymentDto;
import com.company.Project.model.entity.Payment;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel ="spring")
public interface PaymentMapper {
    PaymentDto toPaymentDto(Payment payment);
    List<PaymentDto>toPaymentDtoList(List<Payment>paymentList);
    Payment toPayment(PaymentDto paymentDto);
}
