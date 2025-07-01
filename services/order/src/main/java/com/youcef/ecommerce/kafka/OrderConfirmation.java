package com.youcef.ecommerce.kafka;

import com.youcef.ecommerce.customer.CustomerResponse;
import com.youcef.ecommerce.order.PaymentMethod;
import com.youcef.ecommerce.product.PurchaseResponse;

import java.math.BigDecimal;
import java.util.List;

public record OrderConfirmation(
        String orderReference,
        BigDecimal totalAmount,
        PaymentMethod paymentMethod,
        CustomerResponse customer,
        List<PurchaseResponse> products
) {
}
