package org.app.product.events;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.*;

@Data
@Builder
@ApplicationScoped
@AllArgsConstructor
@NoArgsConstructor
public class AvailableQuantityEvent {
    private Long productId;
    private boolean isAvailable;
    private int inStock;
}
