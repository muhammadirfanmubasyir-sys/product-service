package com.irfan.microservices.product.service;

import com.irfan.microservices.product.dto.ProductRequest;
import com.irfan.microservices.product.dto.ProductResponse;
import com.irfan.microservices.product.model.Product;
import com.irfan.microservices.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductService {
    private final ProductRepository productRepository;

    public ProductResponse createProduct(ProductRequest productRequest) {
        Product product = Product.builder()
                .name(productRequest.name())
                .description(productRequest.description())
                .price(productRequest.price())
                .build();

        Product newProduct = productRepository.save(product);
        log.info("PRODUCT IS CREATED SUCCESSFULLY, ID = " +  newProduct.getId());

        return new ProductResponse(newProduct.getId(), newProduct.getName(), newProduct.getDescription(), newProduct.getPrice());

    }

    public List<ProductResponse> getAllProducts() {
        List<ProductResponse> productResponseList =
                productRepository.findAll()
                .stream()
                .map(product -> new ProductResponse(product.getId(), product.getName(), product.getDescription(), product.getPrice()))
                .toList();

        log.info("productResponseList SIZE =" + productResponseList.size());
        return productResponseList;
    }
}
