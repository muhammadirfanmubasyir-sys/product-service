package com.irfan.microservices.product.repository;

import com.irfan.microservices.product.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.List;

@Component
public interface ProductRepository extends MongoRepository<Product, String> {
   // @Query("select p from Product p where p.name = ?1")
    Optional<List<Product>> findByName(String productName);
}
