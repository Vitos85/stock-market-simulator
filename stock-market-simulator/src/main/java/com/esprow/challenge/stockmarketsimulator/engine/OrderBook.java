package com.esprow.challenge.stockmarketsimulator.engine;

import com.esprow.challenge.stockmarketsimulator.domain.LimitOrder;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

@Slf4j
public class OrderBook {

    private TreeSet<LimitOrder> buyOrders = new TreeSet<>((order1, order2) -> {
        if (order2 == null) {
            return 0;
        } else {
            return Optional.of(Integer.compare(order2.getPrice(), order1.getPrice()))
                    .filter(result -> result != 0)
                    .orElse(order1.getCreated().compareTo(order2.getCreated()));
        }
    });
    private TreeSet<LimitOrder> sellOrders = new TreeSet<>((order1, order2) -> {
        return Optional.of(Integer.compare(order1.getPrice(), order2.getPrice()))
                .filter(result -> result != 0)
                .orElse(order2 == null ? 0 : order1.getCreated().compareTo(order2.getCreated()));
    });

    public void addOrder(LimitOrder order) {
        switch (order.getOrderDir()) {
            case BUY:
                synchronized (buyOrders) {
                    buyOrders.add(order);
                    log.info("Added BUY order in order book - {}", order);
                }
                break;
            case SELL:
                synchronized (sellOrders) {
                    sellOrders.add(order);
                    log.info("Added SELL order in order book - {}", order);
                }
                break;
            default:
                log.warn("Try to add order with incorrect dir - {}", order);
                throw new IllegalArgumentException("Try to add order with incorrect dir");
        }
    }

    public void deleteOrder(LimitOrder order) {
        switch (order.getOrderDir()) {
            case BUY:
                synchronized (buyOrders) {
                    buyOrders.remove(order);
                    log.info("Deleted BUY order from order book - {}", order);
                }
                break;
            case SELL:
                synchronized (sellOrders) {
                    sellOrders.remove(order);
                    log.info("Deleted SELL order from order book - {}", order);
                }
                break;
            default:
                log.warn("Try to cancel order with incorrect dir - {}", order);
                throw new IllegalArgumentException("Try to add order with incorrect dir");
        }
    }

    public Set<LimitOrder> fetchBuyOrders() {
        TreeSet<LimitOrder> result;
        synchronized (buyOrders) {
            result = new TreeSet<>(buyOrders);
        }
        return result;
    }

    public Set<LimitOrder> fetchSellOrders() {
        TreeSet<LimitOrder> result;
        synchronized (sellOrders) {
            result = new TreeSet<>(sellOrders);
        }
        return result;
    }
}
