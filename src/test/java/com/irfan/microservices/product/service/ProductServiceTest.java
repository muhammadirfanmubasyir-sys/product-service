package com.irfan.microservices.product.service;

import com.irfan.microservices.product.dto.ProductRequest;
import com.irfan.microservices.product.dto.ProductResponse;
import com.irfan.microservices.product.exception.ResourceNotFoundException;
import com.irfan.microservices.product.model.Product;
import com.irfan.microservices.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private ProductRequest productRequest;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .id("1")
                .name("iPhone 15")
                .description("Apple iPhone 15")
                .price(BigDecimal.valueOf(999.99))
                .build();

        productRequest = ProductRequest.builder()
                .name("iPhone 15")
                .description("Apple iPhone 15")
                .price(BigDecimal.valueOf(999.99))
                .build();
    }

    @Test
    void shouldCreateProductSuccessfully() {
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductResponse response = productService.createProduct(productRequest);

        assertNotNull(response);
        assertEquals("1", response.id());
        assertEquals("iPhone 15", response.name());
        assertEquals("Apple iPhone 15", response.description());
        assertEquals(BigDecimal.valueOf(999.99), response.price());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void shouldReturnAllProductsWithPagination() {
        List<Product> products = List.of(product);
        Page<Product> productPage = new PageImpl<>(products);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());

        when(productRepository.findAll(any(Pageable.class))).thenReturn(productPage);

        List<ProductResponse> responses = productService.getAllProducts(0, 10, "id", "asc");

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("iPhone 15", responses.get(0).name());
        verify(productRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void shouldReturnEmptyListWhenNoProductsExist() {
        Page<Product> emptyPage = new PageImpl<>(Collections.emptyList());
        when(productRepository.findAll(any(Pageable.class))).thenReturn(emptyPage);

        List<ProductResponse> responses = productService.getAllProducts(0, 10, "id", "asc");

        assertNotNull(responses);
        assertTrue(responses.isEmpty());
        verify(productRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void shouldReturnProductById() {
        when(productRepository.findById("1")).thenReturn(Optional.of(product));

        ProductResponse response = productService.findProductById("1");

        assertNotNull(response);
        assertEquals("1", response.id());
        assertEquals("iPhone 15", response.name());
        verify(productRepository, times(1)).findById("1");
    }

    @Test
    void shouldThrowExceptionWhenProductNotFoundById() {
        when(productRepository.findById("999")).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> productService.findProductById("999")
        );

        assertTrue(exception.getMessage().contains("999"));
        verify(productRepository, times(1)).findById("999");
    }

    @Test
    void shouldReturnProductsByName() {
        List<Product> products = List.of(product);
        when(productRepository.findByName("iPhone 15")).thenReturn(Optional.of(products));

        List<ProductResponse> responses = productService.findProductByName("iPhone 15");

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("iPhone 15", responses.get(0).name());
        verify(productRepository, times(1)).findByName("iPhone 15");
    }

    @Test
    void shouldThrowExceptionWhenProductNotFoundByName() {
        when(productRepository.findByName("NonExistent")).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> productService.findProductByName("NonExistent")
        );

        assertTrue(exception.getMessage().contains("NonExistent"));
        verify(productRepository, times(1)).findByName("NonExistent");
    }

    @Test
    void shouldUpdateProductSuccessfully() {
        ProductRequest updateRequest = ProductRequest.builder()
                .name("iPhone 15 Pro")
                .description("Apple iPhone 15 Pro Max")
                .price(BigDecimal.valueOf(1199.99))
                .build();

        Product updatedProduct = Product.builder()
                .id("1")
                .name("iPhone 15 Pro")
                .description("Apple iPhone 15 Pro Max")
                .price(BigDecimal.valueOf(1199.99))
                .build();

        when(productRepository.findById("1")).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        ProductResponse response = productService.updateProduct("1", updateRequest);

        assertNotNull(response);
        assertEquals("1", response.id());
        assertEquals("iPhone 15 Pro", response.name());
        assertEquals("Apple iPhone 15 Pro Max", response.description());
        assertEquals(BigDecimal.valueOf(1199.99), response.price());
        verify(productRepository, times(1)).findById("1");
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentProduct() {
        when(productRepository.findById("999")).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> productService.updateProduct("999", productRequest)
        );

        assertTrue(exception.getMessage().contains("999"));
        verify(productRepository, times(1)).findById("999");
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void shouldDeleteProductSuccessfully() {
        when(productRepository.findById("1")).thenReturn(Optional.of(product));
        doNothing().when(productRepository).deleteById("1");

        assertDoesNotThrow(() -> productService.deleteProductById("1"));

        verify(productRepository, times(1)).findById("1");
        verify(productRepository, times(1)).deleteById("1");
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentProduct() {
        when(productRepository.findById("999")).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> productService.deleteProductById("999")
        );

        assertTrue(exception.getMessage().contains("999"));
        verify(productRepository, times(1)).findById("999");
        verify(productRepository, never()).deleteById(anyString());
    }

    @Test
    void shouldReturnProductsWithDescendingSort() {
        List<Product> products = List.of(product);
        Page<Product> productPage = new PageImpl<>(products);

        when(productRepository.findAll(any(Pageable.class))).thenReturn(productPage);

        List<ProductResponse> responses = productService.getAllProducts(0, 10, "price", "desc");

        assertNotNull(responses);
        assertEquals(1, responses.size());
        verify(productRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void shouldReturnProductsWithMultiplePages() {
        List<Product> products = List.of(product);
        Page<Product> productPage = new PageImpl<>(products, PageRequest.of(1, 10), 20);

        when(productRepository.findAll(any(Pageable.class))).thenReturn(productPage);

        List<ProductResponse> responses = productService.getAllProducts(1, 10, "id", "asc");

        assertNotNull(responses);
        assertEquals(1, responses.size());
        verify(productRepository, times(1)).findAll(any(Pageable.class));
    }
}
