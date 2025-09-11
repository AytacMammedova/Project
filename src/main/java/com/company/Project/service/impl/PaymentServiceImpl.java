package com.company.Project.service.impl;


import com.company.Project.client.TransactionServiceClient;
import com.company.Project.exceptions.*;
import com.company.Project.mapper.PaymentMapper;
import com.company.Project.model.PaymentMethod;
import com.company.Project.model.PaymentStatus;
import com.company.Project.model.TransactionStatus;
import com.company.Project.model.TransactionType;
import com.company.Project.model.dto.OrderProductDto;
import com.company.Project.model.dto.PaymentDto;
import com.company.Project.model.dto.TransactionDto;
import com.company.Project.model.dto.request.PaymentAddDto;
import com.company.Project.model.dto.request.PaymentRequest;
import com.company.Project.model.dto.request.RefundRequest;
import com.company.Project.model.dto.request.TransactionAddRequestDto;
import com.company.Project.model.dto.response.AccountBalanceResponse;
import com.company.Project.model.dto.response.PaymentResponse;
import com.company.Project.model.entity.*;
import com.company.Project.repository.*;
import com.company.Project.service.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j

public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final BucketRepository bucketRepository;
    private final ProductRepository productRepository;
    private final ProductSizeRepository productSizeRepository;
    private final OrderHistoryRepository orderHistoryRepository;
    private final ProductBucketRepository productBucketRepository;
    private final TransactionServiceClient transactionServiceClient; // NEW

    @Override
    public List<PaymentDto> getPaymentList() {
        log.info("Fetching all payments");
        return paymentMapper.toPaymentDtoList(paymentRepository.findAll());
    }

    @Override
    public PaymentDto add(PaymentAddDto paymentAddDto) {
        log.info("Creating payment for bucket ID: {}", paymentAddDto.getBucketId());

        Bucket bucket = bucketRepository.findById(paymentAddDto.getBucketId())
                .orElseThrow(() -> new BucketNotFoundException("No bucket with id: " + paymentAddDto.getBucketId()));

        if (bucket.getPayment() != null) {
            throw new PaymentException("Bucket already has a payment");
        }

        // NEW: Check if user has enough balance
        try {
            AccountBalanceResponse balance = transactionServiceClient.getBalance(bucket.getUser().getId());
            if (balance.getAvailableBalance().compareTo(BigDecimal.valueOf(bucket.getAmount())) < 0) {
                throw new PaymentException("Insufficient wallet balance. Available: " +
                        balance.getAvailableBalance() + ", Required: " + bucket.getAmount());
            }
        } catch (Exception e) {
            log.error("Error checking balance: {}", e.getMessage());
            throw new PaymentException("Unable to verify wallet balance");
        }

        Payment payment = new Payment();
        payment.setPaymentNo(generatePaymentNo());
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setDate(LocalDate.now());
        payment.setPaymentMethod(paymentAddDto.getPaymentMethod());

        Payment savedPayment = paymentRepository.save(payment);
        bucket.setPayment(savedPayment);
        bucketRepository.save(bucket);
        savedPayment.setBucket(bucket);
        paymentRepository.save(savedPayment);

        log.info("Payment created successfully with ID: {}", savedPayment.getId());
        return paymentMapper.toPaymentDto(savedPayment);
    }

    @Override
    public PaymentDto getById(Long id) {
        log.info("Fetching payment with ID: {}", id);
        return paymentMapper.toPaymentDto(paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException("No payment with id: " + id)));
    }

    @Override
    @Transactional
    public PaymentDto completePayment(Long id) {
        log.info("Completing payment with ID: {}", id);

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException("No payment with id: " + id));

        if (payment.getPaymentStatus() != PaymentStatus.PENDING) {
            throw new PaymentException("Only pending payments can be completed. Current status: " + payment.getPaymentStatus());
        }

        Bucket bucket = payment.getBucket();
        payment.setAmount(bucket.getAmount());

        try {
            // Call transaction service
            PaymentRequest paymentRequest = PaymentRequest.builder()
                    .ecommerceUserId(bucket.getUser().getId())
                    .ecommerceOrderId(bucket.getBucketNo())
                    .amount(BigDecimal.valueOf(bucket.getAmount()))
                    .currency("USD")
                    .description("E-commerce purchase")
                    .paymentMethodId(payment.getPaymentMethod().name())
                    .build();

            PaymentResponse transactionResponse = transactionServiceClient.processPayment(paymentRequest);

            // Store transaction ID
            payment.setTransactionId(transactionResponse.getTransactionId());

            if ("COMPLETED".equals(transactionResponse.getStatus())) {
                payment.setPaymentStatus(PaymentStatus.SUCCESS);

                // Reduce stock (your existing logic)
                reduceStock(bucket);

                // Create order history and reset bucket
                createOrderHistoryAndResetBucket(bucket, payment);

                log.info("Payment completed successfully: {}", transactionResponse.getTransactionId());
            } else {
                payment.setPaymentStatus(PaymentStatus.FAILED);
                payment.setFailureReason(transactionResponse.getFailureReason());
                log.warn("Payment failed: {}", transactionResponse.getFailureReason());
            }

        } catch (Exception e) {
            log.error("Transaction service error: {}", e.getMessage());
            payment.setPaymentStatus(PaymentStatus.FAILED);
            payment.setFailureReason("Payment processing failed: " + e.getMessage());
        }

        Payment savedPayment = paymentRepository.save(payment);
        log.info("Payment processing completed");
        return paymentMapper.toPaymentDto(savedPayment);
    }

    @Override
    @Transactional
    public PaymentDto refundPayment(Long id) {
        log.info("Processing refund for payment ID: {}", id);

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException("No payment with id: " + id));

        if (payment.getPaymentStatus() != PaymentStatus.SUCCESS) {
            throw new PaymentException("Only successful payments can be refunded. Current status: " + payment.getPaymentStatus());
        }

        if (payment.getTransactionId() == null) {
            throw new PaymentException("No transaction ID found for refund");
        }

        try {
            // Call transaction service for refund
            RefundRequest refundRequest = RefundRequest.builder()
                    .originalTransactionId(payment.getTransactionId())
                    .refundAmount(BigDecimal.valueOf(payment.getBucket().getAmount()))
                    .reason("Customer refund request")
                    .build();

            PaymentResponse refundResponse = transactionServiceClient.processRefund(refundRequest);

            if ("COMPLETED".equals(refundResponse.getStatus())) {
                payment.setPaymentStatus(PaymentStatus.REFUNDED);

                // Restore stock (your existing logic)
                restoreStock(payment.getBucket());

                log.info("Refund completed successfully: {}", refundResponse.getTransactionId());
            } else {
                throw new PaymentException("Refund failed: " + refundResponse.getFailureReason());
            }

        } catch (Exception e) {
            log.error("Refund processing error: {}", e.getMessage());
            throw new PaymentException("Refund processing failed: " + e.getMessage());
        }

        Payment savedPayment = paymentRepository.save(payment);
        log.info("Refund processing completed");
        return paymentMapper.toPaymentDto(savedPayment);
    }

    // Your existing helper methods
    private void reduceStock(Bucket bucket) {
        log.info("Reducing stock for {} products", bucket.getProductBucketList().size());

        for (ProductBucket productBucket : bucket.getProductBucketList()) {
            Product product = productBucket.getProduct();
            int orderedQuantity = productBucket.getQuantity();

            // Reduce product stock
            int newTotalStock = product.getStock() - orderedQuantity;
            product.setStock(newTotalStock);
            productRepository.save(product);

            // Reduce size stock if applicable
            if (productBucket.getSizeName() != null) {
                productSizeRepository.findByProductIdAndSizeName(
                                product.getId(), productBucket.getSizeName())
                        .ifPresent(productSize -> {
                            int newSizeStock = productSize.getStockQuantity() - orderedQuantity;
                            productSize.setStockQuantity(newSizeStock);
                            productSize.setIsAvailable(newSizeStock > 0);
                            productSizeRepository.save(productSize);
                        });
            }

            log.info("Stock reduced for product '{}': sold {}", product.getName(), orderedQuantity);
        }
    }

    private void restoreStock(Bucket bucket) {
        log.info("Restoring stock for {} products", bucket.getProductBucketList().size());

        for (ProductBucket productBucket : bucket.getProductBucketList()) {
            Product product = productBucket.getProduct();
            int refundQuantity = productBucket.getQuantity();

            // Restore product stock
            int newTotalStock = product.getStock() + refundQuantity;
            product.setStock(newTotalStock);
            productRepository.save(product);

            // Restore size stock if applicable
            if (productBucket.getSizeName() != null) {
                productSizeRepository.findByProductIdAndSizeName(
                                product.getId(), productBucket.getSizeName())
                        .ifPresent(productSize -> {
                            int newSizeStock = productSize.getStockQuantity() + refundQuantity;
                            productSize.setStockQuantity(newSizeStock);
                            productSize.setIsAvailable(true);
                            productSizeRepository.save(productSize);
                        });
            }

            log.info("Stock restored for product '{}': returned {}", product.getName(), refundQuantity);
        }
    }

    private void createOrderHistoryAndResetBucket(Bucket bucket, Payment payment) {
        // Your existing order history logic stays the same
        log.info("Creating order history and resetting bucket {}", bucket.getId());

        // Reset bucket for new shopping
        productBucketRepository.deleteByBucketId(bucket.getId());
        bucket.setAmount(0.0);
        bucket.setOrderDate(LocalDate.now());
        bucket.setBucketNo(generateBucketNumber());
        bucket.setPayment(null);
        bucketRepository.save(bucket);

        log.info("Bucket {} reset for new shopping", bucket.getId());
    }

    private String generatePaymentNo() {
        Random random = new Random();
        StringBuilder paymentNo = new StringBuilder();
        for (int i = 0; i < 15; i++) {
            paymentNo.append(random.nextInt(10));
        }
        return paymentNo.toString();
    }

    private String generateBucketNumber() {
        Random random = new Random();
        StringBuilder bucketNumber = new StringBuilder();
        for (int i = 0; i < 15; i++) {
            bucketNumber.append(random.nextInt(10));
        }
        return bucketNumber.toString();
    }

}
