package org.app.product.controller;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.AllArgsConstructor;
import org.app.product.entity.Product;
import org.app.product.request.ProductCreationRequest;
import org.app.product.service.ProductService;
import org.app.product.util.Util;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Path("/products")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Product API", description = "Product management API")
public class ProductController {

    @Inject
    ProductService productService;

    @Inject
    Tracer tracer;

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    //POST
    @POST
    @Path("/add")
    @Operation(summary = "Creates a Product in the Database")
    public Response createProduct(ProductCreationRequest request) throws IOException {
        Span span = tracer.spanBuilder("createProduct-controller").startSpan();
        try {
            Product product = Util.productBuilder(request);
            logger.info("Product with name: {}  will now be persisted in the database", product.getName());
            productService.saveProduct(product);
            return Response.status(Response.Status.CREATED).entity(Util.productResponseBuilder(product)).build();
        } catch (Exception e) {
            logger.error("Failed to create product: {}", e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to create product").build();
        } finally {
            span.end();
        }
    }

    @POST
    @Path("/addMultiple")
    @Operation(summary = "Creates Multiple Products in the Database at one time")
    public Response addMultipleProducts(List<ProductCreationRequest> request) {
        Span span = tracer.spanBuilder("addMultipleProducts-controller").startSpan();
        try {
            logger.info("Adding multiple products");
            List<Product> productList = new ArrayList<>();
            for (ProductCreationRequest productCreationRequest : request) {
                productList.add(Util.productBuilder(productCreationRequest));
            }
            productService.saveProductList(productList);
            logger.info("Successfully added {} products", productList.size());
            return Response.status(Response.Status.CREATED).entity("Products added successfully.").build();
        } catch (Exception e) {
            logger.error("Failed to add multiple products: {}", e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to add products").build();
        } finally {
            span.end();
        }
    }

    // GET all products
    @GET
    @Operation(summary = "Gets all of the Products from the database")
    public Response getAllProducts() {
        Span span = tracer.spanBuilder("getAllProducts-controller").startSpan();
        try {
            logger.info("Fetching all products");
            List<Product> products = productService.getAllProducts();
            logger.info("Retrieved {} products", products.size());
            return Response.ok(Util.productResponseListBuilder(products)).build();
        } catch (Exception e) {
            logger.error("Failed to fetch all products: {}", e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to fetch products").build();
        } finally {
            span.end();
        }
    }

    // Search products
    @GET
    @Path("/search")
    @Operation(summary = "Searches the Database for the product based on keyword matching with name or description")
    public Response searchProducts(@QueryParam("keyword") String keyword) {
        Span span = tracer.spanBuilder("searchProducts-controller").startSpan();
        try {
            logger.info("Searching products with keyword: {}", keyword);
            List<Product> products = productService.searchProducts(keyword);
            logger.info("Found {} products for keyword: {}", products.size(), keyword);
            return Response.ok(Util.productResponseListBuilder(products)).build();
        } catch (Exception e) {
            logger.error("Failed to search products with keyword {}: {}", keyword, e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to search products").build();
        } finally {
            span.end();
        }
    }

    // Count products
    @GET
    @Path("/count")
    @Operation(summary = "Gets all of the Products count from the database")
    public Response countProducts() {
        Span span = tracer.spanBuilder("countProducts-controller").startSpan();
        try {
            logger.info("Counting all products");
            long count = productService.countProducts();
            logger.info("Total products count: {}", count);
            return Response.ok(count).build();
        } catch (Exception e) {
            logger.error("Failed to count products: {}", e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to count products").build();
        } finally {
            span.end();
        }
    }

    // Get product by ID
    @GET
    @Path("/{id}")
    @Operation(summary = "Get a product by ID")
    public Response getProductById(@PathParam("id") Long id) {
        Span span = tracer.spanBuilder("getProductById-controller").startSpan();
        try {
            logger.info("Fetching product with ID: {}", id);
            Product product = productService.findProductById(id);
            if (product == null) {
                logger.warn("Product with ID {} not found", id);
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            logger.info("Found product with ID: {}", id);
            return Response.ok(Util.productResponseBuilder(product)).build();
        } catch (Exception e) {
            logger.error("Failed to fetch product by ID {}: {}", id, e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to fetch product").build();
        } finally {
            span.end();
        }
    }

    // DELETE product by ID
    @DELETE
    @Path("/{id}")
    @Operation(summary = "Deletes a product by ID")
    public Response deleteProductById(@PathParam("id") Long id) {
        Span span = tracer.spanBuilder("deleteProductById-controller").startSpan();
        try {
            logger.info("Deleting product with ID: {}", id);
            boolean deleted = productService.deleteProduct(id);
            if (deleted) {
                logger.info("Product with ID {} successfully deleted", id);
                return Response.noContent().build();
            } else {
                logger.warn("Product with ID {} not found for deletion", id);
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        } catch (Exception e) {
            logger.error("Failed to delete product by ID {}: {}", id, e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to delete product").build();
        } finally {
            span.end();
        }
    }
}
