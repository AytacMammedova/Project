package com.company.Project.controller;

import com.company.Project.model.Gender;
import com.company.Project.model.dto.ProductDto;
import com.company.Project.model.dto.request.ProductAddDto;
import com.company.Project.model.entity.ProductSearchCriteria;
import com.company.Project.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.mockito.ArgumentCaptor;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ProductControllerTest {

    @MockBean
    private ProductService productService;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    private ProductDto productDto1;
    private ProductDto productDto2;
    private ProductAddDto productAddDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();

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
    void getProductsList_ShouldReturnProductList() throws Exception {
        // Given
        List<ProductDto> products = Arrays.asList(productDto1, productDto2);
        when(productService.getProductsList()).thenReturn(products);

        // When & Then
        mockMvc.perform(get("/products"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Love Bracelet")))
                .andExpect(jsonPath("$[0].price", is(6100.00)))
                .andExpect(jsonPath("$[0].gender", is("UNISEX")))
                .andExpect(jsonPath("$[1].name", is("Trinity Bracelet")))
                .andExpect(jsonPath("$[1].price", is(11100.00)))
                .andExpect(jsonPath("$[1].gender", is("FEMALE")));

        verify(productService).getProductsList();
    }

    @Test
    void getById_WhenProductExists_ShouldReturnProduct() throws Exception {
        // Given
        Long productId = 1L;
        when(productService.getById(productId)).thenReturn(productDto1);

        // When & Then
        mockMvc.perform(get("/products/{id}", productId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("Love Bracelet")))
                .andExpect(jsonPath("$.price", is(6100.00)))
                .andExpect(jsonPath("$.stock", is(10)))
                .andExpect(jsonPath("$.color", is("Gold")));

        verify(productService).getById(productId);
    }

    @Test
    void getProductsBySubtypeId_ShouldReturnProductsForSubtype() throws Exception {
        // Given
        Integer subtypeId = 1;
        List<ProductDto> products = Arrays.asList(productDto1, productDto2);
        when(productService.getProductsBySubtypeId(subtypeId)).thenReturn(products);

        // When & Then
        mockMvc.perform(get("/products/subtypeId/{subtypeId}", subtypeId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].subTypeId", is(1)))
                .andExpect(jsonPath("$[1].subTypeId", is(1)));

        verify(productService).getProductsBySubtypeId(subtypeId);
    }
    @Test
    void add_WithValidData_ShouldCreateProduct() throws Exception {
        // Given
        when(productService.add(any(ProductAddDto.class))).thenReturn(productDto1);

        // When & Then
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productAddDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("Love Bracelet")))
                .andExpect(jsonPath("$.price", is(6100.00)));

        verify(productService).add(any(ProductAddDto.class));
    }

    @Test
    void add_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Given
        ProductAddDto invalidProduct = new ProductAddDto();
        invalidProduct.setName(""); // Invalid - empty name
        invalidProduct.setPrice(-100.00); // Invalid - negative price
        invalidProduct.setStock(-5); // Invalid - negative stock

        // When & Then
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidProduct)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(productService, never()).add(any(ProductAddDto.class));
    }

    @Test
    void update_WithValidData_ShouldUpdateProduct() throws Exception {
        // Given
        Long productId = 1L;
        ProductDto updatedProduct = new ProductDto();
        updatedProduct.setName("Updated Product");
        updatedProduct.setPrice(5000.00);
        updatedProduct.setStock(8);

        when(productService.update(eq(productId), any(ProductAddDto.class))).thenReturn(updatedProduct);

        // When & Then
        mockMvc.perform(put("/products/{id}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productAddDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("Updated Product")))
                .andExpect(jsonPath("$.price", is(5000.00)));

        verify(productService).update(eq(productId), any(ProductAddDto.class));
    }

    @Test
    void delete_ShouldDeleteProduct() throws Exception {
        // Given
        Long productId = 1L;
        doNothing().when(productService).delete(productId);

        // When & Then
        mockMvc.perform(delete("/products/{id}", productId))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(productService).delete(productId);
    }
    @Test
    void searchProducts_WithCriteria_ShouldReturnPagedResults() throws Exception {
        // Given
        List<ProductDto> products = Arrays.asList(productDto1);
        Page<ProductDto> productPage = new PageImpl<>(products, PageRequest.of(0, 20), 1);

        // Use ArgumentCaptor to capture the actual criteria passed
        ArgumentCaptor<ProductSearchCriteria> criteriaCaptor = ArgumentCaptor.forClass(ProductSearchCriteria.class);
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        when(productService.searchProducts(criteriaCaptor.capture(), pageableCaptor.capture()))
                .thenReturn(productPage);

        // When & Then
        mockMvc.perform(get("/products/search")
                        .param("name", "Love")
                        .param("minPrice", "5000.0")
                        .param("maxPrice", "10000.0")
                        .param("gender", "UNISEX")
                        .param("color", "Gold")
                        .param("subtypeId", "1")
                        .param("inStock", "true")
                        .param("sortBy", "price")
                        .param("sortDirection", "ASC")
                        .param("page", "0")
                        .param("size", "20"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name", is("Love Bracelet")))
                .andExpect(jsonPath("$.content[0].price", is(6100.0)));

        // Verify the captured criteria
        ProductSearchCriteria capturedCriteria = criteriaCaptor.getValue();
        assertThat(capturedCriteria.getName()).isEqualTo("Love");
        assertThat(capturedCriteria.getMinPrice()).isEqualTo(5000.0);
        assertThat(capturedCriteria.getMaxPrice()).isEqualTo(10000.0);
        assertThat(capturedCriteria.getGender()).isEqualTo(Gender.UNISEX);
        assertThat(capturedCriteria.getColor()).isEqualTo("Gold");
        assertThat(capturedCriteria.getSubtypeId()).isEqualTo(1);
        assertThat(capturedCriteria.getInStock()).isTrue();
        assertThat(capturedCriteria.getSortBy()).isEqualTo("price");
        assertThat(capturedCriteria.getSortDirection()).isEqualTo("ASC");

        verify(productService, times(1)).searchProducts(any(ProductSearchCriteria.class), any(Pageable.class));
    }

    // Alternative approach - Testing without complex criteria matching
    @Test
    void searchProducts_WithCriteria_ShouldReturnPagedResults_Alternative() throws Exception {
        // Given
        List<ProductDto> products = Arrays.asList(productDto1);
        Page<ProductDto> productPage = new PageImpl<>(products, PageRequest.of(0, 20), 1);

        when(productService.searchProducts(any(ProductSearchCriteria.class), any(Pageable.class)))
                .thenReturn(productPage);

        // When & Then
        mockMvc.perform(get("/products/search")
                        .param("name", "Love")
                        .param("minPrice", "5000.0")
                        .param("maxPrice", "10000.0")
                        .param("gender", "UNISEX")
                        .param("color", "Gold")
                        .param("subtypeId", "1")
                        .param("inStock", "true")
                        .param("sortBy", "price")
                        .param("sortDirection", "ASC"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name", is("Love Bracelet")))
                .andExpect(jsonPath("$.content[0].price", is(6100.0)));

        verify(productService, times(1)).searchProducts(any(ProductSearchCriteria.class), any(Pageable.class));
    }

    // Test with minimal parameters
    @Test
    void searchProducts_WithMinimalCriteria_ShouldReturnResults() throws Exception {
        // Given
        List<ProductDto> products = Arrays.asList(productDto1, productDto2);
        Page<ProductDto> productPage = new PageImpl<>(products, PageRequest.of(0, 20), 2);

        when(productService.searchProducts(any(ProductSearchCriteria.class), any(Pageable.class)))
                .thenReturn(productPage);

        // When & Then
        mockMvc.perform(get("/products/search"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(2)));

        verify(productService, times(1)).searchProducts(any(ProductSearchCriteria.class), any(Pageable.class));
    }

    // Test with specific name search
    @Test
    void searchProducts_WithNameFilter_ShouldReturnFilteredResults() throws Exception {
        // Given
        List<ProductDto> products = Arrays.asList(productDto1);
        Page<ProductDto> productPage = new PageImpl<>(products, PageRequest.of(0, 20), 1);

        when(productService.searchProducts(any(ProductSearchCriteria.class), any(Pageable.class)))
                .thenReturn(productPage);

        // When & Then
        mockMvc.perform(get("/products/search")
                        .param("name", "Love"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name", is("Love Bracelet")));

        verify(productService, times(1)).searchProducts(any(ProductSearchCriteria.class), any(Pageable.class));
    }

    // Test price range filtering
    @Test
    void searchProducts_WithPriceRange_ShouldReturnFilteredResults() throws Exception {
        // Given
        List<ProductDto> products = Arrays.asList(productDto1);
        Page<ProductDto> productPage = new PageImpl<>(products, PageRequest.of(0, 20), 1);

        when(productService.searchProducts(any(ProductSearchCriteria.class), any(Pageable.class)))
                .thenReturn(productPage);

        // When & Then
        mockMvc.perform(get("/products/search")
                        .param("minPrice", "5000.0")
                        .param("maxPrice", "7000.0"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].price", is(6100.0)));

        verify(productService, times(1)).searchProducts(any(ProductSearchCriteria.class), any(Pageable.class));
    }
}