package com.marketflow.dto.stock;


import com.marketflow.domain.entity.StockMovement;
import com.marketflow.domain.enums.StockMovementType;

import java.time.Instant;
import java.util.UUID;

public record StockMovementResponse(
        UUID id,
        StockMovementType type,
        int quantity,
        String reason,
        UUID orderId,
        Instant createdAt
) {
    public StockMovementResponse(StockMovement stockMovement) {
        this(
                stockMovement.id,
                stockMovement.type,
                stockMovement.quantity,
                stockMovement.reason,
                stockMovement.orderId,
                stockMovement.createdAt
        );
    }
}