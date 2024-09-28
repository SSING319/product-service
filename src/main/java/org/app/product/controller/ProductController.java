package org.app.product.controller;

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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Path("/products")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProductController {

    @Inject
    private final ProductService productService;


    //POST
    @POST
    @Transactional
    @Path("/add")
    public Response createProduct(ProductCreationRequest request) throws IOException {
        Product product = Util.productBuilder(request);
        productService.saveProduct(product);
        return Response.status(Response.Status.CREATED).entity(Util.productResponseBuilder(product)).build();
    }

    @POST
    @Path("/addMultiple")
    public Response addMultipleProducts(List<ProductCreationRequest> request) throws IOException {
        List<Product> productList = new ArrayList<>();
        for (ProductCreationRequest productCreationRequest : request) {
            productList.add(Util.productBuilder(productCreationRequest));
        }
        productService.saveProductList(productList);
        return Response.status(Response.Status.CREATED).entity("Products added successfully.").build();
    }

    //GET
    @GET
    public Response getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return Response.ok(Util.productResponseListBuilder(products)).build();
    }

    @GET
    @Path("/search")
    public Response searchProducts(@QueryParam("keyword") String keyword) {
        List<Product> products = productService.searchProducts(keyword);
        return Response.ok(Util.productResponseListBuilder(products)).build();
    }

    @GET
    @Path("/count")
    public long countProducts() {
        return productService.countProducts();
    }

    @GET
    @Path("/{id}")
    public Response getProductById(@PathParam("id") Long id) {
        Product product = productService.findProductById(id);
        if (product == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(Util.productResponseBuilder(product)).build();
    }

    //DELETE
    @DELETE
    @Path("/{id}")
    public Response deleteProductById(@PathParam("id") Long id) {
        boolean deleted = productService.deleteProduct(id);
        if (deleted) {
            return Response.noContent().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }


}
