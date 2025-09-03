package com.company.Project.service.impl;

import com.company.Project.exceptions.*;
import com.company.Project.mapper.PaymentMapper;
import com.company.Project.model.PaymentMethod;
import com.company.Project.model.PaymentStatus;
import com.company.Project.model.dto.OrderProductDto;
import com.company.Project.model.dto.PaymentDto;
import com.company.Project.model.dto.request.PaymentAddDto;
import com.company.Project.model.entity.*;
import com.company.Project.repository.*;
import com.company.Project.service.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
    private  final PaymentMapper paymentMapper;
    private final PaymentRepository paymentRepository;
    private final BucketRepository bucketRepository;
    private final ProductRepository productRepository;
    private  final ProductSizeRepository productSizeRepository;
    private final OrderHistoryRepository orderHistoryRepository;
    private final ObjectMapper objectMapper;
    private final ProductBucketRepository productBucketRepository;
    @Override
    public List<PaymentDto> getPaymentList() {
        log.info("Fetching all payments");
        return paymentMapper.toPaymentDtoList(paymentRepository.findAll());
    }

    @Override
    public PaymentDto add(PaymentAddDto paymentAddDto) {
        log.info("Creating payment for bucket ID: {} with method: {}", paymentAddDto.getBucketId(), paymentAddDto.getPaymentMethod());

        Bucket bucket = bucketRepository.findById(paymentAddDto.getBucketId())
                .orElseThrow(() -> new BucketNotFoundException("No bucket with id: " + paymentAddDto.getBucketId()));

        if (bucket.getPayment() != null) {
            throw new PaymentException("Bucket already has a payment with ID: " + bucket.getPayment().getId());
        }

        if (bucket.getProductBucketList() == null || bucket.getProductBucketList().isEmpty()) {
            throw new PaymentException("Cannot create payment for empty bucket");
        }

        if (bucket.getAmount() <= 0) {
            throw new PaymentException("Cannot create payment for bucket with zero or negative amount");
        }
        validateStockBeforePayment(bucket);

        Payment payment = new Payment();
        payment.setPaymentNo(generatePaymentNo());
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setDate(LocalDate.now());
        payment.setPaymentMethod(paymentAddDto.getPaymentMethod());

        Payment savedPayment = paymentRepository.save(payment);
        bucket.setPayment(savedPayment);
        bucketRepository.save(bucket);
        savedPayment.setBucket(bucket);

        log.info("Payment created successfully with ID: {} and payment number: {}", savedPayment.getId(), savedPayment.getPaymentNo());
        return paymentMapper.toPaymentDto(savedPayment);
    }
    private void validateStockBeforePayment(Bucket bucket) {
        log.info("Validating stock before payment for bucket: {}", bucket.getId());

        for (ProductBucket productBucket : bucket.getProductBucketList()) {
            Product product = productBucket.getProduct();
            String sizeName = productBucket.getSizeName();
            int requestedQuantity = productBucket.getQuantity();

            // Check size stock
            if (sizeName != null) {
                ProductSize productSize = productSizeRepository.findByProductIdAndSizeName(
                                product.getId().intValue(), sizeName)
                        .orElseThrow(() -> new OutOfStockException("Size " + sizeName + " no longer available"));

                if (productSize.getStockQuantity() < requestedQuantity) {
                    throw new OutOfStockException(
                            String.format("Insufficient stock for %s size %s. Available: %d, In cart: %d",
                                    product.getName(), sizeName, productSize.getStockQuantity(), requestedQuantity));
                }
            }

            // Check total product stock
            if (product.getStock() < requestedQuantity) {
                throw new OutOfStockException(
                        String.format("Insufficient total stock for %s. Available: %d, In cart: %d",
                                product.getName(), product.getStock(), requestedQuantity));
            }
        }

        log.info("Stock validation passed - all items available");
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
        if (bucket != null && bucket.getProductBucketList() != null) {

            // MOVED HERE: Decrease stock ONLY when payment is successful
            log.info("Payment successful - now decreasing stock for {} products", bucket.getProductBucketList().size());

            for (ProductBucket productBucket : bucket.getProductBucketList()) {

                // Decrease size stock
                if (productBucket.getSizeName() != null) {
                    ProductSize productSize = productSizeRepository.findByProductIdAndSizeName(
                            productBucket.getProduct().getId().intValue(),
                            productBucket.getSizeName()
                    ).orElseThrow(() -> new OutOfStockException("Size " + productBucket.getSizeName() + " not found"));

                    int orderedQuantity = productBucket.getQuantity();

                    // Final check before decreasing stock
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

                    log.info("Stock decreased: Product '{}' size '{}': {} -> {} (sold: {})",
                            productBucket.getProduct().getName(),
                            productBucket.getSizeName(),
                            productSize.getStockQuantity() + orderedQuantity,
                            newSizeStock,
                            orderedQuantity
                    );
                }

                // Decrease total product stock
                Product product = productBucket.getProduct();
                int orderedQuantity = productBucket.getQuantity();

                if (product.getStock() < orderedQuantity) {
                    throw new OutOfStockException(
                            "Insufficient total stock for product: " + product.getName() +
                                    ". Available: " + product.getStock() +
                                    ", Ordered: " + orderedQuantity
                    );
                }

                int newTotalStock = product.getStock() - orderedQuantity;
                product.setStock(newTotalStock);
                productRepository.save(product);

                log.info("Total stock decreased: Product '{}': {} -> {} (sold: {})",
                        product.getName(),
                        product.getStock() + orderedQuantity,
                        newTotalStock,
                        orderedQuantity
                );
            }

            // Sync stock levels
            for (ProductBucket productBucket : bucket.getProductBucketList()) {
                syncProductTotalStock(productBucket.getProduct().getId().intValue());
            }
        }

        // Update payment status
        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        Payment savedPayment = paymentRepository.save(payment);

        // Create order history and reset bucket
        if (bucket != null) {
            createOrderHistoryAndResetBucket(bucket, savedPayment);
        }

        log.info("Payment completed successfully - stock decreased and order created");
        return paymentMapper.toPaymentDto(savedPayment);
    }

    private void createOrderHistoryAndResetBucket(Bucket bucket, Payment payment) {
        log.info("Creating order history and resetting bucket {}", bucket.getId());

        // 1. Create order history record BEFORE resetting bucket
        createOrderHistory(bucket, payment);

        // 2. Reset bucket for new shopping
        resetBucketForNewShopping(bucket);

        log.info("Order history created and bucket reset successfully");
    }

    private void createOrderHistory(Bucket bucket, Payment payment) {
        try {
            // Convert products to JSON
            List<OrderProductDto> orderProducts = bucket.getProductBucketList().stream()
                    .map(pb -> {
                        OrderProductDto dto = new OrderProductDto();
                        dto.setProductName(pb.getProduct().getName());
                        dto.setQuantity(pb.getQuantity());
                        dto.setSizeName(pb.getSizeName());
                        dto.setPrice(pb.getProduct().getPrice());
                        dto.setTotalAmount(pb.getTotalAmount());
                        return dto;
                    })
                    .collect(Collectors.toList());

            String productsJson = objectMapper.writeValueAsString(orderProducts);

            // Create order history
            OrderHistory orderHistory = new OrderHistory();
            orderHistory.setOrderNo(bucket.getBucketNo());
            orderHistory.setOrderDate(bucket.getOrderDate());
            orderHistory.setAmount(bucket.getAmount());
            orderHistory.setPaymentId(payment.getId());
            orderHistory.setUserId(bucket.getUser().getId());
            orderHistory.setProductsJson(productsJson);
            orderHistory.setCreatedAt(LocalDateTime.now());

            orderHistoryRepository.save(orderHistory);
            log.info("Order history created for user {}", bucket.getUser().getId());

        } catch (Exception e) {
            log.error("Error creating order history: {}", e.getMessage());
            throw new RuntimeException("Failed to create order history", e);
        }
    }

    private void resetBucketForNewShopping(Bucket bucket) {
        log.info("Resetting bucket {} for new shopping", bucket.getId());

        // Clear all products from bucket
        productBucketRepository.deleteByBucketId(bucket.getId());

        // Reset bucket properties
        bucket.setAmount(0.0);
        bucket.setOrderDate(LocalDate.now());
        bucket.setBucketNo(generateBucketNumber());
        bucket.setPayment(null); // CRITICAL: Remove payment reference

        bucketRepository.save(bucket);
        log.info("Bucket {} reset for new shopping", bucket.getId());
    }

    @Override
    public PaymentDto refundPayment(Long id) {
        log.info("Refunding payment with ID: {}", id);

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException("No payment with id: " + id));

        if (payment.getPaymentStatus() != PaymentStatus.SUCCESS) {
            throw new PaymentException("Only successful payments can be refunded. Current status: " + payment.getPaymentStatus());
        }

        // REFUND: Restore stock when refunding
        Bucket bucket = payment.getBucket();
        if (bucket != null && bucket.getProductBucketList() != null) {
            log.info("Refunding - restoring stock for {} products", bucket.getProductBucketList().size());

            for (ProductBucket productBucket : bucket.getProductBucketList()) {

                // Restore size stock
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

                    log.info("Stock restored: Product '{}' size '{}': {} -> {} (refunded: {})",
                            productBucket.getProduct().getName(),
                            productBucket.getSizeName(),
                            productSize.getStockQuantity() - refundQuantity,
                            newSizeStock,
                            refundQuantity
                    );
                }

                // Restore total product stock
                Product product = productBucket.getProduct();
                int refundQuantity = productBucket.getQuantity();
                int newTotalStock = product.getStock() + refundQuantity;
                product.setStock(newTotalStock);
                productRepository.save(product);

                log.info("Total stock restored: Product '{}': {} -> {} (refunded: {})",
                        product.getName(),
                        product.getStock() - refundQuantity,
                        newTotalStock,
                        refundQuantity
                );
            }

            // Sync stock levels
            for (ProductBucket productBucket : bucket.getProductBucketList()) {
                syncProductTotalStock(productBucket.getProduct().getId().intValue());
            }
        }

        payment.setPaymentStatus(PaymentStatus.REFUNDED);
        Payment savedPayment = paymentRepository.save(payment);

        log.info("Payment refunded successfully - stock restored");
        return paymentMapper.toPaymentDto(savedPayment);
    }

    private void syncProductTotalStock(Integer productId) {
        Integer totalFromSizes = productSizeRepository.getTotalStockForProduct(productId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found: " + productId));

        if (totalFromSizes != null && !totalFromSizes.equals(product.getStock())) {
            product.setStock(totalFromSizes);
            productRepository.save(product);
            log.info("Synced total stock for product {}: {}", productId, totalFromSizes);
        }
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
