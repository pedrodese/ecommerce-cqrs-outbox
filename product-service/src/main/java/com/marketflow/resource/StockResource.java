package com.marketflow.resource;


import com.marketflow.dto.product.PageResponse;
import com.marketflow.dto.stock.AdjustStockRequest;
import com.marketflow.dto.stock.StockMovementResponse;
import com.marketflow.dto.stock.StockResponse;
import com.marketflow.service.StockService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import java.util.UUID;

@Path("/products/{productId}/stock")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class StockResource {

    private final StockService stockService;

    public StockResource(StockService stockService) {
        this.stockService = stockService;
    }

    @GET
    @RolesAllowed({"BUYER", "SELLER", "ADMIN"})
    public StockResponse findByProductId(@PathParam("productId") UUID productId) {
        return stockService.findByProductId(productId);
    }

    @POST
    @Path("/replenish")
    @RolesAllowed({"SELLER", "ADMIN"})
    public StockResponse replenish(
            @PathParam("productId") UUID productId,
            @Valid AdjustStockRequest request) {
        return stockService.replenish(productId, request);
    }

    @POST
    @Path("/reserve")
    @RolesAllowed({"ADMIN"})
    public StockResponse reserve(
            @PathParam("productId") UUID productId,
            @QueryParam("orderId") UUID orderId,
            @Valid AdjustStockRequest request) {
        return stockService.reserve(productId, request, orderId);
    }

    @POST
    @Path("/release")
    @RolesAllowed({"ADMIN"})
    public StockResponse release(
            @PathParam("productId") UUID productId,
            @QueryParam("orderId") UUID orderId,
            @Valid AdjustStockRequest request) {
        return stockService.release(productId, request, orderId);
    }

    @POST
    @Path("/confirm-sale")
    @RolesAllowed({"ADMIN"})
    public StockResponse confirmSale(
            @PathParam("productId") UUID productId,
            @QueryParam("orderId") UUID orderId,
            @Valid AdjustStockRequest request) {
        return stockService.confirmSale(productId, request, orderId);
    }

    @GET
    @Path("/movements")
    @RolesAllowed({"SELLER", "ADMIN"})
    public PageResponse<StockMovementResponse> findMovements(
            @PathParam("productId") UUID productId,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("pageSize") @DefaultValue("20") int pageSize) {
        return stockService.findMovements(productId, page, pageSize);
    }
}