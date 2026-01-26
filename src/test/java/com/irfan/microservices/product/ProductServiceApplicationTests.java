package com.irfan.microservices.product;

import com.irfan.microservices.product.dto.ProductRequest;
import com.irfan.microservices.product.repository.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;


import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import tools.jackson.databind.ObjectMapper;


import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class ProductServiceApplicationTests {

    @Container
    static MongoDBContainer mongoDBContainer = new org.testcontainers.containers.MongoDBContainer("mongo:latest");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Test
    @Order(1000)
    void shouldCreateProduct() throws Exception {
        ProductRequest productRequest = this.getProductRequest();
        String productRequestString = objectMapper.writeValueAsString(productRequest);

        mockMvc.perform(
                          post("/api/product")
                                  .contentType(MediaType.APPLICATION_JSON)
                                  .content(productRequestString)
                        )
                .andExpect(status().isCreated())
                .andDo(print());

        Assertions.assertFalse(productRepository.findAll().isEmpty());
    }

    @Test
    @Order(2000)
    void shouldGetProduct() throws Exception {
        mockMvc.perform(get("/api/product"))
                .andExpect(status().isOk())
                .andDo(print());

        Assertions.assertFalse(productRepository.findAll().isEmpty());
    }

    private ProductRequest getProductRequest() {
        return ProductRequest.builder()
                .name("Iphone 22")
                .description("Iphone 22")
                .price(BigDecimal.valueOf(2200))
                .build();
    }


}
