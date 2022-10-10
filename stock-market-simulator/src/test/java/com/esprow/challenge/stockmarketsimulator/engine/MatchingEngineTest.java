package com.esprow.challenge.stockmarketsimulator.engine;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;

import com.esprow.challenge.stockmarketsimulator.StockMarketSimulatorApplication;
import com.esprow.challenge.stockmarketsimulator.domain.LimitOrder;
import com.esprow.challenge.stockmarketsimulator.domain.OrderDir;
import com.esprow.challenge.stockmarketsimulator.domain.Stock;
import com.esprow.challenge.stockmarketsimulator.domain.Trade;
import com.esprow.challenge.stockmarketsimulator.persistence.StockRepository;
import com.esprow.challenge.stockmarketsimulator.persistence.TradeLedger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import java.time.Instant;
import java.util.List;

@SpringBootTest(classes = StockMarketSimulatorApplication.class)
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
class MatchingEngineTest {

    private static final String STOCK_SYMBOL = "AAPL";

    @Autowired
    private MatchingEngine matchingEngine;

    @Autowired
    private OrderBooks orderBooks;

    @MockBean
    private TradeLedger tradeLedger;

    @MockBean
    private StockRepository stockRepository;

    @BeforeEach
    final void setUp() {
        Stock stock = new Stock();
        stock.setSymbol(STOCK_SYMBOL);
        Mockito.when(stockRepository.findAll())
                .thenReturn(List.of(stock));
    }

    @Test
    final void add2CrossedOrders_thenValidTradeExists() {
        LimitOrder buyOrder = new LimitOrder();
        buyOrder.setOrderDir(OrderDir.BUY);
        buyOrder.setSymbol(STOCK_SYMBOL);
        buyOrder.setPrice(1000);
        buyOrder.setQuantity(100);
        buyOrder.setRest(100);
        buyOrder.setCreated(Instant.now());

        LimitOrder sellOrder = new LimitOrder();
        sellOrder.setOrderDir(OrderDir.SELL);
        sellOrder.setSymbol(STOCK_SYMBOL);
        sellOrder.setPrice(900);
        sellOrder.setQuantity(50);
        sellOrder.setRest(50);
        sellOrder.setCreated(Instant.now());

        orderBooks.addOrder(buyOrder);
        orderBooks.addOrder(sellOrder);
        matchingEngine.runBalancingOrderBooks();

        ArgumentCaptor<Trade> argument = ArgumentCaptor.forClass(Trade.class);
        Mockito.verify(tradeLedger).save(argument.capture());
        assertThat(argument.getValue().getQuantity() == 50 && argument.getValue().getPrice() == 966);
    }

    @Test
    final void addManyCrossedOrders_thenValidTradesExist() {
        LimitOrder buyOrder = new LimitOrder();
        buyOrder.setOrderDir(OrderDir.BUY);
        buyOrder.setSymbol(STOCK_SYMBOL);
        buyOrder.setPrice(1000);
        buyOrder.setQuantity(100);
        buyOrder.setRest(100);
        buyOrder.setCreated(Instant.now());
        orderBooks.addOrder(buyOrder);

        buyOrder = new LimitOrder();
        buyOrder.setOrderDir(OrderDir.BUY);
        buyOrder.setSymbol(STOCK_SYMBOL);
        buyOrder.setPrice(1500);
        buyOrder.setQuantity(50);
        buyOrder.setRest(100);
        buyOrder.setCreated(Instant.now());
        orderBooks.addOrder(buyOrder);

        LimitOrder sellOrder = new LimitOrder();
        sellOrder.setOrderDir(OrderDir.SELL);
        sellOrder.setSymbol(STOCK_SYMBOL);
        sellOrder.setPrice(900);
        sellOrder.setQuantity(20);
        sellOrder.setRest(50);
        sellOrder.setCreated(Instant.now());
        orderBooks.addOrder(sellOrder);

        sellOrder = new LimitOrder();
        sellOrder.setOrderDir(OrderDir.SELL);
        sellOrder.setSymbol(STOCK_SYMBOL);
        sellOrder.setPrice(1000);
        sellOrder.setQuantity(50);
        sellOrder.setRest(50);
        sellOrder.setCreated(Instant.now());
        orderBooks.addOrder(sellOrder);

        matchingEngine.runBalancingOrderBooks();
        ArgumentCaptor<Trade> argument = ArgumentCaptor.forClass(Trade.class);
        Mockito.verify(tradeLedger, times(2)).save(argument.capture());
        List<Trade> trades = argument.getAllValues();
        assertThat(trades.stream()
                .filter(trade -> (trade.getPrice() == 983 && trade.getQuantity() == 20))
                .findAny()
                .isPresent());
    }
}
