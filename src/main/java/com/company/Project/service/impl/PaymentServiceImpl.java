package com.company.Project.service.impl;

import com.company.Project.exceptions.BucketNotFoundException;
import com.company.Project.exceptions.OutOfStockException;
import com.company.Project.exceptions.PaymentException;
import com.company.Project.exceptions.PaymentNotFoundException;
import com.company.Project.mapper.PaymentMapper;
import com.company.Project.model.PaymentMethod;
import com.company.Project.model.PaymentStatus;
import com.company.Project.model.dto.PaymentDto;
import com.company.Project.model.dto.request.PaymentAddDto;
import com.company.Project.model.entity.Bucket;
import com.company.Project.model.entity.Payment;
import com.company.Project.model.entity.Product;
import com.company.Project.model.entity.ProductBucket;
import com.company.Project.repository.BucketRepository;
import com.company.Project.repository.PaymentRepository;
import com.company.Project.repository.ProductRepository;
import com.company.Project.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {
    private  final PaymentMapper paymentMapper;
    private final PaymentRepository paymentRepository;
    private final BucketRepository bucketRepository;
    private final ProductRepository productRepository;
    @Override
    public List<PaymentDto> getPaymentList() {
        log.info("Fetching all payments");
        return paymentMapper.toPaymentDtoList(paymentRepository.findAll());
    }

    @Override
    public PaymentDto add(PaymentAddDto paymentAddDto) {
        log.info("Creating payment for bucket ID: {} with method: {}",paymentAddDto.getBucketId(), paymentAddDto.getPaymentMethod());

        Bucket bucket=bucketRepository.findById(paymentAddDto.getBucketId()).orElseThrow(()->new BucketNotFoundException("No bucket with id: "+paymentAddDto.getBucketId()));
        if (bucket.getPayment() != null) {
            throw new PaymentException("Bucket already has a payment associated with ID: " + bucket.getPayment().getId());
        }

        if (bucket.getProductBucketList() == null || bucket.getProductBucketList().isEmpty()) {
            throw new PaymentException("Cannot create payment for empty bucket");
        }

        if (bucket.getAmount() <= 0) {
            throw new PaymentException("Cannot create payment for bucket with zero or negative amount");
        }
        Payment payment=new Payment();
        payment.setPaymentNo(generatePaymentNo());
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setDate(LocalDate.now());
        payment.setPaymentMethod(paymentAddDto.getPaymentMethod());
        Payment savedPayment = paymentRepository.save(payment);
        bucket.setPayment(savedPayment);
        Bucket updatedBucket = bucketRepository.save(bucket);
        log.info("Bucket updated with payment_id: {}", updatedBucket.getPayment().getId());
        savedPayment.setBucket(updatedBucket);

        log.info("Payment created successfully with ID: {} and payment number: {}",
                savedPayment.getId(), savedPayment.getPaymentNo());

        return paymentMapper.toPaymentDto(savedPayment);
    }

    @Override
    public PaymentDto getById(Long id) {
        log.info("Fetching payment with ID: {}", id);
        return paymentMapper.toPaymentDto(paymentRepository.findById(id).orElseThrow(()->new PaymentNotFoundException("No payment with id: "+id)));
    }

    @Override
    public PaymentDto completePayment(Long id) {
        log.info("Completing payment with ID: {}", id);
        Payment payment = paymentRepository.findById(id).orElseThrow(() -> new PaymentNotFoundException("No payment with id: " + id));

        if (payment.getPaymentStatus() != PaymentStatus.PENDING) {
            throw new PaymentException("Only pending payments can be completed. Current status: " + payment.getPaymentStatus());
        }
        Bucket bucket = payment.getBucket();
        if (bucket != null && bucket.getProductBucketList() != null) {
            for (ProductBucket productBucket : bucket.getProductBucketList()) {
                Product product = productBucket.getProduct();
                int orderedQuantity = productBucket.getQuantity();

                // eger yene stock deyisibse
                if (product.getStock() < orderedQuantity) {
                    throw new OutOfStockException(
                            "Insufficient stock for product: " + product.getName() +
                                    ". Available: " + product.getStock() +
                                    ", Ordered: " + orderedQuantity
                    );
                }

                int newStock = product.getStock() - orderedQuantity;
                product.setStock(newStock);
                productRepository.save(product);

                log.info("Stock updated for product '{}': {} -> {} (sold: {})",
                        product.getName(),
                        product.getStock() + orderedQuantity,
                        newStock,
                        orderedQuantity
                );
            }
        }

                payment.setPaymentStatus(PaymentStatus.SUCCESS);
                Payment savedPayment = paymentRepository.save(payment);

                log.info("Payment completed successfully for ID: {}", id);
                return paymentMapper.toPaymentDto(savedPayment);
    }



    @Override
    public PaymentDto refundPayment(Long id) {
        log.info("Refunding payment with ID: {}", id);
        Payment payment = paymentRepository.findById(id).orElseThrow(()->new PaymentNotFoundException("No payment with id: "+id));

        if (payment.getPaymentStatus() != PaymentStatus.SUCCESS) {
            throw new PaymentException("Only successful payments can be refunded. Current status: " + payment.getPaymentStatus());
        }
        Bucket bucket = payment.getBucket();
        if (bucket != null && bucket.getProductBucketList() != null) {
            for (ProductBucket productBucket : bucket.getProductBucketList()) {
                Product product = productBucket.getProduct();
                int refundQuantity = productBucket.getQuantity();

                int newStock = product.getStock() + refundQuantity;
                product.setStock(newStock);
                productRepository.save(product);

                log.info("Stock restored for product '{}': {} -> {} (refunded: {})",
                        product.getName(),
                        product.getStock() - refundQuantity,
                        newStock,
                        refundQuantity
                );
            }
        }

        payment.setPaymentStatus(PaymentStatus.REFUNDED);
        Payment savedPayment = paymentRepository.save(payment);

        log.info("Payment refunded successfully for ID: {}", id);
        return paymentMapper.toPaymentDto(savedPayment);
    }

    private static String generatePaymentNo(){

        Random random=new Random();
        StringBuilder paymentNo=new StringBuilder();
        for(int i=0;i<15;i++){
            paymentNo.append(random.nextInt(10));
        }
        return paymentNo.toString();

    }
}
