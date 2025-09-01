package com.company.Project.service.impl;

import com.company.Project.exceptions.*;
import com.company.Project.mapper.PaymentMapper;
import com.company.Project.model.PaymentMethod;
import com.company.Project.model.PaymentStatus;
import com.company.Project.model.dto.PaymentDto;
import com.company.Project.model.dto.request.PaymentAddDto;
import com.company.Project.model.entity.*;
import com.company.Project.repository.BucketRepository;
import com.company.Project.repository.PaymentRepository;
import com.company.Project.repository.ProductRepository;
import com.company.Project.repository.ProductSizeRepository;
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
    private  final ProductSizeRepository productSizeRepository;
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
            throw new PaymentException("Bucket already has a payment with ID: " + bucket.getPayment().getId());
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
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException("No payment with id: " + id));

        if (payment.getPaymentStatus() != PaymentStatus.PENDING) {
            throw new PaymentException("Only pending payments can be completed. Current status: " + payment.getPaymentStatus());
        }

        Bucket bucket = payment.getBucket();
        if (bucket != null && bucket.getProductBucketList() != null) {
            for (ProductBucket productBucket : bucket.getProductBucketList()) {

                if (productBucket.getSizeName() != null) {
                    ProductSize productSize = productSizeRepository.findByProductIdAndSizeName(
                                    productBucket.getProduct().getId().intValue(),
                                    productBucket.getSizeName()
                            ).orElseThrow(() -> new OutOfStockException(
                                    "Size " + productBucket.getSizeName() + " not found"));

                    int orderedQuantity = productBucket.getQuantity();

                    if (productSize.getStockQuantity() < orderedQuantity) {
                        throw new OutOfStockException(
                                "Insufficient stock for product: " + productBucket.getProduct().getName() +
                                        " size " + productBucket.getSizeName() +
                                        ". Available: " + productSize.getStockQuantity() +
                                        ", Ordered: " + orderedQuantity
                        );
                    }

                    int newSizeStock = productSize.getStockQuantity() - orderedQuantity;
                    productSize.setStockQuantity(newSizeStock);

                    productSize.setIsAvailable(newSizeStock > 0);
                    productSizeRepository.save(productSize);

                    log.info("Size stock updated for product '{}' size '{}': {} -> {} (sold: {})",
                            productBucket.getProduct().getName(),
                            productBucket.getSizeName(),
                            productSize.getStockQuantity() + orderedQuantity,
                            newSizeStock,
                            orderedQuantity
                    );
                }

                Product product = productBucket.getProduct();
                int orderedQuantity = productBucket.getQuantity();
                int newTotalStock = product.getStock() - orderedQuantity;
                product.setStock(newTotalStock);
                productRepository.save(product);

                log.info("Total stock updated for product '{}': {} -> {} (sold: {})",
                        product.getName(),
                        product.getStock() + orderedQuantity,
                        newTotalStock,
                        orderedQuantity
                );
            }
        }
        if (bucket != null && bucket.getProductBucketList() != null) {
            for (ProductBucket productBucket : bucket.getProductBucketList()) {
                syncProductTotalStock(productBucket.getProduct().getId().intValue());
            }
        }
        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        Payment savedPayment = paymentRepository.save(payment);

        if (bucket != null) {
            bucket.setIsActive(false);
            bucketRepository.save(bucket);
        }


        log.info("Payment completed successfully for ID: {}", id);
        return paymentMapper.toPaymentDto(savedPayment);
    }



    @Override
    public PaymentDto refundPayment(Long id) {
        log.info("Refunding payment with ID: {}", id);
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException("No payment with id: " + id));

        if (payment.getPaymentStatus() != PaymentStatus.SUCCESS) {
            throw new PaymentException("Only successful payments can be refunded. Current status: " + payment.getPaymentStatus());
        }

        Bucket bucket = payment.getBucket();
        if (bucket != null && bucket.getProductBucketList() != null) {
            for (ProductBucket productBucket : bucket.getProductBucketList()) {

                if (productBucket.getSizeName() != null) {
                    ProductSize productSize = productSizeRepository
                            .findByProductIdAndSizeName(
                                    productBucket.getProduct().getId().intValue(),
                                    productBucket.getSizeName()
                            ).orElseThrow(() -> new RuntimeException("Size not found for refund"));

                    int refundQuantity = productBucket.getQuantity();
                    int newSizeStock = productSize.getStockQuantity() + refundQuantity;
                    productSize.setStockQuantity(newSizeStock);
                    productSize.setIsAvailable(true);
                    productSizeRepository.save(productSize);

                    log.info("Size stock restored for product '{}' size '{}': {} -> {} (refunded: {})",
                            productBucket.getProduct().getName(),
                            productBucket.getSizeName(),
                            productSize.getStockQuantity() - refundQuantity,
                            newSizeStock,
                            refundQuantity
                    );
                }
                Product product = productBucket.getProduct();
                int refundQuantity = productBucket.getQuantity();
                int newTotalStock = product.getStock() + refundQuantity;
                product.setStock(newTotalStock);
                productRepository.save(product);

                log.info("Total stock restored for product '{}': {} -> {} (refunded: {})",
                        product.getName(),
                        product.getStock() - refundQuantity,
                        newTotalStock,
                        refundQuantity
                );
            }
        }
        if (bucket != null && bucket.getProductBucketList() != null) {
            for (ProductBucket productBucket : bucket.getProductBucketList()) {
                syncProductTotalStock(productBucket.getProduct().getId().intValue());
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
    private void syncProductTotalStock(Integer productId) {
        Integer totalFromSizes = productSizeRepository.getTotalStockForProduct(productId);
        Product product = productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException("Product not found: " + productId));
        if (totalFromSizes != null && !totalFromSizes.equals(product.getStock())) {
            product.setStock(totalFromSizes);
            productRepository.save(product);
            log.info("Synced total stock for product {}: {}", productId, totalFromSizes);
        }
    }
}
