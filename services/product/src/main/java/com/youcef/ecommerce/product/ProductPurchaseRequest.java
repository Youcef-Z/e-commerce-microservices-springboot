package com.youcef.ecommerce.product;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ProductPurchaseRequest(

        @NotNull(message = "Product ID is required")
        Integer productId,

        @Positive(message = "Quantity must be greater than zero")
        double quantity
) {
}
