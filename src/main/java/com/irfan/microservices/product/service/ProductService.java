package com.irfan.microservices.product.service;

import com.irfan.microservices.product.dto.ProductRequest;
import com.irfan.microservices.product.dto.ProductResponse;
import com.irfan.microservices.product.exception.ResourceNotFoundException;
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

    public List<ProductResponse> findProductByName(String productName) {
        List<Product> lisOfProduct = productRepository.findByName(productName)
                                        .orElseThrow(() -> new ResourceNotFoundException("Product With Name: " + productName + " Is Not Found"));

        List<ProductResponse> productResponseList = lisOfProduct.stream()
                                                                .map(product -> new ProductResponse(product.getId(), product.getName(), product.getDescription(), product.getPrice()))
                                                                .toList();
        log.info("findProductByName [" + productName + "] SIZE =" + productResponseList.size());
        return productResponseList;
    }
    public ProductResponse findProductById(String productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product With Id: " + productId + " Is Not Found"));

        return new ProductResponse(product.getId(), product.getName(), product.getDescription(), product.getPrice());
    }

    public ProductResponse updateProduct(String productId, ProductRequest productRequest) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product With Id: " + productId + " Is Not Found"));

        product.setName(productRequest.name());
        product.setPrice(productRequest.price());
        product.setDescription(productRequest.description());

        Product updatedProduct = productRepository.save(product);
        log.info("PRODUCT IS UPDATED SUCCESSFULLY, ID = " +  updatedProduct.getId());

        return new ProductResponse(updatedProduct.getId(), updatedProduct.getName(), updatedProduct.getDescription(), updatedProduct.getPrice());

    }

    public void deleteProductById(String productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product With Id: " + productId + " Is Not Found"));

        productRepository.deleteById(product.getId());

    }
}
