package com.marketflow.exception;

import java.util.UUID;

public class ProductNotFoundException extends RuntimeException{
    public ProductNotFoundException(UUID productId) {
        super(String.format("Product not found with id: %s", productId));
    }
}
