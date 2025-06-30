package com.youcef.ecommerce.product;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PurchaseRequest(

        @NotNull(message = "Product id is required")
        Integer productId,

        @Positive(message = "Product quantity must be positive")
        double quantity
) {
}
