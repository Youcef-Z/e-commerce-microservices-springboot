package com.youcef.ecommerce.order;

import com.youcef.ecommerce.customer.CustomerClient;
import com.youcef.ecommerce.customer.CustomerResponse;
import com.youcef.ecommerce.exceptions.BusinessException;
import com.youcef.ecommerce.orderLine.OrderLineRequest;
import com.youcef.ecommerce.orderLine.OrderLineService;
import com.youcef.ecommerce.product.ProductClient;
import com.youcef.ecommerce.product.PurchaseRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final CustomerClient customerClient;
    private final ProductClient productClient;
    private final OrderLineService orderLineService;

    public Integer createOrder(OrderRequest orderRequest) {
        // Check the customer -> OpenFeign
        CustomerResponse customer = this.customerClient.findCustomerById(orderRequest.customerId())
                .orElseThrow(() -> new BusinessException("Cannot find customer with id " + orderRequest.customerId()));

        // Purchase the products -> product microservice (Instead of OpenFeign, we'll use RestTemplate)
        this.productClient.purchaseProducts(orderRequest.products());

        // Persist the order
        Order order = orderRepository.save(orderMapper.toOrder(orderRequest));

        // Persist the order lines
        for (PurchaseRequest purchaseRequest : orderRequest.products()) {
            orderLineService.saveOrderLine(
                    new OrderLineRequest(
                            null,
                            order.getId(),
                            purchaseRequest.productId(),
                            purchaseRequest.quantity()
                    )
            );
        }

        // TODO Start the payment process

        // TODO Send the order confirmation -> notification microservice (kafka)
    }
}
