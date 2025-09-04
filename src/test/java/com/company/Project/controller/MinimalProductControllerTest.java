//package com.company.Project.controller;
//
//import com.company.Project.model.Gender;
//import com.company.Project.model.dto.ProductDto;
//import com.company.Project.model.dto.request.ProductAddDto;
//import com.company.Project.service.ProductService;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.web.context.WebApplicationContext;
//
//import java.time.LocalDate;
//import java.util.Arrays;
//import java.util.List;
//
//import static org.hamcrest.Matchers.*;
//        import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.*;
//        import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//        import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@ActiveProfiles("test")
//class ProductControllerTest {
//
//    @MockBean
//    private ProductService productService;
//
//    @Autowired
//    private WebApplicationContext context;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    private MockMvc mockMvc;
//
//    private ProductDto productDto1;
//    private ProductDto productDto2;
//    private ProductAddDto productAddDto;
//
//    @BeforeEach
//    void setUp() {
//        // Build MockMvc without security
//        mockMvc = MockMvcBuilders
//                .webAppContextSetup(context)
//                .build();
//
//        productDto1 = new ProductDto();
//        productDto1.setName("Love Bracelet");
//        productDto1.setDescription("Love Bracelet Medium");
//        productDto1.setPrice(6100.00);
//        productDto1.setStock(10);
//        productDto1.setColor("Gold");
//        productDto1.setGender(Gender.UNISEX);
//        productDto1.setImage("bracelet1.jpg");
//        productDto1.setCreatedDate(LocalDate.now());
//        productDto1.setSubTypeId(1);
//
//        productDto2 = new ProductDto();
//        productDto2.setName("Trinity Bracelet");
//        productDto2.setDescription("Trinity Bracelet Classic");
//        productDto2.setPrice(11100.00);
//        productDto2.setStock(5);
//        productDto2.setColor("Gold");
//        productDto2.setGender(Gender.FEMALE);
//        productDto2.setImage("bracelet2.jpg");
//        productDto2.setCreatedDate(LocalDate.now());
//        productDto2.setSubTypeId(1);
//
//        productAddDto = new ProductAddDto();
//        productAddDto.setName("New Product");
//        productAddDto.setDescription("New product description");
//        productAddDto.setPrice(1000.00);
//        productAddDto.setStock(15);
//        productAddDto.setColor("White");
//        productAddDto.setGender(Gender.MALE);
//        productAddDto.setImage("newproduct.jpg");
//        productAddDto.setSubTypeId(1);
//    }
//
//    @Test
//    void getProductsList_ShouldReturnProductList() throws Exception {
//        // Given
//        List<ProductDto> products = Arrays.asList(productDto1, productDto2);
//        when(productService.getProductsList()).thenReturn(products);
//
//        // When & Then
//        mockMvc.perform(get("/products"))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$", hasSize(2)))
//                .andExpect(jsonPath("$[0].name", is("Love Bracelet")))
//                .andExpect(jsonPath("$[0].price", is(6100.00)));
//
//        verify(productService, times(1)).getProductsList();
//    }
//
//    @Test
//    void getById_WhenProductExists_ShouldReturnProduct() throws Exception {
//        // Given
//        Integer productId = 1;
//        when(productService.getById(productId)).thenReturn(productDto1);
//
//        // When & Then
//        mockMvc.perform(get("/products/{id}", productId))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.name", is("Love Bracelet")))
//                .andExpect(jsonPath("$.price", is(6100.00)));
//
//        verify(productService, times(1)).getById(productId);
//    }
//
//    @Test
//    void add_WithValidData_ShouldCreateProduct() throws Exception {
//        // Given
//        when(productService.add(any(ProductAddDto.class))).thenReturn(productDto1);
//
//        // When & Then
//        mockMvc.perform(post("/products")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(productAddDto)))
//                .andDo(print())
//                .andExpect(status().isCreated())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.name", is("Love Bracelet")));
//
//        verify(productService, times(1)).add(any(ProductAddDto.class));
//    }
//
//    @Test
//    void update_WithValidData_ShouldUpdateProduct() throws Exception {
//        // Given
//        Integer productId = 1;
//        ProductDto updatedProduct = new ProductDto();
//        updatedProduct.setName("Updated Product");
//        updatedProduct.setPrice(5000.00);
//
//        when(productService.update(eq(productId), any(ProductAddDto.class))).thenReturn(updatedProduct);
//
//        // When & Then
//        mockMvc.perform(put("/products/{id}", productId)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(productAddDto)))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.name", is("Updated Product")));
//
//        verify(productService, times(1)).update(eq(productId), any(ProductAddDto.class));
//    }
//
//    @Test
//    void delete_ShouldDeleteProduct() throws Exception {
//        // Given
//        Integer productId = 1;
//        doNothing().when(productService).delete(productId);
//
//        // When & Then
//        mockMvc.perform(delete("/products/{id}", productId))
//                .andDo(print())
//                .andExpect(status().isNoContent());
//
//        verify(productService, times(1)).delete(productId);
//    }
//}