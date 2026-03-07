package com.marketflow.domain.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "stocks")
public class Stock extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    public UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, unique = true)
    public Product product;

    @Column(name = "quantity_available", nullable = false)
    public int quantityAvailable = 0;

    @Column(name = "quantity_reserved", nullable = false)
    public int quantityReserved = 0;

    @Column(name = "updated_at", nullable = false)
    public Instant updatedAt;

    @PrePersist
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now();
    }

    public int quantityTotal() {
        return quantityAvailable + quantityReserved;
    }

    public boolean hasAvailable(int quantity) {
        return quantityAvailable >= quantity;
    }

    public static Stock of(Product product, int initialQuantity) {
        Stock stock = new Stock();
        stock.product = product;
        stock.quantityAvailable = initialQuantity;
        return stock;
    }
}