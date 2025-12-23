package com.example.processingservice.mapper;

import com.example.commonlib.events.FlatOrderProduct;
import com.example.commonlib.events.OrderPlacedEvent;
import com.example.commonlib.models.Product;
import org.springframework.stereotype.Component;

@Component
public class FlatOrderProductMapper {

    public FlatOrderProduct map(OrderPlacedEvent order, Product product) {
        return new FlatOrderProduct(
                order.getOrderId(),
                order.getCustomerId(),
                order.getTotalAmount(),
                order.getCreatedAt().toEpochMilli(),
                product.getProductId(),
                product.getQuantity(),
                product.getPrice(),
                buildOrderProductId(order.getOrderId(), product.getProductId())
        );
    }

    private String buildOrderProductId(String orderId, String productId) {
        return orderId + "-" + productId;
    }
}
