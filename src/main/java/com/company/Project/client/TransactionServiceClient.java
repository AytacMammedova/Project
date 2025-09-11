package com.company.Project.client;

import com.company.Project.model.dto.request.CustomerCreateRequest;
import com.company.Project.model.dto.request.PaymentRequest;
import com.company.Project.model.dto.request.RefundRequest;
import com.company.Project.model.dto.response.AccountBalanceResponse;
import com.company.Project.model.dto.response.CustomerResponse;
import com.company.Project.model.dto.response.PaymentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "transaction-service", url = "http://localhost:8081")
public interface TransactionServiceClient {

    @PostMapping("/api/customers")
    CustomerResponse createCustomer(@RequestBody CustomerCreateRequest request);

    @PostMapping("/api/payments/process")
    PaymentResponse processPayment(@RequestBody PaymentRequest request);

    @PostMapping("/api/payments/refund")
    PaymentResponse processRefund(@RequestBody RefundRequest request);

    @GetMapping("/api/accounts/{ecommerceUserId}/balance")
    AccountBalanceResponse getBalance(@PathVariable Long ecommerceUserId);

    @PostMapping("/api/customers/simple")
    Long createCustomerSimple(@RequestBody CustomerCreateRequest request);

}
