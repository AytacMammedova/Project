package com.company.Project.service;

import com.company.Project.exceptions.ProductNotFoundException;
import com.company.Project.mapper.ProductMapper;
import com.company.Project.model.Gender;
import com.company.Project.model.dto.ProductDto;
import com.company.Project.model.dto.request.ProductAddDto;
import com.company.Project.model.entity.Product;
import com.company.Project.model.entity.SubType;
import com.company.Project.repository.ProductRepository;
import com.company.Project.repository.SubTypeRepository;
import com.company.Project.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {
    @InjectMocks
    private ProductServiceImpl productService;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private ProductMapper productMapper;
    @Mock
    private SubTypeRepository subTypeRepository;

    private Product product1;
    private Product product2;
    private ProductDto productDto1;
    private ProductDto productDto2;
    private ProductAddDto productAddDto;
    private SubType subType;

    @BeforeEach
    void setUp() {
        subType = new SubType();
        subType.setId(1);
        subType.setName("Bracelets");

        product1 = new Product();
        product1.setId(1L);
        product1.setName("Love Bracelet");
        product1.setDescription("Love Bracelet Medium");
        product1.setPrice(6100.00);
        product1.setStock(10);
        product1.setColor("Gold");
        product1.setGender(Gender.UNISEX);
        product1.setImage("bracelet1.jpg");
        product1.setCreatedDate(LocalDate.now());
        product1.setSubType(subType);

        product2 = new Product();
        product2.setId(2L);
        product2.setName("Trinity Bracelet");
        product2.setDescription("Trinity Bracelet Classic");
        product2.setPrice(11100.00);
        product2.setStock(5);
        product2.setColor("Gold");
        product2.setGender(Gender.FEMALE);
        product2.setImage("bracelet2.jpg");
        product2.setCreatedDate(LocalDate.now());
        product2.setSubType(subType);

        productDto1 = new ProductDto();
        productDto1.setName("Love Bracelet");
        productDto1.setDescription("Love Bracelet Medium");
        productDto1.setPrice(6100.00);
        productDto1.setStock(10);
        productDto1.setColor("Gold");
        productDto1.setGender(Gender.UNISEX);
        productDto1.setImage("bracelet1.jpg");
        productDto1.setSubTypeId(1);

        productDto2 = new ProductDto();
        productDto2.setName("Trinity Bracelet");
        productDto2.setDescription("Trinity Bracelet Classic");
        productDto2.setPrice(11100.00);
        productDto2.setStock(5);
        productDto2.setColor("Gold");
        productDto2.setGender(Gender.FEMALE);
        productDto2.setImage("bracelet2.jpg");
        productDto2.setSubTypeId(1);


        productAddDto = new ProductAddDto();
        productAddDto.setName("New Product");
        productAddDto.setDescription("New product description");
        productAddDto.setPrice(1000.00);
        productAddDto.setStock(15);
        productAddDto.setColor("White");
        productAddDto.setGender(Gender.MALE);
        productAddDto.setImage("newproduct.jpg");
        productAddDto.setSubTypeId(1);
    }

    @Test
    void getProductsList_ShouldReturnAllProducts() {
        List<Product> products = Arrays.asList(product1, product2);
        List<ProductDto> expectedProductDtos = Arrays.asList(productDto1, productDto2);

        when(productRepository.findAll()).thenReturn(products);
        when(productMapper.toProductDtoList(products)).thenReturn(expectedProductDtos);

        List<ProductDto> result = productService.getProductsList();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Love Bracelet");
        assertThat(result.get(1).getName()).isEqualTo("Trinity Bracelet");

        verify(productRepository, times(1)).findAll();
        verify(productMapper, times(1)).toProductDtoList(products);
    }

    @Test
    void getById_WhenProductExists_ShouldReturnProductDto() {

        Integer productId = 1;
        when(productRepository.findById(productId)).thenReturn(Optional.of(product1));
        when(productMapper.toProductDto(product1)).thenReturn(productDto1);

        ProductDto result = productService.getById(productId);

        assertThat(result.getName()).isEqualTo("Love Bracelet");
        assertThat(result.getPrice()).isEqualTo(6100.00);
        assertThat(result.getStock()).isEqualTo(10);

        verify(productRepository, times(1)).findById(productId);
        verify(productMapper, times(1)).toProductDto(product1);
    }

    @Test
    void getById_WhenProductNotExists_ShouldThrowException() {
        Integer productId = 10;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getById(productId))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("Product with id 10 not found");

        verify(productRepository, times(1)).findById(productId);
        verify(productMapper, never()).toProductDto(any());
    }

    @Test
    void getProductsBySubtypeId_ShouldReturnProductsBySubtype() {
        Integer subtypeId = 1;
        List<Product> products = Arrays.asList(product1, product2);
        List<ProductDto> expectedDtos = Arrays.asList(productDto1, productDto2);

        when(productRepository.getBySubTypeId(subtypeId)).thenReturn(products);
        when(productMapper.toProductDtoList(products)).thenReturn(expectedDtos);

        List<ProductDto> result = productService.getProductsBySubtypeId(subtypeId);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Love Bracelet");
        assertThat(result.get(1).getName()).isEqualTo("Trinity Bracelet");

        verify(productRepository, times(1)).getBySubTypeId(subtypeId);
        verify(productMapper, times(1)).toProductDtoList(products);
    }

    @Test
    void add_ShouldCreateProductSuccessfully() {
        Product newProduct = new Product();
        newProduct.setName("New Product");
        newProduct.setDescription("New product description");
        newProduct.setPrice(1000.00);
        newProduct.setStock(15);
        newProduct.setColor("White");
        newProduct.setGender(Gender.MALE);
        newProduct.setImage("newproduct.jpg");

        Product savedProduct = new Product();
        savedProduct.setId(3L);
        savedProduct.setName("New Product");
        savedProduct.setDescription("New product description");
        savedProduct.setPrice(1000.00);
        savedProduct.setStock(15);
        savedProduct.setColor("White");
        savedProduct.setGender(Gender.MALE);
        savedProduct.setImage("newproduct.jpg");
        savedProduct.setCreatedDate(LocalDate.now());
        savedProduct.setSubType(subType);

        ProductDto expectedDto = new ProductDto();
        expectedDto.setName("New Product");
        expectedDto.setDescription("New product description");
        expectedDto.setPrice(1000.00);
        expectedDto.setStock(15);
        expectedDto.setColor("White");
        expectedDto.setGender(Gender.MALE);
        expectedDto.setImage("newproduct.jpg");
        expectedDto.setSubTypeId(1);

        when(productMapper.toProduct(productAddDto)).thenReturn(newProduct);
        when(subTypeRepository.findById(productAddDto.getSubTypeId())).thenReturn(Optional.of(subType));
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);
        when(productMapper.toProductDto(savedProduct)).thenReturn(expectedDto);

        ProductDto result = productService.add(productAddDto);

        assertThat(result.getName()).isEqualTo("New Product");
        assertThat(result.getPrice()).isEqualTo(1000.00);

        verify(productMapper, times(1)).toProduct(productAddDto);
        verify(subTypeRepository, times(1)).findById(productAddDto.getSubTypeId());
        verify(productRepository, times(1)).save(any(Product.class));
        verify(productMapper, times(1)).toProductDto(savedProduct);
    }

    @Test
    void update_WhenProductExists_ShouldUpdateSuccessfully() {
        Integer productId = 1;
        ProductAddDto updateDto = new ProductAddDto();
        updateDto.setName("Updated Product");
        updateDto.setPrice(5000.00);
        updateDto.setStock(8);

        Product updatedProduct = new Product();
        updatedProduct.setId(1L);
        updatedProduct.setName("Updated Product");
        updatedProduct.setPrice(5000.00);
        updatedProduct.setStock(8);
        updatedProduct.setColor("Gold");
        updatedProduct.setGender(Gender.UNISEX);

        ProductDto expectedDto = new ProductDto();
        expectedDto.setName("Updated Product");
        expectedDto.setPrice(5000.00);
        expectedDto.setStock(8);
        expectedDto.setColor("Gold");
        expectedDto.setGender(Gender.UNISEX);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product1));
        when(productMapper.updateProduct(updateDto, product1)).thenReturn(product1);
        when(productRepository.save(product1)).thenReturn(updatedProduct);
        when(productMapper.toProductDto(updatedProduct)).thenReturn(expectedDto);

        ProductDto result = productService.update(productId, updateDto);

        assertThat(result.getName()).isEqualTo("Updated Product");
        assertThat(result.getPrice()).isEqualTo(5000.00);
        assertThat(result.getStock()).isEqualTo(8);

        verify(productRepository, times(1)).findById(productId);
        verify(productMapper, times(1)).updateProduct(updateDto, product1);
        verify(productRepository, times(1)).save(product1);
        verify(productMapper, times(1)).toProductDto(updatedProduct);
    }

    @Test
    void update_WhenProductNotExists_ShouldThrowException() {
        Integer productId = 10;
        ProductAddDto updateDto = new ProductAddDto();
        updateDto.setName("Updated Product");

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.update(productId, updateDto))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("Product with id 10 not found");

        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, never()).save(any());
    }

    @Test
    void delete_WhenProductExists_ShouldDeleteSuccessfully() {
        Integer productId = 1;
        when(productRepository.findById(productId)).thenReturn(Optional.of(product1));

        productService.delete(productId);

        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).delete(product1);
    }

    @Test
    void delete_WhenProductNotExists_ShouldThrowException() {
        Integer productId = 10;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.delete(productId))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("Product with id 10 not found");

        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, never()).delete(any());
    }
}