package com.esprow.challenge.stockmarketsimulator.engine;

import com.esprow.challenge.stockmarketsimulator.domain.LimitOrder;
import com.esprow.challenge.stockmarketsimulator.domain.Stock;
import com.esprow.challenge.stockmarketsimulator.persistence.StockRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
public class OrderBooks {
    private final StockRepository stockRepository;
    private Map<String, OrderBook> orderBooks;

    public OrderBooks(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    @PostConstruct
    public void postConstruct() {
        orderBooks = stockRepository.findAll().stream()
                .collect(Collectors.toMap(Stock::getSymbol, stock -> new OrderBook()));
    }

    public void addOrder(LimitOrder order) {
        synchronized (orderBooks) {
            OrderBook orderBook = orderBooks.computeIfAbsent(order.getSymbol(), symbol -> {
                Stock stock = new Stock();
                stock.setSymbol(symbol);
                stock = stockRepository.save(stock);
                log.info("New stock symbol was added - {}", symbol);
                return new OrderBook();
            });
            orderBook.addOrder(order);
        }
    }

    public void deleteOrder(LimitOrder order) {
        synchronized (orderBooks) {
            Optional.ofNullable(orderBooks.get(order.getSymbol()))
                    .ifPresentOrElse(orderBook -> {
                        orderBook.deleteOrder(order);
                    }, () -> {
                        log.warn("Not found order book by symbol=[{}]", order.getSymbol());
                    });
        }
    }

    public Map<String, Pair<Set<LimitOrder>, Set<LimitOrder>>> fetchOrders() {
        HashMap<String, Pair<Set<LimitOrder>, Set<LimitOrder>>> result = new HashMap<>();
        synchronized (orderBooks) {
            orderBooks.forEach((symbol, orderBook) -> {
                Set<LimitOrder> buyOrders = orderBook.fetchBuyOrders();
                Set<LimitOrder> sellOrders = orderBook.fetchSellOrders();
                result.put(symbol, Pair.of(buyOrders, sellOrders));
            });
        }
        return result;
    }
}
