package com.youcef.ecommerce.payment;

import com.youcef.ecommerce.customer.CustomerResponse;
import com.youcef.ecommerce.order.PaymentMethod;

import java.math.BigDecimal;

public record PaymentRequest(

        BigDecimal amount,

        PaymentMethod paymentMethod,

        Integer orderId,

        String orderReference,

        CustomerResponse customer
) {
}
