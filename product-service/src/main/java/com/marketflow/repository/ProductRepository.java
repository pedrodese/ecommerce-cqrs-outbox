package com.marketflow.repository;

import com.marketflow.domain.entity.Product;
import com.marketflow.domain.enums.ProductCategory;
import com.marketflow.domain.enums.ProductStatus;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class ProductRepository implements PanacheRepositoryBase<Product, UUID> {

    public Optional<Product> findBySku(String sku) {
        return find("sku", sku).firstResultOptional();
    }

    public boolean existsBySku(String sku) {
        return count("sku", sku) > 0;
    }

    public List<Product> findByFilters(ProductCategory category, ProductStatus status, int page, int pageSize) {
        var filters = buildFilters(category, status);
        return find(filters.query(), Sort.by("createdAt").descending(), filters.params())
                .page(Page.of(page, pageSize))
                .list();
    }

    public long countByFilters(ProductCategory category, ProductStatus status) {
        var filters = buildFilters(category, status);
        return count(filters.query(), filters.params());
    }

    public List<Product> findBySellerId(UUID sellerId, int page, int pageSize) {
        return find("sellerId", Sort.by("createdAt").descending(), sellerId)
                .page(Page.of(page, pageSize))
                .list();
    }

    private Filters buildFilters(ProductCategory category, ProductStatus status) {
        var query = new StringBuilder("1=1");
        var params = new HashMap<String, Object>();

        if (category != null) {
            query.append(" AND category = :category");
            params.put("category", category);
        }

        if (status != null) {
            query.append(" AND status = :status");
            params.put("status", status);
        }

        return new Filters(query.toString(), params);
    }

    private record Filters(String query, Map<String, Object> params) {}
}