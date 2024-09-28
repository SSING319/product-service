package org.app.product.util;

import lombok.experimental.UtilityClass;
import org.app.product.entity.Product;
import org.app.product.request.ProductCreationRequest;
import org.app.product.request.ProductResponse;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@UtilityClass
public class Util {
    public byte[] convertImageToByteArray(String imagePath) throws IOException {
        // Assuming the image is stored in the resources directory
        File imageFile = new File("src/main/resources/" + imagePath); // Adjust the path as necessary
        return Files.readAllBytes(imageFile.toPath());
    }

    public Product productBuilder(ProductCreationRequest request) throws IOException {
        return Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .sku(request.getSku())
                .quantityInStock(request.getQuantityInStock())
                .image(Util.convertImageToByteArray(request.getImage()))
                .build();
    }

    public ProductResponse productResponseBuilder(Product product) {
        return ProductResponse.builder()
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .sku(product.getSku())
                .quantityInStock(product.getQuantityInStock())
                .build();
    }

    public List<ProductResponse> productResponseListBuilder(List<Product> productList) {
        return productList.stream().map(Util::productResponseBuilder).toList();
    }
}
