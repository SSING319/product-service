package org.app.product.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.app.product.entity.Product;

import java.util.List;

@ApplicationScoped
public class ProductRepository implements PanacheRepository<Product> {
    public List<Product> searchByNameOrDescription(String keyword) {
        return list("LOWER(name) LIKE ?1 OR LOWER(description) LIKE ?1", "%" + keyword.toLowerCase() + "%");
    }

    public long countProducts() {
        return count("SELECT COUNT(p) FROM Product p");
    }
}
