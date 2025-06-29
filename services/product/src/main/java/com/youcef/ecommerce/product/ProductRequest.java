package com.youcef.ecommerce.product;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record ProductRequest(

        Integer id,

        @NotNull(message = "Product name is required")
        String name,

        @NotNull(message = "Product description is required")
        String description,

        @PositiveOrZero(message = "Product available quantity must be greater than or equal to zero")
        double availableQuantity,

        @Positive(message = "Product Price must be greater than zero")
        BigDecimal price,

        @NotNull(message = "Product category ID is required")
        Integer categoryId
) {
}
