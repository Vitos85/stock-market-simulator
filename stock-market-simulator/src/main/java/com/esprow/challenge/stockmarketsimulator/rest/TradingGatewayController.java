package com.esprow.challenge.stockmarketsimulator.rest;

import com.esprow.challenge.stockmarketsimulator.domain.LimitOrder;
import com.esprow.challenge.stockmarketsimulator.domain.OrderStatus;
import com.esprow.challenge.stockmarketsimulator.engine.OrderBooks;
import com.esprow.challenge.stockmarketsimulator.persistence.OrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/orders")
public class TradingGatewayController {
    private final OrderBooks orderBooks;
    private final OrderRepository orderRepository;

    @PostMapping("/add")
    public OrderDTO addOrder(@Valid @RequestBody LimitOrderRequest orderRequest) {
        LimitOrder order = new LimitOrder();
        order.setOrderDir(orderRequest.getDir());
        order.setPrice(orderRequest.getPrice());
        order.setQuantity(orderRequest.getQuantity());
        order.setRest(orderRequest.getQuantity());
        order.setStatus(OrderStatus.OPEN);
        order.setSymbol(orderRequest.getSymbol());
        order.setUserId(orderRequest.getUserID());
        order = orderRepository.save(order);

        orderBooks.addOrder(order);

        return toDto(order);
    }

    @PostMapping("/{orderID}/cancel")
    public OrderDTO cancelOrder(@PathVariable("orderID") UUID orderID) {
        LimitOrder order = orderRepository.findById(orderID)
                .orElseThrow(() -> new RuntimeException("Not found order by ID=" + orderID));
        orderBooks.deleteOrder(order);
        order.setStatus(OrderStatus.CANCELED);
        orderRepository.save(order);
        return toDto(order);
    }

    private OrderDTO toDto(LimitOrder order) {
        return OrderDTO.builder()
                .orderID(order.getId())
                .dir(order.getOrderDir())
                .price(order.getPrice())
                .quantity(order.getQuantity())
                .stockSymbol(order.getSymbol())
                .status(order.getStatus())
                .userID(order.getUserId())
                .build();
    }
}
