package com.irfan.microservices.product.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;



@Document(value = "product")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Schema (
        name = "Product",
        description = "Hold Product Information"
)
public class Product {
    @Id
    private String id;
    @Schema (
        name = "Product Name",
        example = "IPhone 99"
    )
    private String name;

    @Schema (
        name = "Product Desc",
        example = "OK"
    )
    private String description;

    @Schema (
        name = "Product Price",
        example = "77000"
    )
    private BigDecimal price;

}
