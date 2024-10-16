package org.app.product.service;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.app.product.entity.Product;
import org.app.product.events.AvailableQuantityEvent;
import org.app.product.events.CheckQuantityEvent;
import org.app.product.repository.ProductRepository;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@ApplicationScoped
@AllArgsConstructor
@RequiredArgsConstructor
public class ProductService {

    @Inject
    ProductRepository productRepository;

    @Inject
    Tracer tracer;

    @Inject
    @Channel("available-quantity-events")
    Emitter<AvailableQuantityEvent> availableQuantityEventEmitter;

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    @ActivateRequestContext
    @Incoming("check-quantity")
    public void consume(CheckQuantityEvent event) {
        // Logic to check available quantity for the product
        Long productId = event.getProductId();
        int requestedQuantity = event.getRequestedQuantity();

        Product product = productRepository.findById(productId);
        int availableQuantity = product.getQuantityInStock();

        AvailableQuantityEvent availableQuantityEvent = AvailableQuantityEvent.builder()
                .productId(productId)
                .inStock(product.getQuantityInStock())
                .isAvailable(availableQuantity >= requestedQuantity)
                .build();
        availableQuantityEventEmitter.send(availableQuantityEvent);
    }
    @Transactional
    public void saveProduct(Product product) {
        Span span = tracer.spanBuilder("saveProduct-service").startSpan();
        try {
            productRepository.persist(product);
            logger.info("Product with name: {} and id: {} has been successfully persisted in the database", product.getName(), product.getProductId());
        } catch (Exception e) {
            logger.error("Failed to persist product: {}", e.getMessage(), e);
            span.recordException(e);
            throw e;
        } finally {
            span.end();
        }
    }
    @Transactional
    public void saveProductList(List<Product> productList) {
        Span span = tracer.spanBuilder("saveProductList-service").startSpan();
        try {
            productList.forEach(productRepository::persist);
        } catch (Exception e) {
            span.recordException(e);
            throw e;
        } finally {
            span.end();
        }
    }
    @ActivateRequestContext
    public List<Product> getAllProducts() {
        Span span = tracer.spanBuilder("getAllProducts-service").startSpan();
        try {
            return productRepository.listAll();
        } catch (Exception e) {
            span.recordException(e);
            throw e;
        } finally {
            span.end();
        }
    }
    @ActivateRequestContext
    public long countProducts() {
        Span span = tracer.spanBuilder("countProducts-service").startSpan();
        try {
            return productRepository.countProducts();
        } catch (Exception e) {
            span.recordException(e);
            throw e;
        } finally {
            span.end();
        }
    }
    @ActivateRequestContext
    public Product findProductById(Long id) {
        Span span = tracer.spanBuilder("findProductById-service").startSpan();
        try {
            return productRepository.findById(id);
        } catch (Exception e) {
            span.recordException(e);
            throw e;
        } finally {
            span.end();
        }
    }
    @Transactional
    public boolean deleteProduct(Long id) {
        Span span = tracer.spanBuilder("deleteProduct-service").startSpan();
        try {
            Product product = productRepository.findById(id);
            if (product != null) {
                productRepository.delete(product);
                return true;
            }
            return false;
        } catch (Exception e) {
            span.recordException(e);
            throw e;
        } finally {
            span.end();
        }
    }
    @ActivateRequestContext
    public List<Product> searchProducts(String keyword) {
        Span span = tracer.spanBuilder("searchProducts-service").startSpan();
        try {
            return productRepository.searchByNameOrDescription(keyword);
        } catch (Exception e) {
            span.recordException(e);
            throw e;
        } finally {
            span.end();
        }
    }

}
