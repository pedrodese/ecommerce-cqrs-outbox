package com.marketflow.service;


import com.marketflow.domain.entity.Product;
import com.marketflow.domain.enums.ProductCategory;
import com.marketflow.domain.enums.ProductStatus;
import com.marketflow.dto.product.CreateProductRequest;
import com.marketflow.dto.product.PageResponse;
import com.marketflow.dto.product.ProductResponse;
import com.marketflow.dto.product.UpdateProductRequest;
import com.marketflow.dto.stock.StockResponse;
import com.marketflow.exception.ProductNotFoundException;
import com.marketflow.exception.SkuAlreadyExistsException;
import com.marketflow.repository.ProductRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class ProductService {

    private final ProductRepository productRepository;
    private final StockService stockService;
    private final JsonWebToken jwt;

    public ProductService(ProductRepository productRepository, StockService stockService, JsonWebToken jwt) {
        this.productRepository = productRepository;
        this.stockService = stockService;
        this.jwt = jwt;
    }

    @Transactional
    public ProductResponse create(CreateProductRequest request) {
        if (productRepository.existsBySku(request.sku())) {
            throw new SkuAlreadyExistsException(request.sku());
        }

        Product product = Product.of(request, extractSellerIdFromToken());
        productRepository.persist(product);

        StockResponse stock = stockService.create(product, request.initialStock());

        return new ProductResponse(product, stock);
    }

    public ProductResponse findById(UUID id) {
        Product product = findByIdOrThrow(id);
        StockResponse stock = stockService.findByProductId(id);
        return new ProductResponse(product, stock);
    }

    public PageResponse<ProductResponse> findAll(ProductCategory category, ProductStatus status, int page, int pageSize) {
        List<ProductResponse> content = productRepository.findByFilters(category, status, page, pageSize)
                .stream()
                .map(product -> new ProductResponse(product, stockService.findByProductId(product.id)))
                .toList();

        long total = productRepository.countByFilters(category, status);
        return PageResponse.of(content, page, pageSize, total);
    }

    public PageResponse<ProductResponse> findBySeller(int page, int pageSize) {
        List<ProductResponse> content = productRepository.findBySellerId(extractSellerIdFromToken(), page, pageSize)
                .stream()
                .map(product -> new ProductResponse(product, stockService.findByProductId(product.id)))
                .toList();

        long total = productRepository.count("sellerId", extractSellerIdFromToken());
        return PageResponse.of(content, page, pageSize, total);
    }

    @Transactional
    public ProductResponse update(UUID id, UpdateProductRequest request) {
        Product product = findByIdOrThrow(id);

        product.name = request.name();
        product.description = request.description();
        product.price = request.price();

        StockResponse stock = stockService.findByProductId(id);
        return new ProductResponse(product, stock);
    }

    @Transactional
    public void deactivate(UUID id) {
        Product product = findByIdOrThrow(id);
        product.status = ProductStatus.INACTIVE;
    }

    private Product findByIdOrThrow(UUID id) {
        return productRepository.findByIdOptional(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    private UUID extractSellerIdFromToken() {
        return UUID.fromString(jwt.getSubject());
    }
}