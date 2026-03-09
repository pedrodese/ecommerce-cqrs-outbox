package com.marketflow.dto.product;


import com.marketflow.domain.entity.Product;
import com.marketflow.domain.enums.ProductCategory;
import com.marketflow.domain.enums.ProductStatus;
import com.marketflow.dto.stock.StockResponse;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ProductResponse(
        UUID id,
        String name,
        String description,
        BigDecimal price,
        String sku,
        ProductCategory category,
        ProductStatus status,
        UUID sellerId,
        StockResponse stock,
        Instant createdAt,
        Instant updatedAt
) {
    public ProductResponse(Product product, StockResponse stock) {
        this(
                product.id,
                product.name,
                product.description,
                product.price,
                product.sku,
                product.category,
                product.status,
                product.sellerId,
                stock,
                product.createdAt,
                product.updatedAt
        );
    }
}