package com.marketflow.repository;

import com.marketflow.domain.entity.StockMovement;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class StockMovementRepository implements PanacheRepositoryBase<StockMovement, UUID> {

    public List<StockMovement> findByStockId(UUID stockId, int page, int pageSize) {
        return find("stock.id", Sort.by("createdAt").descending(), stockId)
                .page(Page.of(page, pageSize))
                .list();
    }
}