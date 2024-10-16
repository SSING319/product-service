package org.app.product.events;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CheckQuantityEvent {
    private Long productId;
    private int requestedQuantity;
}
