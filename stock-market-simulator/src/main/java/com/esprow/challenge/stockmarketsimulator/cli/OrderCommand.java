package com.esprow.challenge.stockmarketsimulator.cli;

import com.esprow.challenge.stockmarketsimulator.domain.LimitOrder;
import com.esprow.challenge.stockmarketsimulator.domain.OrderDir;
import com.esprow.challenge.stockmarketsimulator.domain.OrderStatus;
import com.esprow.challenge.stockmarketsimulator.engine.OrderBooks;
import com.esprow.challenge.stockmarketsimulator.persistence.OrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@AllArgsConstructor
@ShellComponent
public class OrderCommand {

    private final OrderBooks orderBooks;
    private final OrderRepository orderRepository;

    @ShellMethod(value = "Add new order")
    public void add(@ShellOption(value = "-d") String dir,
            @ShellOption(value = "-p") int price,
            @ShellOption(value = "-q") int quantity,
            @ShellOption(value = "-s") String symbol,
            @ShellOption(value = "-u") String userId) {
        LimitOrder order = new LimitOrder();
        order.setOrderDir(OrderDir.valueOf(dir));
        order.setPrice(price);
        order.setQuantity(quantity);
        order.setRest(quantity);
        order.setStatus(OrderStatus.OPEN);
        order.setSymbol(symbol);
        order.setUserId(userId);
        order = orderRepository.save(order);

        orderBooks.addOrder(order);
    }
}
