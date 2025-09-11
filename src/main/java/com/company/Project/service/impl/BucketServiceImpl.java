package com.company.Project.service.impl;

import com.company.Project.exceptions.*;
import com.company.Project.mapper.BucketMapper;
import com.company.Project.model.dto.BucketDto;
import com.company.Project.model.dto.OrderHistoryDto;
import com.company.Project.model.dto.OrderProductDto;
import com.company.Project.model.dto.request.BucketAddDto;
import com.company.Project.model.entity.*;
import com.company.Project.repository.*;
import com.company.Project.service.BucketService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class BucketServiceImpl implements BucketService {
    private final BucketRepository bucketRepository;
    private final BucketMapper bucketMapper;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ProductBucketRepository productBucketRepository;
    private final ProductSizeRepository productSizeRepository;
    private final OrderHistoryRepository orderHistoryRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public BucketDto addProductToBucket(BucketAddDto bucketAddDto) {
        log.info("Adding item to bucket for user ID: {}", bucketAddDto.getUserId());

        User user = userRepository.findById(bucketAddDto.getUserId())
                .orElseThrow(() -> new UserNotFoundException("No user with id:" + bucketAddDto.getUserId()));

        Product product = productRepository.findById(bucketAddDto.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("No product with id: " + bucketAddDto.getProductId()));

        ProductSize productSize = productSizeRepository.findByProductIdAndSizeName(
                        bucketAddDto.getProductId(), bucketAddDto.getSizeName())
                .orElseThrow(() -> new ProductNotFoundException("Size " + bucketAddDto.getSizeName() + " not available"));

        // Validate stock
        if (productSize.getStockQuantity() < bucketAddDto.getQuantity()) {
            throw new OutOfStockException("Insufficient stock for size " + bucketAddDto.getSizeName() +
                    ". Available: " + productSize.getStockQuantity() + ", Requested: " + bucketAddDto.getQuantity());
        }

        if (product.getStock() < bucketAddDto.getQuantity()) {
            throw new OutOfStockException("Insufficient overall stock. Available: " + product.getStock() + ", Requested: " + bucketAddDto.getQuantity());
        }

        // Get or create bucket
        Bucket bucket = getOrCreateBucket(user);

        // FIXED: Check if SAME product AND SAME size exists
        Optional<ProductBucket> existingProductBucket = productBucketRepository
                .findByBucketIdAndProductIdAndSize(bucket.getId(), product.getId(), bucketAddDto.getSizeName());

        if (existingProductBucket.isPresent()) {
            // OPTION 1: Don't allow updates, force user to use update endpoint
            throw new AlreadyExistsException("Product with size " + bucketAddDto.getSizeName() + " already exists in bucket. Use update quantity endpoint to modify.");

            // OPTION 2: If you want to allow updates, uncomment this instead:
            // updateExistingProductBucket(existingProductBucket.get(), bucketAddDto, product);
            // log.info("Updated existing product: {} size: {} in bucket", product.getName(), bucketAddDto.getSizeName());
        } else {
            // Different product OR different size - add as new item
            addNewProductToBucket(bucket, product, bucketAddDto);
            log.info("Added new product: {} size: {} to bucket", product.getName(), bucketAddDto.getSizeName());
        }

        // Update stocks
        bucketRepository.flush();
        productBucketRepository.flush();

        totalAmount(bucket.getId());
        Bucket updatedBucket = getFreshBucketWithProducts(bucket.getId());

        log.info("Product added successfully. New bucket total: {}, Products count: {}",
                updatedBucket.getAmount(), updatedBucket.getProductBucketList().size());

        return bucketMapper.toBucketDto(updatedBucket);
    }
    private Bucket getFreshBucketWithProducts(Long bucketId) {
        // Get bucket
        Bucket bucket = bucketRepository.findById(bucketId)
                .orElseThrow(() -> new BucketNotFoundException("No bucket with id: " + bucketId));

        // Explicitly load products
        List<ProductBucket> products = productBucketRepository.findByBucketIdOrderBySequence(bucketId);

        // Set the products list (this ensures it's loaded for the mapper)
        bucket.setProductBucketList(products);

        return bucket;
    }

    private Bucket getOrCreateBucket(User user) {
        Optional<Bucket> optionalBucket = bucketRepository.findBucketByUserId(user.getId());

        if (optionalBucket.isPresent()) {
            return optionalBucket.get();
        } else {
            return createNewBucket(user);
        }
    }

    private Bucket createNewBucket(User user) {
        Bucket bucket = new Bucket();
        bucket.setBucketNo(generateBucketNumber());
        bucket.setOrderDate(LocalDate.now());
        bucket.setAmount(0);
        bucket.setUser(user);
        return bucketRepository.save(bucket);
    }

    // Method to update existing product bucket (same product + same size)
    private void updateExistingProductBucket(ProductBucket existingProductBucket, BucketAddDto bucketAddDto, Product product) {
        log.info("Updating existing product bucket - Product: {}, Size: {}, Current Qty: {}, Adding: {}",
                product.getName(), bucketAddDto.getSizeName(), existingProductBucket.getQuantity(), bucketAddDto.getQuantity());

        int newQuantity = existingProductBucket.getQuantity() + bucketAddDto.getQuantity();
        existingProductBucket.setQuantity(newQuantity);
        existingProductBucket.setTotalAmount(product.getPrice() * newQuantity);
        productBucketRepository.save(existingProductBucket);
    }

    // Method to add new product bucket
    private void addNewProductToBucket(Bucket bucket, Product product, BucketAddDto bucketAddDto) {
        Integer maxSequence = productBucketRepository.findMaxSequenceByBucketId(bucket.getId());
        Integer nextSequence = (maxSequence == null) ? 1 : maxSequence + 1;

        ProductBucket productBucket = new ProductBucket();
        productBucket.setBucketSequence(nextSequence);
        productBucket.setBucket(bucket);
        productBucket.setProduct(product);
        productBucket.setQuantity(bucketAddDto.getQuantity());
        productBucket.setSizeName(bucketAddDto.getSizeName());

//        double calculatedTotal = product.getPrice() * bucketAddDto.getQuantity();
//        log.info("=== CALCULATING TOTAL ===");
//        log.info("Product price: {}", product.getPrice());
//        log.info("Quantity: {}", bucketAddDto.getQuantity());
//        log.info("Calculated total: {}", calculatedTotal);

        productBucket.setTotalAmount(product.getPrice() * bucketAddDto.getQuantity());
        productBucketRepository.save(productBucket);
    }

    @Override
    public BucketDto updateProductQuantity(Long bucketId, Long productId, Integer newQuantity, String sizeName) {
        log.info("UPDATING PRODUCT QUANTITY");
        log.info("Bucket ID: {}, Product ID: {}, Size: '{}', New Quantity: {}", bucketId, productId, sizeName, newQuantity);

        if (newQuantity < 1) {
            throw new IllegalArgumentException("Quantity must be at least 1");
        }

        // Verify bucket exists
        Bucket bucket = bucketRepository.findById(bucketId)
                .orElseThrow(() -> new BucketNotFoundException("No bucket with ID: " + bucketId));

        // Find the ProductBucket using your repository method
        Optional<ProductBucket> productBucketOpt = productBucketRepository
                .findByBucketIdAndProductIdAndSize(bucketId, productId, sizeName);

        if (productBucketOpt.isEmpty()) {
            log.info("PRODUCT NOT FOUND");
            log.info("Searching for: Bucket ID: {}, Product ID: {}, Size: '{}'", bucketId, productId, sizeName);

            // Show what products exist for debugging
            List<ProductBucket> sameProductId = productBucketRepository.findByBucketIdAndProductId(bucketId, productId);
            if (sameProductId.isEmpty()) {
                log.info("No products found with Product ID: {} in bucket {}", productId, bucketId);
            } else {
                log.info("Products with Product ID {} found:", productId);
                for (ProductBucket pb : sameProductId) {
                    log.info("  - Size: '{}', Quantity: {}", pb.getSizeName(), pb.getQuantity());
                }
            }

            throw new ProductNotFoundException(
                    String.format("Product with ID %d and size '%s' not found in bucket %d", productId, sizeName, bucketId)
            );
        }

        ProductBucket productBucket = productBucketOpt.get();
        log.info("Found ProductBucket ID: {} with current quantity: {}", productBucket.getId(), productBucket.getQuantity());

        Product product = productBucket.getProduct();

        // Stock validation
        if (product.getStock() < newQuantity) {
            throw new OutOfStockException("Insufficient stock. Available: " + product.getStock() + ", Requested: " + newQuantity);
        }

        // Store old values for logging
        int oldQuantity = productBucket.getQuantity();
        double oldTotalAmount = productBucket.getTotalAmount();

        // UPDATE: This is the key part that was probably missing or wrong
        productBucket.setQuantity(newQuantity);
        productBucket.setSizeName(sizeName);  // Make sure size name is set
        productBucket.setTotalAmount(product.getPrice() * newQuantity);

        // SAVE: Make sure to save the ProductBucket
        ProductBucket savedProductBucket = productBucketRepository.save(productBucket);
        productBucketRepository.flush();  // flush-srazu database yazsin

        log.info("ProductBucket updated:");
        log.info("  - Quantity: {} -> {}", oldQuantity, savedProductBucket.getQuantity());
        log.info("  - Total Amount: {} -> {}", oldTotalAmount, savedProductBucket.getTotalAmount());

        // UPDATE BUCKET TOTAL: Use your existing totalAmount method
        totalAmount(bucketId);

        // RETURN FRESH DATA: Get updated bucket with all products
        Bucket updatedBucket = bucketRepository.findById(bucketId).orElseThrow();
        // Make sure products are loaded for the response
        List<ProductBucket> freshProducts = productBucketRepository.findByBucketIdOrderBySequence(bucketId);
        updatedBucket.setProductBucketList(freshProducts);

        log.info(" UPDATE COMPLETED");
        log.info("Final bucket total: {}", updatedBucket.getAmount());

        return bucketMapper.toBucketDto(updatedBucket);
    }

    @Override
    public void removeProductFromBucket(Long bucketId, Long productBucketId) {
        log.info("Removing product from bucket ID: {}, product bucket ID: {}", bucketId, productBucketId);

        ProductBucket productBucket = productBucketRepository.findById(productBucketId)
                .orElseThrow(() -> new ProductNotFoundException("ProductBucket not found with ID: " + productBucketId));

        if (!productBucket.getBucket().getId().equals(bucketId)) {
            throw new IllegalArgumentException("ProductBucket does not belong to the specified bucket");
        }

        productBucketRepository.delete(productBucket);
        totalAmount(bucketId);

        log.info("Product removed from bucket successfully");
    }

    @Override
    @Transactional
    public void clearBucket(Long bucketId) {
        log.info("Clearing bucket ID: {}", bucketId);

        Bucket bucket = bucketRepository.findById(bucketId)
                .orElseThrow(() -> new BucketNotFoundException("No bucket with id: " + bucketId));

        productBucketRepository.deleteByBucketId(bucketId);
        bucket.setAmount(0.0);
        bucketRepository.save(bucket);

        log.info("Bucket cleared: {}", bucketId);
    }

    @Override
    public BucketDto getById(Long userId) {
        log.info("Getting bucket for user ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("No user with id: " + userId));

        Bucket bucket = getOrCreateBucket(user);

        return bucketMapper.toBucketDto(bucket);
    }

    @Override
    public List<OrderHistoryDto> getOrderHistory(Long userId) {
        log.info("Getting order history for user: {}", userId);

        List<OrderHistory> orderHistories = orderHistoryRepository.findByUserIdOrderByCreatedAtDesc(userId);

        return orderHistories.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private OrderHistoryDto convertToDto(OrderHistory orderHistory) {
        try {
            OrderHistoryDto dto = new OrderHistoryDto();
            dto.setId(orderHistory.getId());
            dto.setOrderNo(orderHistory.getOrderNo());
            dto.setOrderDate(orderHistory.getOrderDate());
            dto.setAmount(orderHistory.getAmount());
            dto.setPaymentId(orderHistory.getPaymentId());
            dto.setCreatedAt(orderHistory.getCreatedAt());

            List<OrderProductDto> products = objectMapper.readValue(
                    orderHistory.getProductsJson(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, OrderProductDto.class)
            );
            dto.setProducts(products);

            return dto;
        } catch (Exception e) {
            log.error("Error converting order history to dto: {}", e.getMessage());
            throw new RuntimeException("Failed to convert order history", e);
        }
    }

    public void totalAmount(Long bucketId) {
        log.info("CALCULATING BUCKET TOTAL");

        // Products ayrıca yüklə
        List<ProductBucket> products = productBucketRepository.findByBucketIdOrderBySequence(bucketId);

        log.info("Found {} products for bucket {}", products.size(), bucketId);

        double total = products.stream()
                .mapToDouble(pb -> {
                    log.info("Product: {}, TotalAmount: {}", pb.getProduct().getName(), pb.getTotalAmount());
                    return pb.getTotalAmount();
                })
                .sum();

        Bucket bucket = bucketRepository.findById(bucketId)
                .orElseThrow(() -> new BucketNotFoundException("No bucket with id: " + bucketId));

        log.info("Calculated bucket total: {}", total);
        bucket.setAmount(total);
        bucketRepository.save(bucket);
        log.info("Saved bucket with amount: {}", bucket.getAmount());
    }

    private static String generateBucketNumber(){

        Random random=new Random();
        StringBuilder bucketNumber=new StringBuilder();
        for(int i=0;i<15;i++){
            bucketNumber.append(random.nextInt(10));
        }
        return bucketNumber.toString();

    }
}
