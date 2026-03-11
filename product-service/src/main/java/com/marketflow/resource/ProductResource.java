package com.marketflow.resource;

import com.marketflow.domain.enums.ProductCategory;
import com.marketflow.domain.enums.ProductStatus;
import com.marketflow.dto.product.CreateProductRequest;
import com.marketflow.dto.product.PageResponse;
import com.marketflow.dto.product.ProductResponse;
import com.marketflow.dto.product.UpdateProductRequest;
import com.marketflow.service.ProductService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.UUID;

@Path("/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductResource {

    private final ProductService productService;

    public ProductResource(ProductService productService) {
        this.productService = productService;
    }

    @POST
    @RolesAllowed({"SELLER", "ADMIN"})
    public Response create(@Valid CreateProductRequest request) {
        return Response.status(Response.Status.CREATED)
                .entity(productService.create(request))
                .build();
    }

    @GET
    @RolesAllowed({"BUYER", "SELLER", "ADMIN"})
    public PageResponse<ProductResponse> findAll(
            @QueryParam("category") ProductCategory category,
            @QueryParam("status") ProductStatus status,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("pageSize") @DefaultValue("20") int pageSize) {
        return productService.findAll(category, status, page, pageSize);
    }

    @GET
    @Path("/my-products")
    @RolesAllowed({"SELLER"})
    public PageResponse<ProductResponse> findBySeller(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("pageSize") @DefaultValue("20") int pageSize) {
        return productService.findBySeller(page, pageSize);
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({"BUYER", "SELLER", "ADMIN"})
    public ProductResponse findById(@PathParam("id") UUID id) {
        return productService.findById(id);
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({"SELLER", "ADMIN"})
    public ProductResponse update(@PathParam("id") UUID id, @Valid UpdateProductRequest request) {
        return productService.update(id, request);
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({"SELLER", "ADMIN"})
    public Response deactivate(@PathParam("id") UUID id) {
        productService.deactivate(id);
        return Response.noContent().build();
    }
}