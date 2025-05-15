package com.company.Project.service;

import com.company.Project.model.PaymentMethod;
import com.company.Project.model.dto.PaymentDto;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface PaymentService {
    List<PaymentDto> getPaymentList();
    PaymentDto add(Long bucketId, PaymentMethod paymentMethod);
    PaymentDto getById(Long id);
    PaymentDto changePaymentStatus(Long id,PaymentDto paymentDto);


}
