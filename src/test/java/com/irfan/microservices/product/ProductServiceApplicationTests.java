package com.irfan.microservices.product;

import com.irfan.microservices.product.dto.ProductRequest;
import com.irfan.microservices.product.model.Product;
import com.irfan.microservices.product.repository.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;


import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@SpringBootTest
@AutoConfigureMockMvc
class ProductServiceApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductRepository productRepository;

    @Test
    @Order(1000)
    void shouldCreateProduct() throws Exception {
        ProductRequest productRequest = this.getProductRequest();
        String productRequestString = objectMapper.writeValueAsString(productRequest);

        Product savedProduct = Product.builder()
                .id("1")
                .name("Iphone 22")
                .description("Iphone 22")
                .price(BigDecimal.valueOf(2200))
                .build();

        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        mockMvc.perform(
                          post("/api/products")
                                  .contentType(MediaType.APPLICATION_JSON)
                                  .content(productRequestString)
                        )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Iphone 22"))
                .andExpect(jsonPath("$.description").value("Iphone 22"))
                .andExpect(jsonPath("$.price").value(2200))
                .andDo(print());

        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @Order(2000)
    void shouldGetAllProducts() throws Exception {
        org.springframework.data.domain.Page<Product> emptyPage =
                new org.springframework.data.domain.PageImpl<>(java.util.Collections.emptyList());

        when(productRepository.findAll(any(org.springframework.data.domain.Pageable.class))).thenReturn(emptyPage);

        mockMvc.perform(get("/api/products")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "id")
                        .param("direction", "asc"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @Order(3000)
    void shouldGetProductById() throws Exception {
        Product product = Product.builder()
                .id("1")
                .name("Iphone 22")
                .description("Iphone 22")
                .price(BigDecimal.valueOf(2200))
                .build();

        when(productRepository.findById("1")).thenReturn(Optional.of(product));

        mockMvc.perform(get("/api/products/{id}", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Iphone 22"))
                .andDo(print());

        verify(productRepository, times(1)).findById("1");
    }

    @Test
    @Order(3500)
    void shouldReturn404WhenGetProductByIdNotFound() throws Exception {
        when(productRepository.findById("nonexistent")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/products/{id}", "nonexistent"))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @Order(4000)
    void shouldUpdateProduct() throws Exception {
        Product existingProduct = Product.builder()
                .id("1")
                .name("Iphone 22")
                .description("Iphone 22")
                .price(BigDecimal.valueOf(2200))
                .build();

        Product updatedProduct = Product.builder()
                .id("1")
                .name("Iphone 23")
                .description("Iphone 23 Pro")
                .price(BigDecimal.valueOf(2500))
                .build();

        when(productRepository.findById("1")).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        ProductRequest updateRequest = ProductRequest.builder()
                .name("Iphone 23")
                .description("Iphone 23 Pro")
                .price(BigDecimal.valueOf(2500))
                .build();
        String updateRequestString = objectMapper.writeValueAsString(updateRequest);

        mockMvc.perform(put("/api/products/{id}", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequestString))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Iphone 23"))
                .andExpect(jsonPath("$.description").value("Iphone 23 Pro"))
                .andExpect(jsonPath("$.price").value(2500))
                .andDo(print());

        verify(productRepository, times(1)).findById("1");
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @Order(4500)
    void shouldReturn404WhenUpdatingNonExistentProduct() throws Exception {
        when(productRepository.findById("nonexistent")).thenReturn(Optional.empty());

        ProductRequest updateRequest = ProductRequest.builder()
                .name("Iphone 23")
                .description("Iphone 23 Pro")
                .price(BigDecimal.valueOf(2500))
                .build();

        mockMvc.perform(put("/api/products/{id}", "nonexistent")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @Order(5000)
    void shouldDeleteProduct() throws Exception {
        Product product = Product.builder()
                .id("1")
                .name("Iphone 22")
                .description("Iphone 22")
                .price(BigDecimal.valueOf(2200))
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
    @Order(5500)
    void shouldReturn404WhenDeletingNonExistentProduct() throws Exception {
        when(productRepository.findById("nonexistent")).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/products/{id}", "nonexistent"))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    private ProductRequest getProductRequest() {
        return ProductRequest.builder()
                .name("Iphone 22")
                .description("Iphone 22")
                .price(BigDecimal.valueOf(2200))
                .build();
    }
}
