package com.irfan.microservices.product.repository;

import com.irfan.microservices.product.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

@Component
public interface ProductRepository extends MongoRepository<Product, String> {
}
