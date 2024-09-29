package org.app.product.request;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ProductResponse {
    private Long productId;
    private String name;
    private String description;
    private BigDecimal price;
    private String sku;
    private Integer quantityInStock;
}
