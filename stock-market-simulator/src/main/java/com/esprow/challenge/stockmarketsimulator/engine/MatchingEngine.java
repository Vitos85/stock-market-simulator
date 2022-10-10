package com.esprow.challenge.stockmarketsimulator.engine;

import com.esprow.challenge.stockmarketsimulator.domain.LimitOrder;
import com.esprow.challenge.stockmarketsimulator.domain.OrderStatus;
import com.esprow.challenge.stockmarketsimulator.domain.Trade;
import com.esprow.challenge.stockmarketsimulator.persistence.OrderRepository;
import com.esprow.challenge.stockmarketsimulator.persistence.TradeLedger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MatchingEngine {
    private final TradeLedger ledger;
    private final OrderBooks orderBooks;
    private final OrderRepository orderRepository;
    private final UserNotificationService userNotificationService;
    private AtomicBoolean isRunning = new AtomicBoolean(false);

    public MatchingEngine(OrderRepository orderRepository, TradeLedger ledger, OrderBooks orderBooks,
            UserNotificationService userNotificationService) {
        this.ledger = ledger;
        this.orderBooks = orderBooks;
        this.orderRepository = orderRepository;
        this.userNotificationService = userNotificationService;
    }

    @Scheduled(fixedRate = 1000, timeUnit = TimeUnit.MILLISECONDS)
    public void runBalancingOrderBooks() {
        if (isRunning.get()) { // to prevent concurrent balancing
            return;
        }
        isRunning.set(true);
        try {
            log.debug("Start balancing");
            Map<String, Pair<Set<LimitOrder>, Set<LimitOrder>>> orders = orderBooks.fetchOrders();
            orders.entrySet().forEach(entry -> {
                log.debug("Start balancing the symbol {}", entry.getKey());
                Set<LimitOrder> buyOrders = entry.getValue().getFirst();
                Set<LimitOrder> sellOrders = entry.getValue().getSecond();
                if (buyOrders.isEmpty() || sellOrders.isEmpty()) {
                    log.debug("Empty orders in orders book = {}", entry.getKey());
                    return;
                }

                for (Iterator<LimitOrder> iterator = buyOrders.iterator(); iterator.hasNext();) {
                    LimitOrder buyOrder = iterator.next();
                    List<LimitOrder> matchedSellOrders = sellOrders.stream()
                            .filter(sellOrder -> buyOrder.getPrice() >= sellOrder.getPrice())
                            .collect(Collectors.toList());
                    for (LimitOrder sellOrder : matchedSellOrders) {
                        int volume = Math.min(buyOrder.getRest(), sellOrder.getRest());
                        createTrade(buyOrder, sellOrder, volume);
                        buyOrder.setRest(buyOrder.getRest() - volume);
                        sellOrder.setRest(sellOrder.getRest() - volume);
                        if (sellOrder.getRest() == 0) {
                            sellOrders.remove(sellOrder);
                            sellOrder.setStatus(OrderStatus.FULLFILLED);
                            sellOrder = orderRepository.save(sellOrder);
                            orderBooks.deleteOrder(sellOrder);
                            userNotificationService.sendOrderFullfilledMessage(sellOrder);
                        }
                        if (buyOrder.getRest() == 0) {
                            iterator.remove();
                            buyOrder.setStatus(OrderStatus.FULLFILLED);
                            orderRepository.save(buyOrder);
                            orderBooks.deleteOrder(buyOrder);
                            userNotificationService.sendOrderFullfilledMessage(buyOrder);
                            break;
                        }
                    }
                }
                // rest of orders mark as partfilled and return back to OrdersBook
                if (!buyOrders.isEmpty()) {
                    markOrdersAsPartFilled(buyOrders);
                }
                if (!sellOrders.isEmpty()) {
                    markOrdersAsPartFilled(sellOrders);
                }
            });

            log.debug("Stop balancing");
        } finally {
            isRunning.set(false);
        }
    }

    private void markOrdersAsPartFilled(Collection<LimitOrder> orders) {
        orders.forEach(order -> {
            order.setStatus(OrderStatus.PARTFILLED);
            orderRepository.save(order);
        });
    }

    private void createTrade(LimitOrder buyOrder, LimitOrder sellOrder, int quantity) {
        Trade trade = new Trade();
        trade.setBuyOrder(buyOrder);
        trade.setSellOrder(sellOrder);
        trade.setPrice(calcWeightedAveragePrice(buyOrder, sellOrder));
        trade.setQuantity(quantity);
        trade = ledger.save(trade);
        log.info("New trade saved - {}", trade);
    }

    private int calcWeightedAveragePrice(LimitOrder... orders) {
        int sum = 0;
        int denominator = 0;
        for (LimitOrder order : orders) {
            sum += order.getPrice() * order.getQuantity();
            denominator += order.getQuantity();
        }
        return sum / denominator;
    }
}
