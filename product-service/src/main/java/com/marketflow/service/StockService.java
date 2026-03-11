package com.marketflow.service;

import com.marketflow.domain.entity.Product;
import com.marketflow.domain.entity.Stock;
import com.marketflow.domain.entity.StockMovement;
import com.marketflow.domain.enums.ProductStatus;
import com.marketflow.domain.enums.StockMovementType;
import com.marketflow.dto.product.PageResponse;
import com.marketflow.dto.stock.AdjustStockRequest;
import com.marketflow.dto.stock.StockMovementResponse;
import com.marketflow.dto.stock.StockResponse;
import com.marketflow.exception.InsufficientStockException;
import com.marketflow.exception.ProductNotFoundException;
import com.marketflow.repository.StockMovementRepository;
import com.marketflow.repository.StockRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class StockService {
    
    private final StockRepository stockRepository;
    private final StockMovementRepository stockMovementRepository;

    public StockService(StockRepository stockRepository, StockMovementRepository stockMovementRepository) {
        this.stockRepository = stockRepository;
        this.stockMovementRepository = stockMovementRepository;
    }

    @Transactional
    public StockResponse create(Product product, int initialQuantity) {
        Stock stock = Stock.of(product, initialQuantity);
        stockRepository.persist(stock);

        if (initialQuantity > 0) {
            stockMovementRepository.persist(
                    StockMovement.of(stock, StockMovementType.REPLENISHED, initialQuantity, "Initial stock", null)
            );
        }

        return new StockResponse(stock);
    }

    public StockResponse findByProductId(UUID productId) {
        return new StockResponse(findStockOrThrow(productId));
    }

    public PageResponse<StockMovementResponse> findMovements(UUID productId, int page, int pageSize) {
        Stock stock = findStockOrThrow(productId);

        List<StockMovementResponse> content = stockMovementRepository
                .findByStockId(stock.id, page, pageSize)
                .stream()
                .map(StockMovementResponse::new)
                .toList();

        long total = stockMovementRepository.count("stock.id", stock.id);
        return PageResponse.of(content, page, pageSize, total);
    }

    @Transactional
    public StockResponse replenish(UUID productId, AdjustStockRequest request) {
        Stock stock = findStockOrThrow(productId);

        stock.quantityAvailable += request.quantity();
        updateProductStatus(stock);

        stockMovementRepository.persist(
                StockMovement.of(stock, StockMovementType.REPLENISHED, request.quantity(), request.reason(), null)
        );

        return new StockResponse(stock);
    }

    @Transactional
    public StockResponse reserve(UUID productId, AdjustStockRequest request, UUID orderId) {
        Stock stock = findStockOrThrow(productId);

        if (!stock.hasAvailable(request.quantity())) {
            throw new InsufficientStockException(request.quantity(), stock.quantityAvailable);
        }

        stock.quantityAvailable -= request.quantity();
        stock.quantityReserved += request.quantity();
        updateProductStatus(stock);

        stockMovementRepository.persist(
                StockMovement.of(stock, StockMovementType.RESERVED, request.quantity(), request.reason(), orderId)
        );

        return new StockResponse(stock);
    }

    @Transactional
    public StockResponse release(UUID productId, AdjustStockRequest request, UUID orderId) {
        Stock stock = findStockOrThrow(productId);

        stock.quantityReserved -= request.quantity();
        stock.quantityAvailable += request.quantity();
        updateProductStatus(stock);

        stockMovementRepository.persist(
                StockMovement.of(stock, StockMovementType.RELEASED, request.quantity(), request.reason(), orderId)
        );

        return new StockResponse(stock);
    }

    @Transactional
    public StockResponse confirmSale(UUID productId, AdjustStockRequest request, UUID orderId) {
        Stock stock = findStockOrThrow(productId);

        stock.quantityReserved -= request.quantity();
        updateProductStatus(stock);

        stockMovementRepository.persist(
                StockMovement.of(stock, StockMovementType.SOLD, request.quantity(), request.reason(), orderId)
        );

        return new StockResponse(stock);
    }

    private Stock findStockOrThrow(UUID productId) {
        return stockRepository.findByProductId(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
    }

    private void updateProductStatus(Stock stock) {
        ProductStatus current = stock.product.status;
        if (stock.quantityAvailable == 0 && current == ProductStatus.ACTIVE) {
            stock.product.status = ProductStatus.OUT_OF_STOCK;
        } else if (stock.quantityAvailable > 0 && current == ProductStatus.OUT_OF_STOCK) {
            stock.product.status = ProductStatus.ACTIVE;
        }
    }
}