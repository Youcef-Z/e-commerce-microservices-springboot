package com.youcef.ecommerce.order;

public class OrderMapper {
    public Order toOrder(OrderRequest orderRequest) {
        return Order.builder()
                .id(orderRequest.id())
                .reference(orderRequest.reference())
                .totalAmount(orderRequest.amount())
                .paymentMethod(orderRequest.paymentMethod())
                .customerId(orderRequest.customerId())
                .build();
    }
}
