package com.irfan.microservices.product.integration;

import com.irfan.microservices.product.dto.ProductRequest;
import com.irfan.microservices.product.model.Product;
import com.irfan.microservices.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        reset(productRepository);
    }

    @Test
    void shouldCreateProduct() throws Exception {
        ProductRequest productRequest = createProductRequest("iPhone 15", "Apple iPhone 15", 999.99);

        Product savedProduct = Product.builder()
                .id("1")
                .name("iPhone 15")
                .description("Apple iPhone 15")
                .price(BigDecimal.valueOf(999.99))
                .build();

        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("iPhone 15"))
                .andExpect(jsonPath("$.description").value("Apple iPhone 15"))
                .andExpect(jsonPath("$.price").value(999.99))
                .andDo(print());

        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void shouldGetAllProducts() throws Exception {
        Product product1 = Product.builder()
                .id("1")
                .name("iPhone 15")
                .description("Apple iPhone 15")
                .price(BigDecimal.valueOf(999.99))
                .build();

        Product product2 = Product.builder()
                .id("2")
                .name("Samsung S24")
                .description("Samsung Galaxy S24")
                .price(BigDecimal.valueOf(899.99))
                .build();

        org.springframework.data.domain.Page<Product> page =
                new org.springframework.data.domain.PageImpl<>(List.of(product1, product2));

        when(productRepository.findAll(any(org.springframework.data.domain.Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/products")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "id")
                        .param("direction", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andDo(print());

        verify(productRepository, times(1)).findAll(any(org.springframework.data.domain.Pageable.class));
    }

    @Test
    void shouldReturnEmptyListWhenNoProducts() throws Exception {
        org.springframework.data.domain.Page<Product> emptyPage =
                new org.springframework.data.domain.PageImpl<>(Collections.emptyList());

        when(productRepository.findAll(any(org.springframework.data.domain.Pageable.class))).thenReturn(emptyPage);

        mockMvc.perform(get("/api/products")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "id")
                        .param("direction", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0))
                .andDo(print());

        verify(productRepository, times(1)).findAll(any(org.springframework.data.domain.Pageable.class));
    }

    @Test
    void shouldGetProductById() throws Exception {
        Product product = Product.builder()
                .id("1")
                .name("iPhone 15")
                .description("Apple iPhone 15")
                .price(BigDecimal.valueOf(999.99))
                .build();

        when(productRepository.findById("1")).thenReturn(Optional.of(product));

        mockMvc.perform(get("/api/products/{id}", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("iPhone 15"))
                .andExpect(jsonPath("$.description").value("Apple iPhone 15"))
                .andDo(print());

        verify(productRepository, times(1)).findById("1");
    }

    @Test
    void shouldReturn404WhenProductNotFound() throws Exception {
        when(productRepository.findById("nonexistentid")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/products/{id}", "nonexistentid"))
                .andExpect(status().isNotFound())
                .andDo(print());

        verify(productRepository, times(1)).findById("nonexistentid");
    }

    @Test
    void shouldUpdateProduct() throws Exception {
        Product existingProduct = Product.builder()
                .id("1")
                .name("iPhone 15")
                .description("Apple iPhone 15")
                .price(BigDecimal.valueOf(999.99))
                .build();

        Product updatedProduct = Product.builder()
                .id("1")
                .name("iPhone 15 Pro")
                .description("Apple iPhone 15 Pro Max")
                .price(BigDecimal.valueOf(1199.99))
                .build();

        ProductRequest updateRequest = createProductRequest("iPhone 15 Pro", "Apple iPhone 15 Pro Max", 1199.99);

        when(productRepository.findById("1")).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        mockMvc.perform(put("/api/products/{id}", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("iPhone 15 Pro"))
                .andExpect(jsonPath("$.description").value("Apple iPhone 15 Pro Max"))
                .andExpect(jsonPath("$.price").value(1199.99))
                .andDo(print());

        verify(productRepository, times(1)).findById("1");
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void shouldReturn404WhenUpdatingNonExistentProduct() throws Exception {
        when(productRepository.findById("nonexistentid")).thenReturn(Optional.empty());

        ProductRequest updateRequest = createProductRequest("iPhone 15 Pro", "Apple iPhone 15 Pro Max", 1199.99);

        mockMvc.perform(put("/api/products/{id}", "nonexistentid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound())
                .andDo(print());

        verify(productRepository, times(1)).findById("nonexistentid");
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void shouldDeleteProduct() throws Exception {
        Product product = Product.builder()
                .id("1")
                .name("iPhone 15")
                .description("Apple iPhone 15")
                .price(BigDecimal.valueOf(999.99))
                .build();

        when(productRepository.findById("1")).thenReturn(Optional.of(product));
        doNothing().when(productRepository).deleteById("1");

        mockMvc.perform(delete("/api/products/{id}", "1"))
                .andExpect(status().isOk())
                .andDo(print());

        verify(productRepository, times(1)).findById("1");
        verify(productRepository, times(1)).deleteById("1");
    }

    @Test
    void shouldReturn404WhenDeletingNonExistentProduct() throws Exception {
        when(productRepository.findById("nonexistentid")).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/products/{id}", "nonexistentid"))
                .andExpect(status().isNotFound())
                .andDo(print());

        verify(productRepository, times(1)).findById("nonexistentid");
        verify(productRepository, never()).deleteById(anyString());
    }

    @Test
    void shouldReturnProductsWithPagination() throws Exception {
        List<Product> products = java.util.stream.IntStream.rangeClosed(1, 15)
                .mapToObj(i -> Product.builder()
                        .id(String.valueOf(i))
                        .name("Product " + i)
                        .description("Description " + i)
                        .price(BigDecimal.valueOf(i * 10.0))
                        .build())
                .toList();

        org.springframework.data.domain.Page<Product> page =
                new org.springframework.data.domain.PageImpl<>(
                        products.subList(0, 5),
                        org.springframework.data.domain.PageRequest.of(0, 5),
                        15);

        when(productRepository.findAll(any(org.springframework.data.domain.Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/products")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sortBy", "id")
                        .param("direction", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5))
                .andDo(print());

        verify(productRepository, times(1)).findAll(any(org.springframework.data.domain.Pageable.class));
    }

    @Test
    void shouldReturnProductsWithDescendingSort() throws Exception {
        Product cheapProduct = Product.builder()
                .id("1")
                .name("Cheap Product")
                .description("Budget option")
                .price(BigDecimal.valueOf(49.99))
                .build();

        Product expensiveProduct = Product.builder()
                .id("2")
                .name("Expensive Product")
                .description("Premium option")
                .price(BigDecimal.valueOf(1999.99))
                .build();

        org.springframework.data.domain.Page<Product> page =
                new org.springframework.data.domain.PageImpl<>(List.of(expensiveProduct, cheapProduct));

        when(productRepository.findAll(any(org.springframework.data.domain.Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/products")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "price")
                        .param("direction", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].price").value(1999.99))
                .andDo(print());

        verify(productRepository, times(1)).findAll(any(org.springframework.data.domain.Pageable.class));
    }

    private ProductRequest createProductRequest(String name, String description, double price) {
        return ProductRequest.builder()
                .name(name)
                .description(description)
                .price(BigDecimal.valueOf(price))
                .build();
    }
}
