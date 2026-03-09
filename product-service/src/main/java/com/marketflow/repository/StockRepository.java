package com.marketflow.repository;

import com.marketflow.domain.entity.Stock;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class StockRepository implements PanacheRepositoryBase<Stock, UUID> {

    public Optional<Stock> findByProductId(UUID productId) {
        return find("product.id", productId).firstResultOptional();
    }
}