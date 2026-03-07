package com.marketflow.domain.entity;

import com.marketflow.domain.enums.StockMovementType;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "stock_movements")
public class StockMovement extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    public UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id", nullable = false)
    public Stock stock;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    public StockMovementType type;

    @Column(name = "quantity", nullable = false)
    public int quantity;

    @Column(name = "reason")
    public String reason;

    @Column(name = "order_id")
    public UUID orderId;

    @Column(name = "created_at", updatable = false, nullable = false)
    public Instant createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = Instant.now();
    }

    public static StockMovement of(Stock stock, StockMovementType type, int quantity, String reason, UUID orderId) {
        StockMovement movement = new StockMovement();
        movement.stock = stock;
        movement.type = type;
        movement.quantity = quantity;
        movement.reason = reason;
        movement.orderId = orderId;
        return movement;
    }
}