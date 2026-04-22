package com.irfan.microservices.product.controller;

import com.irfan.microservices.product.dto.ProductRequest;
import com.irfan.microservices.product.dto.ProductResponse;
import com.irfan.microservices.product.model.Product;
import com.irfan.microservices.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
@Tag(
    name = "Product Management Controller",
    description = "To Create, Delete, Read and Update Product"
)
public class ProductController {

    private final ProductService productService;

    @Operation(
        summary = "Create A Product - Rest API",
        description = "Got A Best Job, Alhamdulillah!"
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse createProduct(@RequestBody ProductRequest productRequest) {
        return productService.createProduct(productRequest);
    }

    @Operation(
            summary = "Update A Product - Rest API",
            description = "Got A Best Job, Alhamdulillah!"
    )

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse updateProduct(@PathVariable("id") String productId,
                                         @RequestBody ProductRequest productRequest) {
        return productService.updateProduct(productId, productRequest);
    }

    @Operation(
            summary = "Get All Product - Rest API",
            description = "Got A Best Job, Alhamdulillah!"
    )

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ProductResponse> getAllProducts() {
        return productService.getAllProducts();
    }

    @Operation(
            summary = "Find Product By Id - Rest API",
            description = "Got A Best Job, Alhamdulillah!"
    )

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ProductResponse findProductById(@PathVariable("id") String productId) {
        return productService.findProductById(productId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteProduct(@PathVariable("id") String productId) {
        productService.deleteProductById(productId);
    }
}
