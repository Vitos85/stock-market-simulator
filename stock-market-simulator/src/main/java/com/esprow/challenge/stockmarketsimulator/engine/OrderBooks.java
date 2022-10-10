package com.esprow.challenge.stockmarketsimulator.engine;

import com.esprow.challenge.stockmarketsimulator.domain.LimitOrder;
import com.esprow.challenge.stockmarketsimulator.domain.OrderStatus;
import com.esprow.challenge.stockmarketsimulator.domain.Stock;
import com.esprow.challenge.stockmarketsimulator.persistence.OrderRepository;
import com.esprow.challenge.stockmarketsimulator.persistence.StockRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

@Slf4j
@Component
public class OrderBooks {
    private final StockRepository stockRepository;
    private final OrderRepository orderRepository;
    private ConcurrentMap<String, OrderBook> orderBooks; // assume that read operations more than write operations

    public OrderBooks(StockRepository stockRepository, OrderRepository orderRepository) {
        this.stockRepository = stockRepository;
        this.orderRepository = orderRepository;
    }

    @PostConstruct
    public void postConstruct() {
        orderBooks = stockRepository.findAll().stream()
                .collect(Collectors.toConcurrentMap(Stock::getSymbol, stock -> new OrderBook()));
        orderRepository.findAllOpenOrders(OrderStatus.OPEN, OrderStatus.PARTFILLED).forEach(this::addOrder);
    }

    public void addOrder(LimitOrder order) {
        OrderBook orderBook = orderBooks.computeIfAbsent(order.getSymbol(), symbol -> {
            Stock stock = new Stock();
            stock.setSymbol(symbol);
            stock = stockRepository.save(stock);
            log.info("New stock symbol was added - {}", symbol);
            return new OrderBook();
        });
        orderBook.addOrder(order);
    }

    public void deleteOrder(LimitOrder order) {
        Optional.ofNullable(orderBooks.get(order.getSymbol()))
                .ifPresentOrElse(orderBook -> {
                    orderBook.deleteOrder(order);
                }, () -> {
                    log.warn("Not found order book by symbol=[{}]", order.getSymbol());
                });
    }

    public Map<String, Pair<Set<LimitOrder>, Set<LimitOrder>>> fetchOrders() {
        HashMap<String, Pair<Set<LimitOrder>, Set<LimitOrder>>> result = new HashMap<>();
        orderBooks.forEach((symbol, orderBook) -> {
            Set<LimitOrder> buyOrders = orderBook.fetchBuyOrders();
            Set<LimitOrder> sellOrders = orderBook.fetchSellOrders();
            result.put(symbol, Pair.of(buyOrders, sellOrders));
        });
        return result;
    }
}
