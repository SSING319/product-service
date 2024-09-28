package org.app.product.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductCreationRequest {
    private String name;
    private String description;
    private BigDecimal price;
    private String sku;
    private Integer quantityInStock;
    private String image;
}
