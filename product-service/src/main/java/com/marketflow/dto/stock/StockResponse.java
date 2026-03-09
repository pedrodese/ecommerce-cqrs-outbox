package com.marketflow.dto.stock;

import com.marketflow.domain.entity.Stock;

import java.time.Instant;
import java.util.UUID;

public record StockResponse(
        UUID id,
        int quantityAvailable,
        int quantityReserved,
        int quantityTotal,
        Instant updatedAt
) {
    public StockResponse(Stock stock) {
        this(
                stock.id,
                stock.quantityAvailable,
                stock.quantityReserved,
                stock.quantityTotal(),
                stock.updatedAt
        );
    }
}