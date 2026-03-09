package com.marketflow.dto.product;

import com.marketflow.domain.enums.ProductCategory;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CreateProductRequest(

        @NotBlank(message = "Name is required")
        String name,

        String description,

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.01", message = "Price must be greater than zero")
        @Digits(integer = 17, fraction = 2, message = "Invalid price format")
        BigDecimal price,

        @NotBlank(message = "SKU is required")
        String sku,

        @NotNull(message = "Category is required")
        ProductCategory category,

        @Min(value = 0, message = "Initial stock cannot be negative")
        int initialStock
) {}