package com.company.Project.service.impl;

import com.company.Project.exceptions.BucketNotFoundException;
import com.company.Project.exceptions.PaymentNotFoundException;
import com.company.Project.mapper.PaymentMapper;
import com.company.Project.model.PaymentMethod;
import com.company.Project.model.PaymentStatus;
import com.company.Project.model.dto.PaymentDto;
import com.company.Project.model.entity.Bucket;
import com.company.Project.model.entity.Payment;
import com.company.Project.repository.BucketRepository;
import com.company.Project.repository.PaymentRepository;
import com.company.Project.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private  final PaymentMapper paymentMapper;
    private final PaymentRepository paymentRepository;
    private final BucketRepository bucketRepository;
    @Override
    public List<PaymentDto> getPaymentList() {
        return paymentMapper.toPaymentDtoList(paymentRepository.findAll());
    }

    @Override
    public PaymentDto add(Long bucketId, PaymentMethod paymentMethod) {
        Bucket bucket=bucketRepository.findById(bucketId).orElseThrow(()->new BucketNotFoundException("No bucket with id: "+bucketId));
        Payment payment=new Payment();
        payment.setPaymentNo(UUID.randomUUID().toString());
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setDate(LocalDate.now());
        payment.setPaymentMethod(paymentMethod);
        payment.setBucket(bucket);
        paymentRepository.save(payment);
        bucket.setPayment(payment);
        bucketRepository.save(bucket);
        return paymentMapper.toPaymentDto(payment);
    }

    @Override
    public PaymentDto getById(Long id) {
        return paymentMapper.toPaymentDto(paymentRepository.findById(id).orElseThrow(()->new PaymentNotFoundException("No payment with id: "+id)));
    }

    @Override
    public PaymentDto changePaymentStatus(Long id, PaymentDto paymentDto) {
        Payment payment=paymentRepository.findById(id).orElseThrow(()->new PaymentNotFoundException("No payment with this "+id));
        payment.setPaymentStatus(paymentDto.getPaymentStatus());
        return paymentMapper.toPaymentDto(paymentRepository.save(payment));
    }
}
