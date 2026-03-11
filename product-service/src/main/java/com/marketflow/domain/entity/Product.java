package com.marketflow.domain.entity;

import com.marketflow.domain.enums.ProductCategory;
import com.marketflow.domain.enums.ProductStatus;
import com.marketflow.dto.product.CreateProductRequest;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "products")
public class Product extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    public UUID id;

    @Column(name = "name", nullable = false)
    public String name;

    @Column(name = "description", columnDefinition = "TEXT")
    public String description;

    @Column(name = "price", nullable = false, precision = 19, scale = 2)
    public BigDecimal price;

    @Column(name = "sku", nullable = false, unique = true)
    public String sku;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    public ProductCategory category;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    public ProductStatus status = ProductStatus.ACTIVE;

    @Column(name = "seller_id", nullable = false)
    public UUID sellerId;

    @Column(name = "created_at", updatable = false, nullable = false)
    public Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    public Instant updatedAt;

    public static Product of(CreateProductRequest request, UUID sellerId) {
        Product product = new Product();
        product.name = request.name();
        product.description = request.description();
        product.price = request.price();
        product.sku = request.sku();
        product.category = request.category();
        product.sellerId = sellerId;
        return product;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now();
    }
}