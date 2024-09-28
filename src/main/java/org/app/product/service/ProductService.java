package org.app.product.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.app.product.entity.Product;
import org.app.product.repository.ProductRepository;

import java.util.List;

@ApplicationScoped
@AllArgsConstructor
public class ProductService {

    @Inject
    private final ProductRepository productRepository;

    @Transactional
    public void saveProduct(Product product) {
        productRepository.persist(product);
    }

    @Transactional
    public void saveProductList(List<Product> productList) {
        productList.forEach(productRepository::persist);
    }

    public List<Product> getAllProducts() {
        return productRepository.listAll();
    }

    public long countProducts() {
        return productRepository.countProducts();
    }

    public Product findProductById(Long id) {
        return productRepository.findById(id);
    }

    @Transactional
    public boolean deleteProduct(Long id) {
        Product product = productRepository.findById(id);
        if (product != null) {
            productRepository.delete(product);
            return true;
        }
        return false;
    }

    public List<Product> searchProducts(String keyword) {
        return productRepository.searchByNameOrDescription(keyword);
    }
}
