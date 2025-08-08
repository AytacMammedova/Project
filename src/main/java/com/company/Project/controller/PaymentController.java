package com.company.Project.controller;

import com.company.Project.model.dto.PaymentDto;
import com.company.Project.model.dto.request.PaymentAddDto;
import com.company.Project.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @GetMapping
    public List<PaymentDto> getAllPayments() {
        return paymentService.getPaymentList();
    }

    @GetMapping("/{id}")
    public PaymentDto getPaymentById(@PathVariable Long id) {
        return paymentService.getById(id);
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentDto createPayment(@Valid @RequestBody PaymentAddDto paymentAddDto) {
        return paymentService.add(paymentAddDto);
    }

    @PutMapping("/{id}/complete")
    public PaymentDto completePayment(@PathVariable Long id) {
        return paymentService.completePayment(id);
    }

    @PutMapping("/{id}/refund")
    public PaymentDto refundPayment(@PathVariable Long id) {
        return paymentService.refundPayment(id);
    }
}
