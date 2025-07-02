package com.youcef.ecommerce.order;

import com.youcef.ecommerce.customer.CustomerClient;
import com.youcef.ecommerce.customer.CustomerResponse;
import com.youcef.ecommerce.exceptions.BusinessException;
import com.youcef.ecommerce.kafka.OrderConfirmation;
import com.youcef.ecommerce.kafka.OrderProducer;
import com.youcef.ecommerce.orderLine.OrderLineRequest;
import com.youcef.ecommerce.orderLine.OrderLineService;
import com.youcef.ecommerce.payment.PaymentClient;
import com.youcef.ecommerce.payment.PaymentRequest;
import com.youcef.ecommerce.product.ProductClient;
import com.youcef.ecommerce.product.PurchaseRequest;
import com.youcef.ecommerce.product.PurchaseResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final CustomerClient customerClient;
    private final ProductClient productClient;
    private final OrderLineService orderLineService;
    private final OrderProducer orderProducer;
    private final PaymentClient paymentClient;

    public Integer createOrder(OrderRequest orderRequest) {
        // Check the customer -> OpenFeign
        CustomerResponse customer = customerClient.findCustomerById(orderRequest.customerId())
                .orElseThrow(() -> new BusinessException("Cannot find customer with id " + orderRequest.customerId()));

        // Purchase the products -> product microservice (Instead of OpenFeign, we'll use RestTemplate)
        List<PurchaseResponse> purchasedProducts = productClient.purchaseProducts(orderRequest.products());

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

        // Start the payment process
        paymentClient.requestOrderPayment(
                new PaymentRequest(
                        orderRequest.amount(),
                        orderRequest.paymentMethod(),
                        order.getId(),
                        orderRequest.reference(),
                        customer
                )
        );

        // Send the order confirmation -> notification microservice (kafka)
        orderProducer.sendOrderConfirmation(
                new OrderConfirmation(
                        orderRequest.reference(),
                        orderRequest.amount(),
                        orderRequest.paymentMethod(),
                        customer,
                        purchasedProducts
                )
        );

        return order.getId();
    }

    public List<OrderResponse> findAll() {
        return orderRepository.findAll().stream().map(orderMapper::toOrderResponse).toList();
    }

    public OrderResponse getOrderById(Integer orderId) {
        return orderRepository.findById(orderId).map(orderMapper::toOrderResponse).orElseThrow(
                () -> new EntityNotFoundException(String.format("Order with id %d not found", orderId))
        );
    }
}
