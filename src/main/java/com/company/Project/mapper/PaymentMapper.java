package com.company.Project.mapper;

import com.company.Project.model.dto.PaymentDto;
import com.company.Project.model.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel ="spring")
public interface PaymentMapper {
//    @Mapping(source = "originalBucketId", target = "bucketId")
//    @Mapping(source = "amount", target = "amount")
//    PaymentDto toPaymentDto(Payment payment);
    List<PaymentDto>toPaymentDtoList(List<Payment>paymentList);


    @Mapping(source = "bucket.id", target = "bucketId")
    @Mapping(source = "amount", target = "amount")
    PaymentDto toPaymentDto(Payment payment);

}
