package com.esprow.challenge.stockmarketsimulator.engine;

import com.esprow.challenge.stockmarketsimulator.domain.LimitOrder;
import com.esprow.challenge.stockmarketsimulator.websocket.WebSocketHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
public class UserNotificationService {

    private final WebSocketHandler webSocketHandler;
    private ExecutorService executorService = Executors.newCachedThreadPool();

    public UserNotificationService(WebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
    }

    public void sendOrderFullfilledMessage(LimitOrder order) {
        executorService.submit(() -> {
            try {
                webSocketHandler.sendMessage(order.getUserId(), formOrderFullfilledMessage(order));
            } catch (IOException e) {
                log.warn("Failed to send user message about fullfilled order-{}", order);
            }
        });
    }

    private String formOrderFullfilledMessage(LimitOrder order) {
        StringBuilder sb = new StringBuilder();
        sb.append("Your order[")
                .append(order.getId().toString())
                .append("]")
                .append(" was processed.");
        return sb.toString();
    }
}
