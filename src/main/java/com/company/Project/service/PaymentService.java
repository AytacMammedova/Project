package com.company.Project.service;

import com.company.Project.model.PaymentMethod;
import com.company.Project.model.dto.PaymentDto;
import com.company.Project.model.dto.request.PaymentAddDto;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface PaymentService {
    List<PaymentDto> getPaymentList();
    PaymentDto add(PaymentAddDto paymentAddDto);
    PaymentDto getById(Long id);
    PaymentDto completePayment(Long id);
    PaymentDto refundPayment(Long id);


}
