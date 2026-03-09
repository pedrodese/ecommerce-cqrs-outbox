package com.marketflow.exception;

public class SkuAlreadyExistsException extends RuntimeException {
    public SkuAlreadyExistsException(String sku) {
        super(String.format("SKU already in use: %s", sku));
    }
}
