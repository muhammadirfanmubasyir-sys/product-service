package com.irfan.microservices.product;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
        info = @Info(
            title = "Product Service Application",
            description = "<bold>Irfan Jaya Corporation</bold>",
            contact   = @Contact(
                name  = "Muhamad Irfan Mubasyir",
                url   = "https://www.linkedin.com/in/m-irfan-mubasyir/",
                email = "irfan.mubasyir99@gmail.com"
            ),
            license  = @License(
                name = "Bismillah License",
                url  = "https://www.linkedin.com/in/m-irfan-mubasyir/"
            )
        ),
        externalDocs = @ExternalDocumentation(
            description = "Got A better Job, alhamdulillah",
            url  = "https://www.linkedin.com/in/m-irfan-mubasyir/"
        )
)

public class ProductServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductServiceApplication.class, args);
	}

}
